package org.altviews.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.altviews.core.AVModule;
import org.altviews.core.AVModuleNavigator;
import org.altviews.intellij.AVJavaIDEAUtils;

/**
 * Created by enrico on 3/10/16.
 */
public class AVJavaIDEAModuleNavigator implements AVModuleNavigator {
    private final Project project;

    public AVJavaIDEAModuleNavigator(Project project) {
        this.project = project;
    }

    @Override
    public void navigateTo(AVModule module) {
        final PsiClass psiClass = AVJavaIDEAUtils.getPsiClass(project, module.getFullName());
        if (psiClass != null) {
            psiClass.navigate(true);
        }
    }
}
