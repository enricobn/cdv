package org.altviews.swing;

import org.altviews.core.*;
import org.altviews.ui.AVModuleChooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.util.Collection;

/**
 * Created by enrico on 3/21/16.
 */
public class AVSwingTabContainer extends JTabbedPane {
    private final AVModuleNavigator navigator;
    private final AVDependenciesFinder finder;
    private final AVModuleTypeProvider typeProvider;
    private final AVModuleChooser moduleChooser;
    private final AVSwingGraph swingGraph;
    private final AVNamespaceNavigator nsNavigator;
    private final AVSwingNamespacesGraph nsSwingGraph;

    public AVSwingTabContainer(AVModuleNavigator navigator, AVDependenciesFinder finder,
                               AVModuleTypeProvider typeProvider, AVModuleChooser moduleChooser,
                               AVNamespaceNavigator nsNavigator, boolean horizontal, boolean editable) {
        this.navigator = navigator;
        this.finder = finder;
        this.typeProvider = typeProvider;
        this.moduleChooser = moduleChooser;
        this.nsNavigator = nsNavigator;
        swingGraph = new AVSwingGraph(navigator, finder, typeProvider, moduleChooser, horizontal, editable);
        nsSwingGraph = new AVSwingNamespacesGraph(nsNavigator, finder, horizontal);
        addTab("Modules", swingGraph.getComponent());
        addTab("Namespaces", nsSwingGraph.getComponent());
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                if (index == 1) {
                    nsSwingGraph.setGraph(swingGraph);
                }
            }
        });
    }

    public void exportToSVG(File file) {
        if (getSelectedIndex() == 0) {
            swingGraph.exportToSVG(file);
        } else {
            nsSwingGraph.exportToSVG(file);
        }
    }

    public void exportToPng(File file) {
        if (getSelectedIndex() == 0) {
            swingGraph.exportToPng(file);
        } else {
            nsSwingGraph.exportToPng(file);
        }
    }

    public void addModule(AVModule module) {
        swingGraph.addModule(module);
        if (getSelectedIndex() == 1) {
            nsSwingGraph.setGraph(swingGraph);
        }
    }

    public void addModules(Collection<AVModule> modules) {
        swingGraph.addModules(modules);
        if (getSelectedIndex() == 1) {
            nsSwingGraph.setGraph(swingGraph);
        }
    }

    public void addListener(AVComponentListener listener) {
        swingGraph.addListener(listener);
    }

    public AVGraph getGraph() {
        return swingGraph;
    }

    public void clear() {
        swingGraph.clear();
        if (getSelectedIndex() == 1) {
            nsSwingGraph.clear();
        }

    }

    public Collection<AVModule> getModules() {
        return swingGraph.getModules();
    }

    public void addModules(Collection<AVModule> modules, AVDependenciesFinderCached finder) {
        swingGraph.addModules(modules, finder);
        if (getSelectedIndex() == 1) {
            nsSwingGraph.setGraph(swingGraph);
        }
    }
}
