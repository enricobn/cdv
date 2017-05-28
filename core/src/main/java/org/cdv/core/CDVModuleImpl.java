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
package org.cdv.core;

import java.io.Serializable;

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
