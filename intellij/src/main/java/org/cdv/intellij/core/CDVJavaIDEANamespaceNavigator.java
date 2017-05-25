package org.cdv.intellij.core;

import com.intellij.openapi.project.Project;
import org.cdv.core.CDVNamespaceNavigator;

/**
 * Created by enrico on 3/10/16.
 */
public class CDVJavaIDEANamespaceNavigator implements CDVNamespaceNavigator {
    private final Project project;

    public CDVJavaIDEANamespaceNavigator(Project project) {
        this.project = project;
    }

    @Override
    public void navigateTo(String namespace) {
        // TODO
    }
}
