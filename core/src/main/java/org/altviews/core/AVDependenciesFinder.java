package org.altviews.core;

import java.util.Set;

/**
 * Created by enrico on 3/10/16.
 */
public interface AVDependenciesFinder {

    Set<AVModuleDependency> getDependencies(AVModule module);

}
