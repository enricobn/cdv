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
import org.cdv.ui.CDVFileSaveChooser;
import org.cdv.ui.CDVModuleChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Collection;

public class CDVSwingContainer extends JPanel {
    private final JToolBar toolBar = new JToolBar();
    private final CDVSwingTabContainer tabContainer;

    public CDVSwingContainer(final CDVModuleNavigator navigator, final CDVDependenciesFinder finder,
                             final CDVModuleTypeProvider typeProvider, final CDVModuleChooser moduleChooser,
                             final CDVNamespaceNavigator nsNavigator, final CDVFileSaveChooser saveChooser,
                             final boolean horizontal,
                             final boolean editable) {
        super(new BorderLayout());
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.PAGE_START);

        tabContainer = new CDVSwingTabContainer(navigator, finder, typeProvider, moduleChooser,
                nsNavigator, horizontal, editable);

        addButton(getClass().getResource("/org/cdv/swing/exportToSVG.png"), "Export to .svg", "Export to .svg",
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = saveChooser.choose("SVG", "svg");
                if (file != null) {
                    tabContainer.exportToSVG(file);
                }
            }
        }, -1);

        addButton(getClass().getResource("/org/cdv/swing/exportToPNG.png"), "Export to .png", "Export to .png",
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        File file = saveChooser.choose("PNG", "png");
                        if (file != null) {
                            tabContainer.exportToPNG(file);
                        }
                    }
                }, -1);

        add(tabContainer, BorderLayout.CENTER);
    }

    public void addButton(URL imageURL,
                               //String actionCommand,
                               String toolTipText,
                               String altText,
                               ActionListener actionListener,
                               int index) {
        //Create and initialize the button.
        JButton button = new JButton();
//        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(actionListener);

        if (imageURL != null) {
            button.setIcon(new ImageIcon(imageURL, altText));
        }

        toolBar.add(button, index);
    }

    public CDVGraph getGraph() {
        return tabContainer.getGraph();
    }

    public void addModule(CDVModule module) {
        tabContainer.addModule(module);
    }

    public void addModules(Collection<CDVModule> modules) {
        tabContainer.addModules(modules);
    }

    public void addListener(CDVComponentListener listener) {
        tabContainer.addListener(listener);
    }

    public void clear() {
        tabContainer.clear();
    }

    public Collection<CDVModule> getModules() {
        return tabContainer.getModules();
    }

    public void addModules(Collection<CDVModule> modules, CDVDependenciesFinderCached finder) {
        tabContainer.addModules(modules, finder);
    }
}
