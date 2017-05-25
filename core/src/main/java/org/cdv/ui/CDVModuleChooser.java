package org.cdv.ui;

import org.cdv.core.CDVModule;

import java.util.Collection;

/**
 * Created by enrico on 3/10/16.
 */
public interface CDVModuleChooser {

    CDVModule show(String title);

    CDVModule show(String title, Collection<CDVModule> modules);
}
