package org.cdv.ui;

import java.io.File;

/**
 * Created by enrico on 3/17/16.
 */
public interface CDVFileSaveChooser {

    File choose(String message, String suffix);

}
