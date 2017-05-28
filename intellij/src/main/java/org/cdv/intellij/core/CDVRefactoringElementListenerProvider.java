package org.cdv.intellij.core;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.refactoring.listeners.RefactoringElementAdapter;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import org.cdv.core.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 5/25/17.
 */
public class CDVRefactoringElementListenerProvider implements RefactoringElementListenerProvider {
    private static final Logger LOGGER = Logger.getInstance(CDVRefactoringElementListenerProvider.class);
    private static final CDVGraphFileReader reader = new CDVGraphFileReader();

    @Nullable
    @Override
    public RefactoringElementListener getListener(final PsiElement oldPsiElement) {
        final String oldName;
        if (oldPsiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) oldPsiElement;
            oldName = psiClass.getQualifiedName();
        } else if (oldPsiElement instanceof PsiPackage) {
            oldName = ((PsiPackage) oldPsiElement).getQualifiedName();
        } else {
            oldName = null;
        }

        return new RefactoringElementAdapter() {
            @Override
            public void undoElementMovedOrRenamed(@NotNull PsiElement psiElement, @NotNull String s) {
                LOGGER.info("Undo moved or renamed " + oldName + " to " + psiElement + " s=" + s);
            }

            @Override
            protected void elementRenamedOrMoved(@NotNull final PsiElement psiElement) {
                LOGGER.info("Moved or renamed " + oldName + " to " + psiElement);

                if (oldName != null) {
                    for (final VirtualFile cdv : FilenameIndex.getAllFilesByExt(psiElement.getProject(), "cdv")) {
                        if (psiElement instanceof PsiClass) {
                            String newName = ((PsiClass) psiElement).getQualifiedName();
                            try {
                                renameClass(psiElement.getProject(), oldName, newName, cdv);
                            } catch (Exception e) {
                                LOGGER.info(e);
                            }
                        } else if (psiElement instanceof PsiPackage) {
                            String newName = ((PsiPackage) psiElement).getQualifiedName();
                            try {
                                renamePackage(psiElement.getProject(), oldName, newName, cdv);
                            } catch (Exception e) {
                                LOGGER.info(e);
                            }
                        }
                    }
                }
            }

        };
    }

    private void renamePackage(Project project, String oldName, String newName, VirtualFile cdv) throws Exception {
        final CDVGraph cdvGraph = reader.read(cdv.getInputStream());
        for (final CDVModule cdvModule : cdvGraph.getModules()) {
            if (cdvModule.getFullName().startsWith(oldName + ".")) {
                final CDVGraph newGraph = replaceModule(cdvGraph, cdvModule,
                        newName + cdvModule.getFullName().substring(oldName.length()));
                saveGraph(project, cdv, newGraph);
            }
        }
    }

    private void renameClass(final Project project, String oldName, final String newName, final VirtualFile cdv) throws Exception {
        final CDVGraph cdvGraph = reader.read(cdv.getInputStream());
        for (final CDVModule cdvModule : cdvGraph.getModules()) {
            if (cdvModule.getFullName().equals(oldName)) {
                final CDVGraph newGraph = replaceModule(cdvGraph, cdvModule, newName);

                saveGraph(project, cdv, newGraph);
            }
        }
    }

    @NotNull
    private static CDVGraph replaceModule(final CDVGraph cdvGraph, final CDVModule moduleToReplace, final String newFullName) {
        return new CDVGraph() {
                        @Override
                        public Set<CDVModule> getModules() {
                            Set<CDVModule> modules = new HashSet<>(cdvGraph.getModules());
                            modules.remove(moduleToReplace);
                            modules.add(new CDVModuleImpl(newFullName));
                            return modules;
                        }
                    };
    }

    private static void saveGraph(Project project, final VirtualFile cdv, final CDVGraph newGraph) {
        CDVIDEAUtils.runUndoableWriteActionCommand(project, new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = FileDocumentManager.getInstance().getDocument(cdv);
                    String text = CDVGraphUtils.toString(newGraph);
                    document.setText(text);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, "save", null);
    }


}
