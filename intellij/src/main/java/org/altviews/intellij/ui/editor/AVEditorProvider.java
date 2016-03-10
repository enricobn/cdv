package org.altviews.intellij.ui.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by enrico on 3/8/16.
 */
public class AVEditorProvider extends FileTypeFactory implements FileEditorProvider {
    @Override
    public boolean accept(Project project, VirtualFile virtualFile) {
        if (virtualFile.getExtension() == null) {
            return false;
        }
        return virtualFile.getExtension().equals(AVFileType.DEFAULT_EXTENSION);
    }

    @NotNull
    @Override
    public FileEditor createEditor(Project project, VirtualFile virtualFile) {
        try {
            return new AVFileEditor(project, virtualFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disposeEditor(@NotNull FileEditor fileEditor) {

    }

    @NotNull
    @Override
    public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new AVFileEditor.AVEditorState();
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
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(AVFileType.INSTANCE, "");
    }
}
