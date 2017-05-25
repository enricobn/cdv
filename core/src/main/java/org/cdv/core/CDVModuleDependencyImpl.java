package org.cdv.core;

import java.io.Serializable;

/**
 * Created by enrico on 3/10/16.
 */
public class CDVModuleDependencyImpl implements CDVModuleDependency,Serializable {
    private final CDVModule module;

    public CDVModuleDependencyImpl(CDVModule module) {
        this.module = module;
    }

    @Override
    public CDVModule getModule() {
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

        CDVModuleDependencyImpl that = (CDVModuleDependencyImpl) o;

        return !(module != null ? !module.equals(that.module) : that.module != null);

    }

    @Override
    public int hashCode() {
        return module != null ? module.hashCode() : 0;
    }
}
