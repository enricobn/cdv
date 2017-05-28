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

import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.cdv.ui.CDVFileSaveChooser;

import java.io.File;

public class CDVIDEAFileSaveChooser implements CDVFileSaveChooser {
    private final Project project;

    public CDVIDEAFileSaveChooser(Project project) {
        this.project = project;
    }

    public File choose(String message, String suffix) {
        final FileSaverDialog dialog = FileChooserFactory.getInstance().createSaveFileDialog(
                new FileSaverDescriptor(message, "", suffix), project);
        final VirtualFile baseDir = project.getBaseDir();
        final VirtualFileWrapper save = dialog.save(baseDir, "");
        if (save != null) {
            return save.getFile();
//            field.setText(FileUtil.toSystemDependentName(save.getFile().getAbsolutePath()));
//            if (fieldToUpdate.getText().isEmpty()) {
//                fieldToUpdate.setText(Utils.replaceLastSubString(field.getText(), suffixToReplace, suffix));
//            }
        }
        return null;
    }
}
