/**
 * Created by enrico on 3/12/16.
 */
package org.altviews.intellij.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.*;
import com.intellij.ui.content.*;
import org.altviews.core.AVModule;
import org.altviews.core.AVModuleDependency;
import org.altviews.core.AVModuleImpl;
import org.altviews.intellij.AVJavaIDEAUtils;
import org.altviews.intellij.core.AVJavIdeaModuleTypeProvider;
import org.altviews.intellij.core.AVJavaIDEADependenciesFinder;
import org.altviews.intellij.core.AVJavaIDEAModuleNavigator;
import org.altviews.intellij.ui.editor.AVGraphSwingComponent;

import java.util.Collection;
import java.util.Set;

/**
 * Created by enrico on 3/4/16.
 */
public class AVToolWindowFactory implements ToolWindowFactory {
    private static final Logger logger = Logger.getInstance(AVToolWindowFactory.class);
    private Project project;
    private ToolWindow toolWindow;
    private AVGraphSwingComponent component;

    public AVToolWindowFactory() {
    }

    @Override
    public void createToolWindowContent(final Project project, final ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        component = new AVGraphSwingComponent(
                new AVJavaIDEAClassChooser(project),
                new AVJavaIDEAModuleNavigator(project),
                new AVJavaIDEADependenciesFinder(project),
                new AVJavIdeaModuleTypeProvider(project), true);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(component, "", false);
        toolWindow.getContentManager().addContent(content);



//        toolWindow.getContentManager().addContentManagerListener(new ContentManagerListener() {
//            @Override
//            public void contentAdded(ContentManagerEvent contentManagerEvent) {
//
//            }
//
//            @Override
//            public void contentRemoved(ContentManagerEvent contentManagerEvent) {
//
//            }
//
//            @Override
//            public void contentRemoveQuery(ContentManagerEvent contentManagerEvent) {
//
//            }
//
//            @Override
//            public void selectionChanged(ContentManagerEvent contentManagerEvent) {
//
//            }
//        });

//        final MessageBusConnection connection = project.getMessageBus().connect();
//        connection.subscribe(UpdatedFilesListener.UPDATED_FILES, new UpdatedFilesListener() {
//            @Override void consume(Set<String> files) {
////                show("On bus VCS update: ${files.join(",")}")
//            }
//        });

//        PsiDocumentManager.getInstance(project).addListener(new PsiDocumentManager.Listener() {
//            @Override
//            public void documentCreated(Document document, PsiFile psiFile) {
//
//            }
//
//            @Override
//            public void fileCreated(PsiFile psiFile, Document document) {
//
//            }
//        });

//        FileEditorManager.getInstance(project).

//        ProjectManager.getInstance().addProjectManagerListener(project, new ProjectManagerListener() {
//            @Override
//            public void projectOpened(Project project) {
//
//            }
//
//            @Override
//            public boolean canCloseProject(Project project) {
//                return false;
//            }
//
//            @Override
//            public void projectClosed(Project project) {
//
//            }
//
//            @Override
//            public void projectClosing(Project project) {
//
//            }
//        });

//        CommonActionsManager.getInstance().

//        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());




//        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
//            @Override
//            public void beforeDocumentChange(DocumentEvent documentEvent) {
//
//            }
//
//            @Override
//            public void documentChanged(DocumentEvent documentEvent) {
//
//            }
//        });



        EditorFactory.getInstance().getEventMulticaster().addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(SelectionEvent selectionEvent) {
//                VirtualFile file = FileDocumentManager.getInstance().getFile(selectionEvent.getEditor().getDocument());
//                final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
//
//                component.clear();
//
//                if (psiFile != null && psiFile instanceof  PsiClassOwner) {
//                    final PsiClass[] psiClasses = ((PsiClassOwner) psiFile).getClasses();
//                    if (psiClasses.length > 0) {
//                        final AVModuleImpl module = new AVModuleImpl(psiClasses[0].getQualifiedName());
//                        component.addModule(module);
//                        final Set<AVModuleDependency> dependencies = new AVJavaIDEADependenciesFinder(project).getDependencies(module);
//                        for (AVModuleDependency dependency : dependencies) {
//                            component.addModule(dependency.getModule());
//                        }
//                    }
//
//                }
            }
        });


        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpened(FileEditorManager fileEditorManager, VirtualFile virtualFile) {
                refresh(virtualFile);
            }

            @Override
            public void fileClosed(FileEditorManager fileEditorManager, VirtualFile virtualFile) {

            }

            @Override
            public void selectionChanged(FileEditorManagerEvent fileEditorManagerEvent) {
                refresh(fileEditorManagerEvent.getNewFile());
            }
        });

        ActionManager.getInstance().addAnActionListener(new AnActionListener() {
            @Override
            public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {

            }

            @Override
            public void afterActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
//                if (anAction instanceof SelectInAction) {
//                    VirtualFile file = DataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
//                    if (file != null) {
//                        refresh(file);
//                    } else {
//                        component.clear();
//                    }
//                } else {
//                    component.clear();
//                }
            }

            @Override
            public void beforeEditorTyping(char c, DataContext dataContext) {

            }
        });
    }

    private void refresh(VirtualFile file) {
        logger.info("AVToolWindowFactory.refresh " + file);
        component.clear();

        final PsiClass mainClass = AVJavaIDEAUtils.getMainClass(project, file);

        if (mainClass == null) {
            return;
        }

        AVModule mainModule = new AVModuleImpl(mainClass.getQualifiedName());

        component.addModule(mainModule);

        final Set<AVModuleDependency> dependencies = new AVJavaIDEADependenciesFinder(project)
                .getDependencies(mainModule);
        for (AVModuleDependency dependency : dependencies) {
            component.addModule(dependency.getModule());
        }

    }

}
