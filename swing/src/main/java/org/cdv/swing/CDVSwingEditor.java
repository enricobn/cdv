package org.cdv.swing;

import org.cdv.core.*;
import org.cdv.ui.CDVFileSaveChooser;
import org.cdv.ui.CDVModuleChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by enrico on 3/8/16.
 */
public class CDVSwingEditor extends CDVSwingContainer {
    private final CDVModuleChooser moduleChooser;

    public CDVSwingEditor(CDVModuleChooser moduleChooser, CDVModuleNavigator navigator, CDVDependenciesFinder finder,
                          CDVModuleTypeProvider typeProvider, final CDVFileSaveChooser saveChooser,
                          CDVNamespaceNavigator nsNavigator,
                          boolean horizontal) {
        super(navigator, finder, typeProvider, moduleChooser, nsNavigator, saveChooser, horizontal, true);

        this.moduleChooser = moduleChooser;

        addButton(getClass().getResource("/org/cdv/swing/addModule.png"), "Add module", "Add module",
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addModule();
                    }
                }, 0);
    }

    private void addModule() {
        final CDVModule module = moduleChooser.show("Add module");
        if (module != null) {
            addModule(module);
        }
    }

}
