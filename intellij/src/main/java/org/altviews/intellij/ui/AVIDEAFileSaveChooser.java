package org.altviews.intellij.ui;

import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.altviews.ui.AVFileSaveChooser;

import java.io.File;

/**
 * Created by enrico on 3/17/16.
 */
public class AVIDEAFileSaveChooser implements AVFileSaveChooser {
    private final Project project;

    public AVIDEAFileSaveChooser(Project project) {
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
