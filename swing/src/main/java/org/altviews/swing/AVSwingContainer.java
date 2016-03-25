package org.altviews.swing;

import org.altviews.core.*;
import org.altviews.ui.AVFileSaveChooser;
import org.altviews.ui.AVModuleChooser;

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
public class AVSwingContainer extends JPanel {
    private final JToolBar toolBar = new JToolBar();
    private final AVSwingTabContainer tabContainer;

    public AVSwingContainer(final AVModuleNavigator navigator, final AVDependenciesFinder finder,
                            final AVModuleTypeProvider typeProvider, final AVModuleChooser moduleChooser,
                            final AVNamespaceNavigator nsNavigator, final AVFileSaveChooser saveChooser,
                            final boolean horizontal,
                            final boolean editable) {
        super(new BorderLayout());
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.PAGE_START);

        tabContainer = new AVSwingTabContainer(navigator, finder, typeProvider, moduleChooser,
                nsNavigator, horizontal, editable);

        addButton(getClass().getResource("/org/altviews/swing/exportToSVG.png"), "Export to .svg", "Export to .svg",
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = saveChooser.choose("SVG", "svg");
                if (file != null) {
                    tabContainer.exportToSVG(file);
                }
            }
        }, -1);

        addButton(getClass().getResource("/org/altviews/swing/exportToPNG.png"), "Export to .png", "Export to .png",
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

    public AVGraph getGraph() {
        return tabContainer.getGraph();
    }

    public void addModule(AVModule module) {
        tabContainer.addModule(module);
    }

    public void addModules(Collection<AVModule> modules) {
        tabContainer.addModules(modules);
    }

    public void addListener(AVComponentListener listener) {
        tabContainer.addListener(listener);
    }

    public void clear() {
        tabContainer.clear();
    }

    public Collection<AVModule> getModules() {
        return tabContainer.getModules();
    }

    public void addModules(Collection<AVModule> modules, AVDependenciesFinderCached finder) {
        tabContainer.addModules(modules, finder);
    }
}
