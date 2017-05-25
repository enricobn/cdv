/**
 * Created by enrico on 3/12/16.
 */
package org.cdv.intellij.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.cdv.core.*;
import org.cdv.intellij.CDVJavaIDEAUtils;
import org.cdv.intellij.core.CDVJavIdeaModuleTypeProvider;
import org.cdv.intellij.core.CDVJavaIDEADependenciesFinder;
import org.cdv.intellij.core.CDVJavaIDEAModuleNavigator;
import org.cdv.intellij.core.CDVJavaIDEANamespaceNavigator;
import org.cdv.swing.CDVSwingContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 3/4/16.
 */
public class CDVToolWindowFactory implements ToolWindowFactory {
    private static final Logger logger = Logger.getInstance(CDVToolWindowFactory.class);

    public CDVToolWindowFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull final ToolWindow toolWindow) {
        logger.info("AVToolWindowFactory.createToolWindowContent");

        final CDVSwingContainer component = new CDVSwingContainer(
                new CDVJavaIDEAModuleNavigator(project),
                new CDVJavaIDEADependenciesFinder(project),
                new CDVJavIdeaModuleTypeProvider(project),
                new CDVJavaIDEAModuleChooser(project),
                new CDVJavaIDEANamespaceNavigator(project),
                new CDVIDEAFileSaveChooser(project),
                true,
                false);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        final Content content = contentFactory.createContent(component, "", false);
        toolWindow.getContentManager().addContent(content);

        final TimedQueueThread<VirtualFile> queue = new TimedQueueThread<>(new TimedQueueThread.ElementRunnable<VirtualFile>() {
            @Override
            public void run(final VirtualFile element) {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        refresh(project, component, element);
                    }
                });
            }
        }, 1000);

        queue.start();

        toolWindow.getActivation().processOnDone(new Runnable() {
            @Override
            public void run() {
                logger.info("AVToolWindowFactory.onDone");
                if (toolWindow.isVisible()) {
                    refreshSelectedFile(project, component, queue);
                }
            }
        }, true);

        final DocumentListener documentListener = new DocumentListener() {
            @Override
            public void beforeDocumentChange(DocumentEvent documentEvent) {

            }

            @Override
            public void documentChanged(DocumentEvent documentEvent) {
                if (!isVisible(project, toolWindow)) {
                    return;
                }
                final VirtualFile file = FileDocumentManager.getInstance().getFile(
                        documentEvent.getDocument());
                PsiDocumentManager.getInstance(project).performForCommittedDocument(documentEvent.getDocument(), new Runnable() {
                    @Override
                    public void run() {
                        queue.add(file);
                    }
                });
            }
        };

        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(documentListener);

        final FileEditorManagerListener fileEditorManagerListener = new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
                if (isVisible(project, toolWindow)) {
                    queue.add(virtualFile);
                }
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {

            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {
                if (!isVisible(project, toolWindow)) {
                    return;
                }
                final VirtualFile file = fileEditorManagerEvent.getNewFile();
                if (file == null) {
                    component.clear();
                } else {
                    queue.add(file);
                }
            }
        };

        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileEditorManagerListener);

        final ToolWindowManagerListener toolWindowManagerListener = new ToolWindowManagerListener() {
            @Override
            public void toolWindowRegistered(@NotNull String s) {

            }

            @Override
            public void stateChanged() {
                if (isVisible(project, toolWindow)) {
                    refreshSelectedFile(project, component, queue);
                }
            }
        };

        final ToolWindowManagerEx manager = ToolWindowManagerEx.getInstanceEx(project);

        manager.addToolWindowManagerListener(toolWindowManagerListener);

        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerListener() {
            @Override
            public void projectOpened(Project project) {

            }

            @Override
            public boolean canCloseProject(Project project) {
                return true;
            }

            @Override
            public void projectClosed(Project pr) {
                if (pr.equals(project)) {
                    logger.info("AVToolWindowFactory project closed");
                    manager.removeToolWindowManagerListener(toolWindowManagerListener);
                    EditorFactory.getInstance().getEventMulticaster().removeDocumentListener(documentListener);
                    queue.stop();
                    ProjectManager.getInstance().removeProjectManagerListener(this);
                }
            }

            @Override
            public void projectClosing(Project project) {

            }
        });
    }

    private boolean isVisible(@NotNull final Project project, @NotNull final ToolWindow toolWindow) {
        // We have to check if our tool window is still registered, as
        // otherwise it will raise an exception when project is closed.
        if (project.isDisposed() || ToolWindowManagerEx.getInstanceEx(project).getToolWindow("Alt view") == null) {
            return false;
        }
        return toolWindow.isAvailable() && toolWindow.isVisible();
    }

    private void refreshSelectedFile(@NotNull final Project project, @NotNull CDVSwingContainer component,
                                     @NotNull final TimedQueueThread<VirtualFile> queue) {
        final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            final Document document = editor.getDocument();
            final VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            if (file == null) {
                component.clear();
            } else {
                queue.add(file);
            }
        }
    }

    private void refresh(@NotNull final Project project, @NotNull CDVSwingContainer component, VirtualFile file) {
//        logger.info("AVToolWindowFactory.refresh " + file);
        final PsiClass mainClass = CDVJavaIDEAUtils.getMainClass(project, file);

        if (mainClass == null) {
            component.clear();
            return;
        }

        CDVModule mainModule = new CDVModuleImpl(mainClass.getQualifiedName());

        Collection<CDVModule> modules = new HashSet<>();

        modules.add(mainModule);

        final CDVDependenciesFinderCached finder = new CDVDependenciesFinderCached(new CDVJavaIDEADependenciesFinder(project));

        final Set<CDVModuleDependency> dependencies = finder.getDependencies(mainModule);
        for (CDVModuleDependency dependency : dependencies) {
            modules.add(dependency.getModule());
        }

        if (!modules.equals(component.getModules())) {
            component.clear();
            component.addModules(modules, finder);
        }
    }

}
