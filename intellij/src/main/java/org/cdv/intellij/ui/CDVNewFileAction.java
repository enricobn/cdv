/*
 * Copyright (c) 2017 Enrico Benedetti
 *
 * This file is part of Class dependency viewer (CDV).
 *
 * CDV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CDV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CDV.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cdv.intellij.ui;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.cdv.core.CDVConstants;
import org.cdv.core.CDVGraphFileWriter;
import org.cdv.intellij.ui.editor.CDVIDEAFileType;
import org.jetbrains.annotations.NotNull;

public class CDVNewFileAction extends CreateElementActionBase {
    private static final Logger logger = Logger.getInstance(CDVNewFileAction.class);

    public CDVNewFileAction() {
        super("CDV", CDVConstants.PLUGIN_NAME, CDVIDEAFileType.INSTANCE.getIcon());
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory psiDirectory) {
        final MyInputValidator validator = new MyInputValidator(project, psiDirectory);
        Messages.showInputDialog(project, "File name", "Create New " + CDVConstants.PLUGIN_NAME, Messages.getQuestionIcon(), "", validator);

        return validator.getCreatedElements();
    }

    @NotNull
    @Override
    protected PsiElement[] create(String s, PsiDirectory psiDirectory) throws Exception {
        final PsiFile file = PsiFileFactory.getInstance(psiDirectory.getProject())
                .createFileFromText(s + "." + CDVIDEAFileType.DEFAULT_EXTENSION, CDVIDEAFileType.INSTANCE,
                        CDVGraphFileWriter.empty());
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
        return "newCDVFile";
    }

}
