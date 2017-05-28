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

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CDVGraphFileWriterTest {

    @Test
    public void testWriteRead() throws Exception {
        CDVGraphFileWriter writer = new CDVGraphFileWriter();

        final Set<CDVModule> modules = new HashSet<>();
        modules.add(new CDVModuleImpl("org.cdv.Module1"));
        modules.add(new CDVModuleImpl("org.cdv.ui.Module2"));

        CDVGraph graph = new CDVGraph() {
            @Override
            public Set<CDVModule> getModules() {
                return modules;
            }
        };

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            writer.write(graph, os);

            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

            CDVGraphFileReader reader = new CDVGraphFileReader();
            final CDVGraph graphRed = reader.read(is);

            assertEquals(modules, graphRed.getModules());
        }
    }
}