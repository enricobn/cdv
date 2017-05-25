package org.cdv.core;

import java.io.Serializable;

/**
 * Created by enrico on 3/10/16.
 */
public class CDVModuleImpl implements CDVModule,Serializable {
    private final String fullName;

    public CDVModuleImpl(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getSimpleName() {
        int index = getFullName().lastIndexOf('.');
        if (index < 0) {
            return getFullName();
        }
        return getFullName().substring(index + 1);
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getNamespace() {
        int index = getFullName().lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return getFullName().substring(0, index);
    }

    @Override
    public String toString() {
        return getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CDVModuleImpl avModule = (CDVModuleImpl) o;

        return !(fullName != null ? !fullName.equals(avModule.fullName) : avModule.fullName != null);

    }

    @Override
    public int hashCode() {
        return fullName != null ? fullName.hashCode() : 0;
    }
}
