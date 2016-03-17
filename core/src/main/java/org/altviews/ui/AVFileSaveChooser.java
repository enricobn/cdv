package org.altviews.ui;

import java.io.File;

/**
 * Created by enrico on 3/17/16.
 */
public interface AVFileSaveChooser {

    File choose(String message, String suffix);

}
