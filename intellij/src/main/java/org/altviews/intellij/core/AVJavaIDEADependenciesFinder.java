package org.altviews.intellij.core;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTypesUtil;
import org.altviews.core.*;
import org.altviews.intellij.AVJavaIDEAUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 3/10/16.
 */
public class AVJavaIDEADependenciesFinder implements AVDependenciesFinder {
    private static final Logger logger = Logger.getInstance(AVJavaIDEADependenciesFinder.class);

    private final Project project;

    public AVJavaIDEADependenciesFinder(Project project) {
        this.project = project;
    }

    @Override
    public Set<AVModuleDependency> getDependencies(AVModule module) {
        Set<AVModuleDependency> dependencies = new HashSet<>();

        for (PsiClass psiClass : getClasses(module)) {
            dependencies.addAll(getDependencies(psiClass));
        }

        return dependencies;
    }

    public PsiClass[] getClasses(AVModule module) {
        PsiElement element = AVJavaIDEAUtils.getPsiClass(project, module.getFullName());
        while (element != null) {
            if (element instanceof PsiClassOwner) {
                return ((PsiClassOwner) element).getClasses();
            }
            element = element.getParent();
        }
        return new PsiClass[0];
    }

    public Set<AVModuleDependency> getDependencies(PsiClass psiClass) {
        final Set<AVModuleDependency> result = new HashSet<>();

        final PsiElementVisitor visitor = new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
//                logger.info("AVJavaIDEAUtils.getPsiClasses element " + element + " " + element.getClass());
                if (element instanceof PsiTypeElement) {
                    addDependency(result, (PsiTypeElement) element);
                } else if (element instanceof PsiClass) {
                    result.addAll(getDependencies((PsiClass) element));
                } else if (element instanceof PsiReference) {
                    addDependency(result, (PsiReference) element);
                } else {
                    element.acceptChildren(this);
                }
            }
        };

        psiClass.acceptChildren(visitor);

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

        for (PsiMethod method : psiClass.getConstructors()) {
            for (PsiParameter parameter : method.getParameterList().getParameters()) {
                addDependency(result, PsiTypesUtil.getPsiClass(parameter.getType()));
            }
        }
        return result;
    }

    private static void addDependency(final Set<AVModuleDependency> result, final PsiReference dep) {
//        logger.info("AVJavaIDEADependenciesFinder.addDependancy " + dep);
        if (dep == null) {
            return;
        }

//        logger.info("AVJavaIDEADependenciesFinder.addDependancy PsiReference resolve " + dep.resolve());

        if (dep.resolve() instanceof PsiClass) {
            addDependency(result, (PsiClass) dep.resolve());
        }

        if (dep instanceof PsiJavaCodeReferenceElement) {
            PsiJavaCodeReferenceElement qualified = (PsiJavaCodeReferenceElement) dep;
            for (PsiType psiType : qualified.getTypeParameters()) {
                addDependency(result, PsiTypesUtil.getPsiClass(psiType));
            }
        }

    }

    private static void addDependency(final Set<AVModuleDependency> result, final PsiTypeElement dep) {
//        logger.info("AVJavaIDEADependenciesFinder.addDependancy " + dep);
        if (dep == null) {
            return;
        }

        dep.getType().accept(new PsiTypeVisitor<Boolean>() {
            @Nullable
            @Override
            public Boolean visitArrayType(PsiArrayType arrayType) {
                addDependency(result, PsiTypesUtil.getPsiClass(arrayType.getComponentType()));
                return false;
            }

            @Nullable
            @Override
            public Boolean visitClassType(PsiClassType classType) {
                addDependency(result, classType.resolve());
                for (PsiType psiType : classType.getParameters()) {
                    addDependency(result, PsiTypesUtil.getPsiClass(psiType));
                }
                return false;
            }
        });
    }

    private static void addDependency(Set<AVModuleDependency> result, PsiClass dep) {
//        logger.info("AVJavaIDEADependenciesFinder.addDependancy " + dep);
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
