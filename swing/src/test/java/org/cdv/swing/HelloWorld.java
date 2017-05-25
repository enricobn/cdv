package org.cdv.swing; /**
 * Created by enrico on 3/5/16.
 */

import org.cdv.core.*;
import org.cdv.ui.CDVFileSaveChooser;
import org.cdv.ui.CDVModuleChooser;

import javax.swing.*;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HelloWorld extends JFrame
{

    /**
     *
     */
    private static final long serialVersionUID = -2707712944901661771L;

    public HelloWorld()
    {
        super("Hello, World!");
        final CDVModule m5 = new CDVModuleDummy("org.cdv.5");
        final CDVModule m4 = new CDVModuleDummy("org.cdv.4");
        final CDVModule m2 = new CDVModuleDummy("org.cdv.core.2", new CDVModule[]{m4,m5});
        final CDVModule m3 = new CDVModuleDummy("org.cdv.3");
        final CDVModule m1 = new CDVModuleDummy("org.cdv.ui.1", new CDVModule[]{m2, m3});

        CDVModuleChooser moduleChooser = new CDVModuleChooser() {
            @Override
            public CDVModule show(String title) {
                return m1;
            }

            @Override
            public CDVModule show(String title, Collection<CDVModule> modules) {
                return m1;
            }

        };
        CDVModuleNavigator navigator = new CDVModuleNavigator() {
            @Override
            public void navigateTo(CDVModule module) {
            }
        };
        CDVDependenciesFinder finder = new CDVDependenciesFinder() {
            @Override
            public Set<CDVModuleDependency> getDependencies(CDVModule module) {
                return ((CDVModuleDummy)module).getDependencies();
            }
        };

        CDVModuleTypeProvider typeProvider = new CDVModuleTypeProvider() {
            @Override
            public CDVModuleType getType(CDVModule module) {
                if (module.equals(m1)) {
                    return CDVModuleType.Interface;
                }
                return CDVModuleType.Class;
            }
        };
        CDVFileSaveChooser saveChooser = new CDVFileSaveChooser() {
            @Override
            public File choose(String message, String suffix) {
                return new File("test." + suffix);
            }
        };

        CDVNamespaceNavigator nsNavigator = new CDVNamespaceNavigator() {
            @Override
            public void navigateTo(String namespace) {

            }
        };

        getContentPane().add(new CDVSwingEditor(moduleChooser, navigator, finder, typeProvider,
                saveChooser, nsNavigator, false));
    }

    public static void main(String[] args)
    {
        HelloWorld frame = new HelloWorld();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    }

    private static class CDVModuleDummy extends CDVModuleImpl implements Serializable {
        private final Set<CDVModuleDependency> dependencies = new HashSet<>();

        private CDVModuleDummy(String fullName) {
            super(fullName);
        }

        private CDVModuleDummy(String fullName, CDVModule[] deps) {
            super(fullName);
            for (CDVModule dep : deps) {
                dependencies.add(new CDVModuleDependencyDummy(dep));
            }
        }

        public Set<CDVModuleDependency> getDependencies() {
            return dependencies;
        }

    }

    private static class CDVModuleDependencyDummy implements CDVModuleDependency, Serializable {
        private final CDVModule module;

        private CDVModuleDependencyDummy(CDVModule module) {
            this.module = module;
        }

        @Override
        public CDVModule getModule() {
            return module;
        }

        @Override
        public String toString() {
            return getModule().toString();
        }
    }

}
