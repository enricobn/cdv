/**
 * Created by enrico on 3/5/16.
 */

import javax.swing.*;

import org.altviews.core.*;
import org.altviews.intellij.ui.editor.AVSwingEditor;
import org.altviews.ui.AVModuleChooser;
import org.altviews.ui.AVFileSaveChooser;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class HelloWorld extends JFrame
{

    /**
     *
     */
    private static final long serialVersionUID = -2707712944901661771L;

    public HelloWorld()
    {
        super("Hello, World!");
        final AVModule m5 = new AVModuleDummy("org.altviews.5");
        final AVModule m4 = new AVModuleDummy("org.altviews.4");
        final AVModule m2 = new AVModuleDummy("org.altviews.core.2", new AVModule[]{m4,m5});
        final AVModule m3 = new AVModuleDummy("org.altviews.3");
        final AVModule m1 = new AVModuleDummy("org.altviews.ui.1", new AVModule[]{m2, m3});

        AVModuleChooser moduleChooser = new AVModuleChooser() {
            @Override
            public AVModule show(String title) {
                return m1;
            }

            @Override
            public AVModule show(String title, Collection<AVModule> modules) {
                return m1;
            }

        };
        AVModuleNavigator navigator = new AVModuleNavigator() {
            @Override
            public void navigateTo(AVModule module) {
            }
        };
        AVDependenciesFinder finder = new AVDependenciesFinder() {
            @Override
            public Set<AVModuleDependency> getDependencies(AVModule module) {
                return ((AVModuleDummy)module).getDependencies();
            }
        };

        AVModuleTypeProvider typeProvider = new AVModuleTypeProvider() {
            @Override
            public AVModuleType getType(AVModule module) {
                if (module.equals(m1)) {
                    return AVModuleType.Interface;
                }
                return AVModuleType.Class;
            }
        };
        AVFileSaveChooser saveChooser = new AVFileSaveChooser() {
            @Override
            public File choose(String message, String suffix) {
                return new File("test." + suffix);
            }
        };

        AVNamespaceNavigator nsNavigator = new AVNamespaceNavigator() {
            @Override
            public void navigateTo(String namespace) {

            }
        };

        getContentPane().add(new AVSwingEditor(moduleChooser, navigator, finder, typeProvider,
                saveChooser, nsNavigator, false));
    }

    public static void main(String[] args)
    {
        HelloWorld frame = new HelloWorld();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    }

    private static class AVModuleDummy extends AVModuleImpl implements Serializable {
        private final Set<AVModuleDependency> dependencies = new HashSet<>();

        private AVModuleDummy(String fullName) {
            super(fullName);
        }

        private AVModuleDummy(String fullName, AVModule[] deps) {
            super(fullName);
            for (AVModule dep : deps) {
                dependencies.add(new AVModuleDependencyDummy(dep));
            }
        }

        public Set<AVModuleDependency> getDependencies() {
            return dependencies;
        }

    }

    private static class AVModuleDependencyDummy implements AVModuleDependency, Serializable {
        private final AVModule module;

        private AVModuleDependencyDummy(AVModule module) {
            this.module = module;
        }

        @Override
        public AVModule getModule() {
            return module;
        }

        @Override
        public String toString() {
            return getModule().toString();
        }
    }

}
