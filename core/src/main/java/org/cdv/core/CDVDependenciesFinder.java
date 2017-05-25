package org.cdv.core;

import java.util.Set;

/**
 * Created by enrico on 3/10/16.
 */
public interface CDVDependenciesFinder {

    Set<CDVModuleDependency> getDependencies(CDVModule module);

}
