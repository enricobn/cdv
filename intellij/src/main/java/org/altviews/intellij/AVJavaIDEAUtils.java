package org.altviews.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

/**
 * Created by enrico on 3/10/16.
 */
public abstract class AVJavaIDEAUtils {

    public static PsiClass getPsiClass(Project project, String fullName) {
        String name;
        int pos = fullName.lastIndexOf('.');
        if (pos < 0) {
            name = fullName;
        } else {
            name = fullName.substring(pos + 1);
        }
        final PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(name,
                GlobalSearchScope.allScope(project));
        for (PsiClass psiClass : psiClasses) {
            if (fullName.equals(psiClass.getQualifiedName())) {
                return psiClass;
            }
        }
        return null;
    }
}
