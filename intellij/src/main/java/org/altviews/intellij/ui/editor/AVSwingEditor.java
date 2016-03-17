package org.altviews.intellij.ui.editor;

import org.altviews.core.*;
import org.altviews.ui.AVClassChooser;
import org.altviews.ui.AVFileSaveChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by enrico on 3/8/16.
 */
public class AVSwingEditor extends JPanel {
    private final AVClassChooser classChooser;
    private final AVSwingGraph component;

    public AVSwingEditor(AVClassChooser classChooser, AVModuleNavigator navigator, AVDependenciesFinder finder,
                         AVModuleTypeProvider typeProvider, final AVFileSaveChooser saveChooser, boolean horizontal) {
        this.classChooser = classChooser;
        component = new AVSwingGraph(navigator, finder, typeProvider,
                horizontal, true);

        {
            JButton addFileButton = new JButton("Add");
            addFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addFile();
                }
            });

            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 0;
            add(addFileButton, gbc);
        }

        {
            JButton exportButton = new JButton("export to svg");
            exportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File file = saveChooser.choose("SVG", "svg");
                    if (file != null) {
                        component.exportToSVG(file);
                    }
                }
            });

            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 0;
            add(exportButton, gbc);
        }

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            add(component.getComponent(), gbc);
        }
    }

    public AVGraph getGraph() {
        return component;
    }

    private void addFile() {
        final AVModule module = classChooser.show("Add class");
        if (module != null) {
            component.addModule(module);
        }
    }

    public void addModule(AVModule module) {
        component.addModule(module);
    }

    public void addListener(AVFileEditorComponentListener listener) {
        component.addListener(listener);
    }
}