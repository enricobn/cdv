package org.cdv.intellij.core;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import org.cdv.core.*;
import org.cdv.intellij.CDVJavaIDEAUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by enrico on 3/10/16.
 */
public class CDVJavaIDEADependenciesFinder implements CDVDependenciesFinder {
    private static final Logger logger = Logger.getInstance(CDVJavaIDEADependenciesFinder.class);

    private final Project project;

    public CDVJavaIDEADependenciesFinder(Project project) {
        this.project = project;
    }

    @Override
    public Set<CDVModuleDependency> getDependencies(CDVModule module) {
        Set<CDVModuleDependency> dependencies = new HashSet<>();

        for (PsiClass psiClass : getClasses(module)) {
            dependencies.addAll(getDependencies(psiClass));
        }

        return dependencies;
    }

    public PsiClass[] getClasses(CDVModule module) {
        PsiElement element = CDVJavaIDEAUtils.getPsiClass(project, module.getFullName());
        while (element != null) {
            if (element instanceof PsiClassOwner) {
                return ((PsiClassOwner) element).getClasses();
            }
            element = element.getParent();
        }
        logger.info("Cannot find classes for " + module);
        return new PsiClass[0];
    }

    public Set<CDVModuleDependency> getDependencies(PsiClass psiClass) {
        final Set<CDVModuleDependency> result = new HashSet<>();

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

//        for (PsiClassType ancestor : psiClass.getExtendsListTypes()) {
//            addDependency(result, ancestor.resolve());
//        }
//
//        for (PsiClassType iFace : psiClass.getImplementsListTypes()) {
//            addDependency(result, iFace.resolve());
//        }
//
//        for (PsiField field : psiClass.getAllFields()) {
//            addDependency(result, PsiTypesUtil.getPsiClass(field.getType()));
//        }
//
//        for (PsiMethod method : psiClass.getAllMethods()) {
//            addDependency(result, PsiTypesUtil.getPsiClass(method.getReturnType()));
//            for (PsiParameter parameter : method.getParameterList().getParameters()) {
//                addDependency(result, PsiTypesUtil.getPsiClass(parameter.getType()));
//            }
//        }
//
//        for (PsiMethod method : psiClass.getConstructors()) {
//            for (PsiParameter parameter : method.getParameterList().getParameters()) {
//                addDependency(result, PsiTypesUtil.getPsiClass(parameter.getType()));
//            }
//        }
        return result;
    }

    private void addDependency(final Set<CDVModuleDependency> result, final PsiReference dep) {
//        logger.info("AVJavaIDEADependenciesFinder.addDependency " + dep);
        if (dep == null) {
            return;
        }

//        logger.info("AVJavaIDEADependenciesFinder.addDependency PsiReference resolve " + dep.resolve());

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

    private void addDependency(final Set<CDVModuleDependency> result, final PsiTypeElement dep) {
//        logger.info("AVJavaIDEADependenciesFinder.addDependency " + dep);
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

    private void addDependency(Set<CDVModuleDependency> result, PsiClass dep) {
//        logger.info("AVJavaIDEADependenciesFinder.addDependency " + dep);
        if (dep == null) {
            return;
        }

        if (dep.getContainingFile() == null || dep.getQualifiedName() == null) {
            return;
        }

        final CDVConfiguration.State config = CDVConfiguration.getConfig(project).getState();

        if (config != null && !config.isValid(dep.getQualifiedName())) {
            return;
        }
        final CDVModule module = new CDVModuleImpl(dep.getQualifiedName());
        result.add(new CDVModuleDependencyImpl(module));
    }

    private static boolean match(String value, Collection<String> expressions) {
        for (String s : expressions) {
            final Pattern pattern = Pattern.compile(s);
            final Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }
}
