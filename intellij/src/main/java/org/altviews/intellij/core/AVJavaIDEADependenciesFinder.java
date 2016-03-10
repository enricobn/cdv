package org.altviews.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTypesUtil;
import org.altviews.core.*;
import org.altviews.intellij.AVJavaIDEAUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 3/10/16.
 */
public class AVJavaIDEADependenciesFinder implements AVDependenciesFinder {
    private final Project project;

    public AVJavaIDEADependenciesFinder(Project project) {
        this.project = project;
    }

    @Override
    public Set<AVModuleDependency> getDependencies(AVModule module) {
        PsiClass psiClass = AVJavaIDEAUtils.getPsiClass(project, module.getFullName());
        Set<AVModuleDependency> result = new HashSet<>();

        for (PsiClassType ancestor : psiClass.getExtendsListTypes()) {
            addDependency(result, ancestor.resolve());
        }

        for (PsiClassType iFace : psiClass.getImplementsListTypes()) {
            addDependency(result, iFace.resolve());
        }

        for (PsiField field : psiClass.getAllFields()) {
            addDependency(result, PsiTypesUtil.getPsiClass(field.getType()));
        }

        for (PsiMethod method : psiClass.getAllMethods()) {
            addDependency(result, PsiTypesUtil.getPsiClass(method.getReturnType()));
            for (PsiParameter parameter : method.getParameterList().getParameters()) {
                addDependency(result, PsiTypesUtil.getPsiClass(parameter.getType()));
            }
        }
        return result;
    }

    private static void addDependency(Set<AVModuleDependency> result, PsiClass dep) {
        if (dep == null) {
            return;
        }
        final PsiFile[] files = FilenameIndex.getFilesByName(dep.getProject(), dep.getName() + ".java",
                GlobalSearchScope.projectScope(dep.getProject()));
        if (files.length > 0) {
            final AVModule module = new AVModuleImpl(dep.getQualifiedName());
            result.add(new AVModuleDependencyImpl(module));
        }
    }
}
