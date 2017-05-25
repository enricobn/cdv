package org.cdv.intellij.ui.editor;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by enrico on 3/8/16.
 */
public class CDVIDEAFileType implements FileType {
    public static final CDVIDEAFileType INSTANCE = new CDVIDEAFileType();
    public static final String DEFAULT_EXTENSION = "cdv";
    private final Icon icon;

    private CDVIDEAFileType() {
        icon = IconLoader.getIcon("/org/cdv/intellij/ui/fileType.png");
    }

    @NotNull
    @Override
    public String getName() {
        return "CDV";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Class dependency viewer";
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
