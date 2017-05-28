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
package org.cdv.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.cdv.core.CDVModule;
import org.cdv.core.CDVModuleNavigator;
import org.cdv.intellij.CDVJavaIDEAUtils;

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
