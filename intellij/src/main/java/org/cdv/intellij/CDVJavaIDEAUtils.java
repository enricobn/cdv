/*
 * Copyright (c) 2017 Enrico Benedetti
 *
 * This file is part of Class dependency viewer (CDV).
 *
 * CDV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CDV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CDV.  If not, see <http://www.gnu.org/licenses/>.
 */
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
