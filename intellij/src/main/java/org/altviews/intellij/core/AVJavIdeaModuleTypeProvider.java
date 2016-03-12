package org.altviews.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.altviews.core.AVModule;
import org.altviews.core.AVModuleType;
import org.altviews.core.AVModuleTypeProvider;
import org.altviews.intellij.AVJavaIDEAUtils;

/**
 * Created by enrico on 3/11/16.
 */
public class AVJavIdeaModuleTypeProvider implements AVModuleTypeProvider {
    private final Project project;

    public AVJavIdeaModuleTypeProvider(Project project) {
        this.project = project;
    }

    @Override
    public AVModuleType getType(AVModule module) {
        PsiClass psiClass = AVJavaIDEAUtils.getPsiClass(project, module.getFullName());
        if (psiClass != null) {
            if (psiClass.isInterface()) {
                return AVModuleType.Interface;
            }
        }
        return AVModuleType.Class;
    }
}
