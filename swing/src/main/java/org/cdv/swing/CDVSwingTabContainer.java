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
package org.cdv.swing;

import org.cdv.core.*;
import org.cdv.ui.CDVModuleChooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.util.Collection;

public class CDVSwingTabContainer extends JTabbedPane {
    private final CDVModuleNavigator navigator;
    private final CDVDependenciesFinder finder;
    private final CDVModuleTypeProvider typeProvider;
    private final CDVModuleChooser moduleChooser;
    private final CDVSwingGraph swingGraph;
    private final CDVNamespaceNavigator nsNavigator;
    private final CDVSwingNamespacesGraph nsSwingGraph;

    public CDVSwingTabContainer(CDVModuleNavigator navigator, CDVDependenciesFinder finder,
                                CDVModuleTypeProvider typeProvider, CDVModuleChooser moduleChooser,
                                CDVNamespaceNavigator nsNavigator, boolean horizontal, boolean editable) {
        this.navigator = navigator;
        this.finder = finder;
        this.typeProvider = typeProvider;
        this.moduleChooser = moduleChooser;
        this.nsNavigator = nsNavigator;
        swingGraph = new CDVSwingGraph(navigator, finder, typeProvider, moduleChooser, horizontal, editable);
        nsSwingGraph = new CDVSwingNamespacesGraph(nsNavigator, finder, horizontal);
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

    public void exportToPNG(File file) {
        if (getSelectedIndex() == 0) {
            swingGraph.exportToPNG(file);
        } else {
            nsSwingGraph.exportToPNG(file);
        }
    }

    public void addModule(CDVModule module) {
        swingGraph.addModule(module);
        if (getSelectedIndex() == 1) {
            nsSwingGraph.setGraph(swingGraph);
        }
    }

    public void addModules(Collection<CDVModule> modules) {
        swingGraph.addModules(modules);
        if (getSelectedIndex() == 1) {
            nsSwingGraph.setGraph(swingGraph);
        }
    }

    public void addListener(CDVComponentListener listener) {
        swingGraph.addListener(listener);
    }

    public CDVGraph getGraph() {
        return swingGraph;
    }

    public void clear() {
        swingGraph.clear();
        if (getSelectedIndex() == 1) {
            nsSwingGraph.clear();
        }

    }

    public Collection<CDVModule> getModules() {
        return swingGraph.getModules();
    }

    public void addModules(Collection<CDVModule> modules, CDVDependenciesFinderCached finder) {
        swingGraph.addModules(modules, finder);
        if (getSelectedIndex() == 1) {
            nsSwingGraph.setGraph(swingGraph);
        }
    }
}
