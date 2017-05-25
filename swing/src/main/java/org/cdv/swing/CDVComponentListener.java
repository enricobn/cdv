package org.cdv.swing;

import org.cdv.core.CDVModule;

/**
 * Created by enrico on 3/9/16.
 */
public interface CDVComponentListener {

    void onAdd(CDVModule module);

    void onRemove(CDVModule module);
}
