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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
