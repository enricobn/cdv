package org.altviews.intellij.core;

import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiClassUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.altviews.core.AVModule;
import org.altviews.core.AVModuleDependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 3/4/16.
 */
public class AVJavaIDEAModule implements AVModule {
    private final PsiClass psiClass;

    public AVJavaIDEAModule(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    @Override
    public Set<AVModuleDependency> getDependencies() {
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
            final AVJavaIDEAModule module = new AVJavaIDEAModule(dep);
            result.add(new AVJavaIDEADependency(module));
        }
    }

    @Override
    public String getSimpleName() {
        return psiClass.getName();
    }

    @Override
    public String getFullName() {
        return psiClass.getQualifiedName();
    }

    @Override
    public String toString() {
        return getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AVJavaIDEAModule that = (AVJavaIDEAModule) o;

        return !(psiClass != null ? !psiClass.equals(that.psiClass) : that.psiClass != null);

    }

    @Override
    public int hashCode() {
        return psiClass != null ? psiClass.hashCode() : 0;
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }
}
