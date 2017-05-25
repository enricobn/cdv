package org.cdv.core;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by enrico on 3/21/16.
 */
public class CDVNamespacesGraphTest extends TestCase {
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