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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class CDVGraphUtils {

    public static String toString(CDVGraph newGraph) throws Exception {
        CDVGraphFileWriter writer = new CDVGraphFileWriter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            writer.write(newGraph, os);
        } finally {
            os.close();
        }
        return os.toString("UTF-8");
    }

    public static CDVGraph fromString(String text) throws Exception {
        CDVGraphFileReader reader = new CDVGraphFileReader();

        try (InputStream inputStream = new ByteArrayInputStream(text.getBytes(Charset.forName("UTF-8")))) {
            return reader.read(inputStream);
        }
    }
}
