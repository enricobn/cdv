package org.cdv.intellij.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.refactoring.listeners.RefactoringElementAdapter;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import org.cdv.core.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 5/25/17.
 */
public class CDVRefactoringElementListenerProvider implements RefactoringElementListenerProvider {
    private static final Logger LOGGER = Logger.getInstance(CDVRefactoringElementListenerProvider.class);
    private static final CDVGraphFileReader reader = new CDVGraphFileReader();
    private static final CDVGraphFileWriter writer = new CDVGraphFileWriter();

    @Nullable
    @Override
    public RefactoringElementListener getListener(final PsiElement oldPsiElement) {
        final String oldName;
        if (oldPsiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) oldPsiElement;
            oldName = psiClass.getQualifiedName();
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

                final String newName;

                if (psiElement instanceof PsiClass) {
                    newName = ((PsiClass) psiElement).getQualifiedName();
                } else {
                    newName = null;
                }

                if (oldName != null && newName != null) {
                    for (final VirtualFile cdv : FilenameIndex.getAllFilesByExt(psiElement.getProject(), "cdv")) {
                        try {
                            renameClass(psiElement.getProject(), oldName, newName, cdv);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LOGGER.info(cdv.toString());
                    }
                }
            }

        };
    }

    private void renameClass(final Project project, String oldName, final String newName, final VirtualFile cdv) throws Exception {
        final CDVGraph cdvGraph = reader.read(cdv.getInputStream());
        for (final CDVModule cdvModule : cdvGraph.getModules()) {
            if (cdvModule.getFullName().equals(oldName)) {
                final CDVGraph newGraph = new CDVGraph() {
                    @Override
                    public Set<CDVModule> getModules() {
                        Set<CDVModule> modules = new HashSet<>(cdvGraph.getModules());
                        modules.remove(cdvModule);
                        modules.add(new CDVModuleImpl(newName));
                        return modules;
                    }
                };

//                ApplicationManager.getApplication().invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
                        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                            @Override
                            public void run() {
                                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Document document = FileDocumentManager.getInstance().getDocument(cdv);
                                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                                            writer.write(newGraph, os);
                                            os.close();
                                            document.setText(os.toString("UTF-8"));
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }, "save", CDVRefactoringElementListenerProvider.this);
                            }
                        });
//                    }
//                });
            }
        }
    }

}
