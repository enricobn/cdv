package org.altviews.swing;

import org.altviews.core.*;
import org.altviews.ui.AVFileSaveChooser;
import org.altviews.ui.AVModuleChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by enrico on 3/8/16.
 */
public class AVSwingEditor extends AVSwingContainer {
    private final AVModuleChooser moduleChooser;

    public AVSwingEditor(AVModuleChooser moduleChooser, AVModuleNavigator navigator, AVDependenciesFinder finder,
                         AVModuleTypeProvider typeProvider, final AVFileSaveChooser saveChooser,
                         AVNamespaceNavigator nsNavigator,
                         boolean horizontal) {
        super(navigator, finder, typeProvider, moduleChooser, nsNavigator, saveChooser, horizontal, true);

        this.moduleChooser = moduleChooser;

        addButton(getClass().getResource("/org/altviews/swing/addModule.png"), "Add module", "Add module",
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addFile();
                    }
                }, 0);
    }

    private void addFile() {
        final AVModule module = moduleChooser.show("Add module");
        if (module != null) {
            addModule(module);
        }
    }

}
