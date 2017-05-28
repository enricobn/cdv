/*
 * Copyright (c) 2017 Enrico Benedetti
 *
 * This file is part of Class dependency viewer (CDV).
 *
 * CDV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CDV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CDV.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cdv.core;

import java.util.*;

public final class CDVNamespacesGraph {
    private final Map<String,Set<CDVModule>> namespaces = new HashMap<>();
    private final CDVDependenciesFinder finder;

    public CDVNamespacesGraph(CDVGraph graph, CDVDependenciesFinder finder) {
        this.finder = finder;
        for (CDVModule module : graph.getModules()) {
            Set<CDVModule> modules;
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
            for (CDVModule module : namespaces.get(namespace)) {
                dependencies.addAll(getDependencies(module));
            }

        }
        return dependencies;
    }

    private Set<String> getDependencies(CDVModule module) {
        Set<String> namespaces = new HashSet<>();
        for (CDVModuleDependency dependency : finder.getDependencies(module)) {
            if (!module.getNamespace().equals(dependency.getModule().getNamespace())) {
                namespaces.add(dependency.getModule().getNamespace());
            }
        }
        return namespaces;
    }
}
