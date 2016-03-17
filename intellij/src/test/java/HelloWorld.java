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
        final AVModule second = new AVModuleDummy("org.altviews.SecondClass");
        final AVModule third = new AVModuleDummy("org.altviews.ThirdClass");
        final AVModule first = new AVModuleDummy("org.altviews.FirstClass", new AVModule[]{second, third});

        AVModuleChooser moduleChooser = new AVModuleChooser() {
            @Override
            public AVModule show(String title) {
                return first;
            }

            @Override
            public AVModule show(String title, Collection<AVModule> modules) {
                return first;
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
                if (module.equals(first)) {
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
        getContentPane().add(new AVSwingEditor(moduleChooser, navigator, finder, typeProvider,
                saveChooser, false));
    }

    public static void main(String[] args)
    {
        HelloWorld frame = new HelloWorld();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    }

    private static class AVModuleDummy implements AVModule, Serializable {
        private final String fullName;
        private final Set<AVModuleDependency> dependencies = new HashSet<>();

        private AVModuleDummy(String fullName) {
            this.fullName = fullName;
        }

        private AVModuleDummy(String fullName, AVModule[] deps) {
            this.fullName = fullName;
            for (AVModule dep : deps) {
                dependencies.add(new AVModuleDependencyDummy(dep));
            }
        }

        public Set<AVModuleDependency> getDependencies() {
            return dependencies;
        }

        @Override
        public String getSimpleName() {
            return fullName.substring(fullName.lastIndexOf('.') + 1);
        }

        @Override
        public String getFullName() {
            return fullName;
        }

        @Override
        public String toString() {
            return getSimpleName();
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
