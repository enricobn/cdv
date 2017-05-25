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

/**
 * Created by enrico on 3/24/16.
 */
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
