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

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CDVNamespacesGraphTest {
    @Test
    public void test() throws Exception {
        CDVGraph graph = mock(CDVGraph.class);
        Set<CDVModule> modules = new HashSet<>();
        final CDVModuleImpl C1 = new CDVModuleImpl("org.cdv.C1");
        modules.add(C1);
        final CDVModuleImpl C11 = new CDVModuleImpl("org.cdv.C11");
        modules.add(C11);
        final CDVModuleImpl C2 = new CDVModuleImpl("org.cdv.core.C2");
        modules.add(C2);
        final CDVModuleImpl C3 = new CDVModuleImpl("org.cdv.ui.C3");
        modules.add(C3);
        when(graph.getModules()).thenReturn(modules);

        CDVDependenciesFinder finder = mock(CDVDependenciesFinder.class);
        Set<CDVModuleDependency> C3Dep = new HashSet<>();
        C3Dep.add(new CDVModuleDependencyImpl(C1));
        C3Dep.add(new CDVModuleDependencyImpl(C11));
        C3Dep.add(new CDVModuleDependencyImpl(C2));
        when(finder.getDependencies(C3)).thenReturn(C3Dep);

        Set<CDVModuleDependency> C2Dep = new HashSet<>();
        C2Dep.add(new CDVModuleDependencyImpl(C1));
        when(finder.getDependencies(C2)).thenReturn(C2Dep);

        Set<CDVModuleDependency> C1Dep = new HashSet<>();
        C1Dep.add(new CDVModuleDependencyImpl(C11));
        when(finder.getDependencies(C1)).thenReturn(C1Dep);

        CDVNamespacesGraph cdvNamespacesGraph = new CDVNamespacesGraph(graph, finder);

        Set<String> namespaces = new HashSet<>();
        namespaces.add("org.cdv");
        namespaces.add("org.cdv.core");
        namespaces.add("org.cdv.ui");

        assertEquals(namespaces, cdvNamespacesGraph.getNamespaces());

        Set<String> dependencies = new HashSet<>();
        dependencies.add("org.cdv");
        dependencies.add("org.cdv.core");
        assertEquals(dependencies, cdvNamespacesGraph.getDependencies("org.cdv.ui"));

        dependencies = new HashSet<>();
        dependencies.add("org.cdv");
        assertEquals(dependencies, cdvNamespacesGraph.getDependencies("org.cdv.core"));

        assertEquals(Collections.emptySet(), cdvNamespacesGraph.getDependencies("org.cdv"));
    }
}