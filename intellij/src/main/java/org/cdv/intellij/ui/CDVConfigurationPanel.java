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
package org.cdv.intellij.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.cdv.intellij.core.CDVConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;

public class CDVConfigurationPanel extends JPanel implements Configurable, DocumentListener {
    private final Project project;
    private final JTextArea includes = new JTextArea();
    private final JTextArea excludes = new JTextArea();
    private boolean modified;

    public static void main(String[] args) {
        JFrame frm = new JFrame();
        frm.setSize(400, 400);
        frm.getContentPane().add(new CDVConfigurationPanel(null));
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }

    public CDVConfigurationPanel(Project project) {
        this.project = project;

//        includes.setRows(10);
//        includes.setColumns(30);
        includes.setMaximumSize(new Dimension(300, 300));

//        excludes.setRows(10);
//        excludes.setColumns(30);
        excludes.setMaximumSize(new Dimension(300, 300));

        setLayout(new GridBagLayout());

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets.left = 10;
            gbc.insets.top = 10;
            gbc.insets.bottom = 5;
            JLabel includesLabel = new JLabel("Include:");
            add(includesLabel, gbc);

            gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridy = 1;
            gbc.weighty = 1.0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets.left = 10;
            gbc.insets.right = 10;
            add(includes, gbc);
        }

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridy = 2;
            gbc.insets.left = 10;
            gbc.insets.top = 10;
            gbc.insets.bottom = 5;
            JLabel excludesLabel = new JLabel("Exclude:");
            add(excludesLabel, gbc);

            gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridy = 3;
            gbc.weighty = 1.0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets.left = 10;
            gbc.insets.bottom = 10;
            gbc.insets.right = 10;
            add(excludes, gbc);
        }
    }

    private CDVConfiguration.State getState() {
        CDVConfiguration config = CDVConfiguration.getConfig(project);
        return config.getState();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Alt views";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return this;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        CDVConfiguration.State configuration = getState();

        String[] split = includes.getText().split("\n");
        java.util.List<String> includesList = new ArrayList<>();

        for (String s : split) {
            if (!s.trim().isEmpty()) {
                includesList.add(s);
            }
        }
        configuration.setIncludes(includesList);

        split = excludes.getText().split("\n");
        java.util.List<String> excludesList = new ArrayList<>();
        for (String s : split) {
            if (!s.trim().isEmpty()) {
                excludesList.add(s);
            }
        }
        configuration.setExcludes(excludesList);
        modified = false;
    }

    @Override
    public void reset() {
        CDVConfiguration.State configuration = getState();

        StringBuilder sb = new StringBuilder();
        for (String s : configuration.getIncludes()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(s);
        }
        includes.setText(sb.toString());

        sb = new StringBuilder();
        for (String s : configuration.getExcludes()) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(s);
        }
        excludes.setText(sb.toString());

        includes.getDocument().addDocumentListener(this);
        excludes.getDocument().addDocumentListener(this);
    }

    @Override
    public void disposeUIResources() {
        modified = true;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        modified = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        modified = true;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
