package org.altviews.intellij.ui;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.altviews.core.AVModule;
import org.altviews.core.AVModuleImpl;
import org.altviews.intellij.AVJavaIDEAUtils;
import org.altviews.ui.AVModuleChooser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 3/10/16.
 */
public class AVJavaIDEAModuleChooser implements AVModuleChooser {
    private final Project project;

    public AVJavaIDEAModuleChooser(Project project) {
        this.project = project;
    }

    @Override
    public AVModule show(String title) {
        final TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project)
                .createAllProjectScopeChooser(title);
        chooser.showDialog();
        if (chooser.getSelected() == null) {
            return null;
        }
        return new AVModuleImpl(chooser.getSelected().getQualifiedName());

    }

    @Override
    public AVModule show(String title, Collection<AVModule> modules) {
        final Set<String> qualifiedNames = new HashSet<>();

        for (AVModule module : modules) {
            qualifiedNames.add(module.getFullName());
        }

        TreeFileChooser.PsiFileFilter filter = new TreeFileChooser.PsiFileFilter() {
            @Override
            public boolean accept(PsiFile psiFile) {
                final PsiClass mainClass = AVJavaIDEAUtils.getMainClass(project, psiFile.getVirtualFile());
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
        final PsiClass mainClass = AVJavaIDEAUtils.getMainClass(project, chooser.getSelectedFile().getVirtualFile());
        if (mainClass != null) {
            return new AVModuleImpl(mainClass.getQualifiedName());
        }
        return null;
    }

}
