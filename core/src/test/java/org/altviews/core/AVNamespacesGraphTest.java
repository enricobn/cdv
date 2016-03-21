package org.altviews.core;

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
public class AVNamespacesGraphTest extends TestCase {
    @Test
    public void test() throws Exception {
        AVGraph graph = mock(AVGraph.class);
        Set<AVModule> modules = new HashSet<>();
        final AVModuleImpl C1 = new AVModuleImpl("org.altviews.C1");
        modules.add(C1);
        final AVModuleImpl C11 = new AVModuleImpl("org.altviews.C11");
        modules.add(C11);
        final AVModuleImpl C2 = new AVModuleImpl("org.altviews.core.C2");
        modules.add(C2);
        final AVModuleImpl C3 = new AVModuleImpl("org.altviews.ui.C3");
        modules.add(C3);
        when(graph.getModules()).thenReturn(modules);

        AVDependenciesFinder finder = mock(AVDependenciesFinder.class);
        Set<AVModuleDependency> C3Dep = new HashSet<>();
        C3Dep.add(new AVModuleDependencyImpl(C1));
        C3Dep.add(new AVModuleDependencyImpl(C11));
        C3Dep.add(new AVModuleDependencyImpl(C2));
        when(finder.getDependencies(C3)).thenReturn(C3Dep);

        Set<AVModuleDependency> C2Dep = new HashSet<>();
        C2Dep.add(new AVModuleDependencyImpl(C1));
        when(finder.getDependencies(C2)).thenReturn(C2Dep);

        Set<AVModuleDependency> C1Dep = new HashSet<>();
        C1Dep.add(new AVModuleDependencyImpl(C11));
        when(finder.getDependencies(C1)).thenReturn(C1Dep);

        AVNamespacesGraph avNamespacesGraph = new AVNamespacesGraph(graph, finder);

        Set<String> namespaces = new HashSet<>();
        namespaces.add("org.altviews");
        namespaces.add("org.altviews.core");
        namespaces.add("org.altviews.ui");

        assertEquals(namespaces, avNamespacesGraph.getNamespaces());

        Set<String> dependencies = new HashSet<>();
        dependencies.add("org.altviews");
        dependencies.add("org.altviews.core");
        assertEquals(dependencies, avNamespacesGraph.getDependencies("org.altviews.ui"));

        dependencies = new HashSet<>();
        dependencies.add("org.altviews");
        assertEquals(dependencies, avNamespacesGraph.getDependencies("org.altviews.core"));

        assertEquals(Collections.emptySet(), avNamespacesGraph.getDependencies("org.altviews"));
    }
}