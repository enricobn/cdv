package org.altviews.core;

import java.util.*;

/**
 * Created by enrico on 3/21/16.
 */
public final class AVNamespacesGraph {
    private final Map<String,Set<AVModule>> namespaces = new HashMap<>();
    private final AVDependenciesFinder finder;

    public AVNamespacesGraph(AVGraph graph, AVDependenciesFinder finder) {
        this.finder = finder;
        for (AVModule module : graph.getModules()) {
            Set<AVModule> modules;
            if (namespaces.containsKey(module.getNamespace())) {
                modules = namespaces.get(module.getNamespace());
            } else {
                modules = new HashSet<>();
                namespaces.put(module.getNamespace(), modules);
            }
            modules.add(module);
        }
    }

    public Set<String> getNamespaces() {
        return namespaces.keySet();
    }

    public Set<String> getDependencies(String namespace) {
        Set<String> dependencies = new HashSet<>();
        if (namespaces.containsKey(namespace)) {
            for (AVModule module : namespaces.get(namespace)) {
                dependencies.addAll(getDependencies(module));
            }

        }
        return dependencies;
    }

    private Set<String> getDependencies(AVModule module) {
        Set<String> namespaces = new HashSet<>();
        for (AVModuleDependency dependency : finder.getDependencies(module)) {
            if (!module.getNamespace().equals(dependency.getModule().getNamespace())) {
                namespaces.add(dependency.getModule().getNamespace());
            }
        }
        return namespaces;
    }
}
