package org.cdv.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

/**
 * Created by enrico on 3/10/16.
 */
public abstract class CDVJavaIDEAUtils {
    private static final Logger logger = Logger.getInstance(CDVJavaIDEAUtils.class);

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

    public static PsiClass getMainClass(Project project, VirtualFile file) {
        final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

        if (psiFile != null && psiFile instanceof PsiClassOwner) {
            for (PsiClass psiClass : ((PsiClassOwner) psiFile).getClasses()) {
                if (psiClass.getContainingClass() == null) {
                    return psiClass;
                }
            }
        }
        return null;
    }
}
