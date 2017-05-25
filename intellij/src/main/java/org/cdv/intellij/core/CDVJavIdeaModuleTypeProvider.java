package org.cdv.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.cdv.core.CDVModule;
import org.cdv.core.CDVModuleType;
import org.cdv.core.CDVModuleTypeProvider;
import org.cdv.intellij.CDVJavaIDEAUtils;

/**
 * Created by enrico on 3/11/16.
 */
public class CDVJavIdeaModuleTypeProvider implements CDVModuleTypeProvider {
    private final Project project;

    public CDVJavIdeaModuleTypeProvider(Project project) {
        this.project = project;
    }

    @Override
    public CDVModuleType getType(CDVModule module) {
        PsiClass psiClass = CDVJavaIDEAUtils.getPsiClass(project, module.getFullName());
        if (psiClass != null) {
            if (psiClass.isInterface()) {
                return CDVModuleType.Interface;
            }
        }
        return CDVModuleType.Class;
    }
}
