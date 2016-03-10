package org.altviews.core;

import java.io.Serializable;

/**
 * Created by enrico on 3/10/16.
 */
public class AVModuleDependencyImpl implements AVModuleDependency,Serializable {
    private final AVModule module;

    public AVModuleDependencyImpl(AVModule module) {
        this.module = module;
    }

    @Override
    public AVModule getModule() {
        return module;
    }

    @Override
    public String toString() {
        return getModule().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AVModuleDependencyImpl that = (AVModuleDependencyImpl) o;

        return !(module != null ? !module.equals(that.module) : that.module != null);

    }

    @Override
    public int hashCode() {
        return module != null ? module.hashCode() : 0;
    }
}
