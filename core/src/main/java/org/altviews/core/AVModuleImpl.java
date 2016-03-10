package org.altviews.core;

import java.io.Serializable;

/**
 * Created by enrico on 3/10/16.
 */
public class AVModuleImpl implements AVModule,Serializable {
    private final String fullName;

    public AVModuleImpl(String fullName) {
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
    public String toString() {
        return getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AVModuleImpl avModule = (AVModuleImpl) o;

        return !(fullName != null ? !fullName.equals(avModule.fullName) : avModule.fullName != null);

    }

    @Override
    public int hashCode() {
        return fullName != null ? fullName.hashCode() : 0;
    }
}
