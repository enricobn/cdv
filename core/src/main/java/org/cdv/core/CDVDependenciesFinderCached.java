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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CDVDependenciesFinderCached implements CDVDependenciesFinder {
    private final CDVDependenciesFinder finder;
    private final ConcurrentHashMap<CDVModule,Set<CDVModuleDependency>> cache = new ConcurrentHashMap<>();

    public CDVDependenciesFinderCached(CDVDependenciesFinder finder) {
        this.finder = finder;
    }

    @Override
    public Set<CDVModuleDependency> getDependencies(CDVModule module) {
        if (!cache.containsKey(module)) {
            cache.put(module, finder.getDependencies(module));
        }
        return cache.get(module);
    }
}
