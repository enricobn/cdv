package org.altviews.swing;

import org.altviews.core.AVModule;

/**
 * Created by enrico on 3/9/16.
 */
public interface AVComponentListener {

    void onAdd(AVModule module);

    void onRemove(AVModule module);
}
