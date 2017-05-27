package org.cdv.swing;

import javax.swing.*;

/**
 * Created by enrico on 5/27/17.
 */
public class CDVSwingUtilities {

    public static void invokeAndWait(final Runnable doRun) {
        if (SwingUtilities.isEventDispatchThread()) {
            doRun.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(doRun);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
