package org.altviews.intellij.ui;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.project.Project;
import org.altviews.core.AVModule;
import org.altviews.intellij.core.AVJavaIDEAModule;
import org.altviews.ui.AVClassChooser;

/**
 * Created by enrico on 3/10/16.
 */
public class AVJavaIDEAClassChooser implements AVClassChooser {
    private final Project project;

    public AVJavaIDEAClassChooser(Project project) {
        this.project = project;
    }

    @Override
    public AVModule show(String title) {
        final TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project).createAllProjectScopeChooser(title);
        chooser.showDialog();
        if (chooser.getSelected() == null) {
            return null;
        }
        return new AVJavaIDEAModule(chooser.getSelected());

    }
}
