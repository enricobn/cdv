package org.altviews.intellij.ui.editor;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 * Created by enrico on 3/8/16.
 */
public class AVFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(AVFileType.INSTANCE, AVFileType.DEFAULT_EXTENSION);
    }
}
