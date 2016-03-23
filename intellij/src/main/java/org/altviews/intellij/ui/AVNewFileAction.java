package org.altviews.intellij.ui;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.altviews.core.AVGraphFileWriter;
import org.altviews.intellij.ui.editor.AVIDEAFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by enrico on 3/20/16.
 */
public class AVNewFileAction extends CreateElementActionBase {
    private static final Logger logger = Logger.getInstance(AVNewFileAction.class);

    public AVNewFileAction() {
        super("Alt view", "Alt view", AVIDEAFileType.INSTANCE.getIcon());
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory psiDirectory) {
        final MyInputValidator validator = new MyInputValidator(project, psiDirectory);
        Messages.showInputDialog(project, "New Alt view", "New Alt view", Messages.getQuestionIcon(), "", validator);

        final PsiElement[] elements = validator.getCreatedElements();
        return elements;
    }

    @NotNull
    @Override
    protected PsiElement[] create(String s, PsiDirectory psiDirectory) throws Exception {
        final PsiFile file = PsiFileFactory.getInstance(psiDirectory.getProject())
                .createFileFromText(s + "." + AVIDEAFileType.DEFAULT_EXTENSION, AVIDEAFileType.INSTANCE,
                        AVGraphFileWriter.empty());
        return new PsiElement[]{psiDirectory.add(file)};
    }

    @Override
    protected String getErrorTitle() {
        return null;
    }

    @Override
    protected String getCommandName() {
        return null;
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, String s) {
        return "newAVFile";
    }

}
