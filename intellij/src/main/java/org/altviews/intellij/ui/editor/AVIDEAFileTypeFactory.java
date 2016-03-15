package org.altviews.intellij.ui.editor;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by enrico on 3/8/16.
 */
public class AVIDEAFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(AVIDEAFileType.INSTANCE, AVIDEAFileType.DEFAULT_EXTENSION);
    }
}
