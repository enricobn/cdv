package org.altviews.core;

import java.util.Collection;
import java.util.Set;

/**
 * Created by enrico on 3/4/16.
 */
public interface AVModule {

    Set<AVModuleDependency> getDependencies();

    String getSimpleName();

    String getFullName();

}
