package org.altviews.intellij.ui.editor;

import org.altviews.core.AVModule;

/**
 * Created by enrico on 3/9/16.
 */
public interface AVFileEditorPanelListener {

    void onAdd(AVModule module);

    void onRemove(AVModule module);
}
