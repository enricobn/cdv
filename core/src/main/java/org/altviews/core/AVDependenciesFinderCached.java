package org.altviews.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by enrico on 3/16/16.
 */
public class AVDependenciesFinderCached implements AVDependenciesFinder {
    private final AVDependenciesFinder finder;
    private final ConcurrentHashMap<AVModule,Set<AVModuleDependency>> cache = new ConcurrentHashMap<>();

    public AVDependenciesFinderCached(AVDependenciesFinder finder) {
        this.finder = finder;
    }

    @Override
    public Set<AVModuleDependency> getDependencies(AVModule module) {
        if (!cache.containsKey(module)) {
            cache.put(module, finder.getDependencies(module));
        }
        return cache.get(module);
    }
}
