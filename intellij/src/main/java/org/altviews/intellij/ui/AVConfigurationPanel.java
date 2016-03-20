package org.altviews.intellij.ui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Attribute;
import org.altviews.intellij.core.AVConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by enrico on 3/19/16.
 */
public class AVConfigurationPanel extends JPanel implements Configurable, DocumentListener {
    private final Project project;
    private final JLabel includesLabel = new JLabel("Include:");
    private final JTextArea includes = new JTextArea();
    private final JLabel excludesLabel = new JLabel("Exclude:");
    private final JTextArea excludes = new JTextArea();
    private boolean modified;


    public static void main(String[] args) {
        JFrame frm = new JFrame();
        frm.setSize(400, 400);
        frm.getContentPane().add(new AVConfigurationPanel(null));
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }

    public AVConfigurationPanel(Project project) {
        this.project = project;

        includes.setRows(10);
        includes.setColumns(30);

        excludes.setRows(10);
        excludes.setColumns(30);

        setLayout(new GridBagLayout());
        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            add(includesLabel, gbc);

            gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.BOTH;
            add(includes, gbc);
        }
        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridy = 2;
            gbc.insets.top = 10;
            add(excludesLabel, gbc);

            gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.BOTH;
            add(excludes, gbc);
        }
    }

    private AVConfiguration.State getState() {
        AVConfiguration config = AVConfiguration.getConfig(project);
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
        AVConfiguration.State configuration = getState();

        String[] split = includes.getText().split("\n");
        java.util.List<String> includesList = new ArrayList<>();

        for (String s : split) {
            if (!s.trim().isEmpty()) {
                includesList.add(s);
            }
        }
        configuration.includes = includesList;

        split = excludes.getText().split("\n");
        java.util.List<String> excludesList = new ArrayList<>();
        for (String s : split) {
            if (!s.trim().isEmpty()) {
                excludesList.add(s);
            }
        }
        configuration.excludes = excludesList;
        modified = false;
    }

    @Override
    public void reset() {
        AVConfiguration.State configuration = getState();

        StringBuilder sb = new StringBuilder();
        for (String s : configuration.includes) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(s);
        }
        includes.setText(sb.toString());

        sb = new StringBuilder();
        for (String s : configuration.excludes) {
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
