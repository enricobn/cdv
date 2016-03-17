package org.altviews.core;

import java.io.File;
import java.util.Set;

/**
 * Created by enrico on 3/9/16.
 */
public interface AVGraph {

    Set<AVModule> getModules();

    void clear();

    void exportToSVG(File file);

}
