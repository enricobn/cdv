package org.altviews.ui;

import org.altviews.core.AVModule;

import java.util.Collection;

/**
 * Created by enrico on 3/10/16.
 */
public interface AVModuleChooser {

    AVModule show(String title);

    AVModule show(String title, Collection<AVModule> modules);
}
