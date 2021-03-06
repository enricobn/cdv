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
package org.cdv.intellij.ui.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * DumbAware: indicates that the editor can be showed when indexed are not ready
 * then in the editor itself ({@link CDVIDEAFileEditor}) the loading phase is done
 * inside DumbService.runWhenSmart
 */
public class CDVIDEAEditorProvider extends FileTypeFactory implements FileEditorProvider,DumbAware {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if (virtualFile.getExtension() == null) {
            return false;
        }
        return virtualFile.getExtension().equals(CDVIDEAFileType.DEFAULT_EXTENSION);
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new CDVIDEAFileEditor(project, virtualFile);
    }

    @Override
    public void disposeEditor(@NotNull FileEditor fileEditor) {

    }

    @NotNull
    @Override
    public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new CDVIDEAFileEditor.AVEditorState();
    }

    @Override
    public void writeState(@NotNull FileEditorState fileEditorState, @NotNull Project project, @NotNull Element element) {

    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "AV";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(CDVIDEAFileType.INSTANCE, "");
    }
}
