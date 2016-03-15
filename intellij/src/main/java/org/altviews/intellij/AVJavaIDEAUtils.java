package org.altviews.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.containers.hash.*;
import com.intellij.util.containers.hash.HashSet;
import org.altviews.core.AVModuleImpl;

import java.util.*;

/**
 * Created by enrico on 3/10/16.
 */
public abstract class AVJavaIDEAUtils {
    private static final Logger logger = Logger.getInstance(AVJavaIDEAUtils.class);
    private static final PsiClass[] EMPTY_PSICLASSES = new PsiClass[0];

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
