package org.cdv.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by enrico on 3/16/16.
 */
public class CDVDependenciesFinderCached implements CDVDependenciesFinder {
    private final CDVDependenciesFinder finder;
    private final ConcurrentHashMap<CDVModule,Set<CDVModuleDependency>> cache = new ConcurrentHashMap<>();

    public CDVDependenciesFinderCached(CDVDependenciesFinder finder) {
        this.finder = finder;
    }

    @Override
    public Set<CDVModuleDependency> getDependencies(CDVModule module) {
        if (!cache.containsKey(module)) {
            cache.put(module, finder.getDependencies(module));
        }
        return cache.get(module);
    }
}
