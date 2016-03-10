package org.altviews.intellij.core;

import org.altviews.core.AVModule;
import org.altviews.core.AVModuleDependency;

/**
 * Created by enrico on 3/4/16.
 */
public class AVJavaIDEADependency implements AVModuleDependency {
    private final AVJavaIDEAModule module;

    public AVJavaIDEADependency(AVJavaIDEAModule module) {
        this.module = module;
    }

    @Override
    public AVModule getModule() {
        return module;
    }

    @Override
    public String toString() {
        return module.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AVJavaIDEADependency that = (AVJavaIDEADependency) o;

        return !(module != null ? !module.equals(that.module) : that.module != null);

    }

    @Override
    public int hashCode() {
        return module != null ? module.hashCode() : 0;
    }
}
