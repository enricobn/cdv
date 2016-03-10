package org.altviews.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.altviews.core.AVModule;
import org.altviews.core.AVModuleProvider;

/**
 * Created by enrico on 3/9/16.
 */
public class AVJavaIDEAModuleProvider implements AVModuleProvider {
    private final Project project;

    public AVJavaIDEAModuleProvider(Project project) {
        this.project = project;
    }

    @Override
    public AVModule getModule(String fullClassName) {
        String name;
        int pos = fullClassName.lastIndexOf('.');
        if (pos < 0) {
            name = fullClassName;
        } else {
            name = fullClassName.substring(pos + 1);
        }
        final PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(name,
                GlobalSearchScope.allScope(project));
        for (PsiClass psiClass : psiClasses) {
            if (fullClassName.equals(psiClass.getQualifiedName())) {
                return new AVJavaIDEAModule(psiClass);
            }
        }
        return null;
    }

}
