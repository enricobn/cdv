/**
 * Created by enrico on 3/12/16.
 */
package org.altviews.intellij.ui;

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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.altviews.core.*;
import org.altviews.intellij.AVJavaIDEAUtils;
import org.altviews.intellij.core.AVJavIdeaModuleTypeProvider;
import org.altviews.intellij.core.AVJavaIDEADependenciesFinder;
import org.altviews.intellij.core.AVJavaIDEAModuleNavigator;
import org.altviews.intellij.ui.editor.AVSwingGraph;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 3/4/16.
 */
public class AVToolWindowFactory implements ToolWindowFactory {
    private static final Logger logger = Logger.getInstance(AVToolWindowFactory.class);
    private TimedQueueThread<VirtualFile> queue;
    private Project project;
    private AVSwingGraph component;
    private ToolWindow toolWindow;

    public AVToolWindowFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull final ToolWindow toolWindow) {
        logger.info("AVToolWindowFactory.createToolWindowContent");
        this.project = project;
        this.toolWindow = toolWindow;

        component = new AVSwingGraph(
                new AVJavaIDEAModuleNavigator(project),
                new AVJavaIDEADependenciesFinder(project),
                new AVJavIdeaModuleTypeProvider(project),
                new AVJavaIDEAModuleChooser(project),
                true,
                false);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        final Content content = contentFactory.createContent(component.getComponent(), "", false);
        toolWindow.getContentManager().addContent(content);

        queue = new TimedQueueThread<>(new TimedQueueThread.ElementRunnable<VirtualFile>() {
            @Override
            public void run(final VirtualFile element) {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        refresh(element);
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
                    refreshSelectedFile();
                }
            }
        }, true);

        ToolWindowManagerEx manager = ToolWindowManagerEx.getInstanceEx(project);
        manager.addToolWindowManagerListener(new ToolWindowManagerListener() {
            @Override
            public void toolWindowRegistered(@NotNull String s) {

            }

            @Override
            public void stateChanged() {
                if (toolWindow.isVisible()) {
                    refreshSelectedFile();
                }
            }
        });

        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void beforeDocumentChange(DocumentEvent documentEvent) {

            }

            @Override
            public void documentChanged(DocumentEvent documentEvent) {
                if (!toolWindow.isVisible()) {
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
        });


        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
                queue.add(virtualFile);
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {

            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {
                queue.add(fileEditorManagerEvent.getNewFile());
            }
        });
    }

    private void refreshSelectedFile() {
        final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            final Document document = editor.getDocument();
            final VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            if (file != null) {
                queue.add(file);
            }
        }
    }

    private void refresh(VirtualFile file) {
//        logger.info("AVToolWindowFactory.refresh " + file);
        final PsiClass mainClass = AVJavaIDEAUtils.getMainClass(project, file);

        if (mainClass == null) {
            component.clear();
            return;
        }

        AVModule mainModule = new AVModuleImpl(mainClass.getQualifiedName());

        Collection<AVModule> modules = new HashSet<>();

        modules.add(mainModule);

        final AVDependenciesFinderCached finder = new AVDependenciesFinderCached(new AVJavaIDEADependenciesFinder(project));

        final Set<AVModuleDependency> dependencies = finder.getDependencies(mainModule);
        for (AVModuleDependency dependency : dependencies) {
            modules.add(dependency.getModule());
        }

        if (!modules.equals(component.getModules())) {
            component.clear();
            component.addModules(modules, finder);
        }
    }

}
