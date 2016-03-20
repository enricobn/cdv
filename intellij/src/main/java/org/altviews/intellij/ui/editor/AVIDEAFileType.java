package org.altviews.intellij.ui.editor;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by enrico on 3/8/16.
 */
public class AVIDEAFileType implements FileType {
    public static final AVIDEAFileType INSTANCE = new AVIDEAFileType();
    public static final String DEFAULT_EXTENSION = "av";
    private final Icon icon;

    private AVIDEAFileType() {
        icon = IconLoader.getIcon("/org/altviews/intellij/ui/fileType.png");
    }

    @NotNull
    @Override
    public String getName() {
        return "Alt view";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Alternative views";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile virtualFile, @NotNull byte[] bytes) {
        return "UTF-8";
    }
}
