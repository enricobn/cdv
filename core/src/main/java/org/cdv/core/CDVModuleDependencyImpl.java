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
