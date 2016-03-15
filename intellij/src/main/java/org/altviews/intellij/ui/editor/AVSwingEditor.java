package org.altviews.intellij.ui.editor;

import org.altviews.core.*;
import org.altviews.ui.AVClassChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by enrico on 3/8/16.
 */
public class AVSwingEditor extends JPanel {
    private final AVClassChooser classChooser;
    private final AVSwingGraph component;

    public AVSwingEditor(AVClassChooser classChooser, AVModuleNavigator navigator, AVDependenciesFinder finder,
                         AVModuleTypeProvider typeProvider, boolean horizontal) {
        this.classChooser = classChooser;
        component = new AVSwingGraph(navigator, finder, typeProvider,
                horizontal, true);

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

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(component.getComponent(), gbc);
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
