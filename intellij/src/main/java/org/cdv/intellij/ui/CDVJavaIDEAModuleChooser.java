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
package org.cdv.intellij.ui;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.cdv.core.CDVModule;
import org.cdv.core.CDVModuleImpl;
import org.cdv.intellij.CDVJavaIDEAUtils;
import org.cdv.ui.CDVModuleChooser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CDVJavaIDEAModuleChooser implements CDVModuleChooser {
    private final Project project;

    public CDVJavaIDEAModuleChooser(Project project) {
        this.project = project;
    }

    @Override
    public CDVModule show(String title) {
        final TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project)
                .createAllProjectScopeChooser(title);
        chooser.showDialog();
        if (chooser.getSelected() == null) {
            return null;
        }
        return new CDVModuleImpl(chooser.getSelected().getQualifiedName());

    }

    @Override
    public CDVModule show(String title, Collection<CDVModule> modules) {
        final Set<String> qualifiedNames = new HashSet<>();

        for (CDVModule module : modules) {
            qualifiedNames.add(module.getFullName());
        }

        TreeFileChooser.PsiFileFilter filter = new TreeFileChooser.PsiFileFilter() {
            @Override
            public boolean accept(PsiFile psiFile) {
                final PsiClass mainClass = CDVJavaIDEAUtils.getMainClass(project, psiFile.getVirtualFile());
                if (mainClass != null) {
                    return qualifiedNames.contains(mainClass.getQualifiedName());
                } else {
                    return false;
                }
            }
        };
        final TreeFileChooser chooser = TreeClassChooserFactory.getInstance(project)
                .createFileChooser(title, null, JavaFileType.INSTANCE, filter);
        chooser.showDialog();
        if (chooser.getSelectedFile() == null) {
            return null;
        }
        final PsiClass mainClass = CDVJavaIDEAUtils.getMainClass(project, chooser.getSelectedFile().getVirtualFile());
        if (mainClass != null) {
            return new CDVModuleImpl(mainClass.getQualifiedName());
        }
        return null;
    }

}
