package org.cdv.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.cdv.core.CDVModule;
import org.cdv.core.CDVModuleNavigator;
import org.cdv.intellij.CDVJavaIDEAUtils;

/**
 * Created by enrico on 3/10/16.
 */
public class CDVJavaIDEAModuleNavigator implements CDVModuleNavigator {
    private final Project project;

    public CDVJavaIDEAModuleNavigator(Project project) {
        this.project = project;
    }

    @Override
    public void navigateTo(CDVModule module) {
        final PsiClass psiClass = CDVJavaIDEAUtils.getPsiClass(project, module.getFullName());
        if (psiClass != null) {
            psiClass.navigate(true);
        }
    }
}
