package org.altviews.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.altviews.core.AVModule;
import org.altviews.core.AVModuleNavigator;
import org.altviews.core.AVNamespaceNavigator;
import org.altviews.intellij.AVJavaIDEAUtils;

/**
 * Created by enrico on 3/10/16.
 */
public class AVJavaIDEANamespaceNavigator implements AVNamespaceNavigator {
    private final Project project;

    public AVJavaIDEANamespaceNavigator(Project project) {
        this.project = project;
    }

    @Override
    public void navigateTo(String namespace) {
        // TODO
    }
}
