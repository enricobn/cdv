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

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

public class CDVGraphFileWriter {
    static final String MODULE_ELEMENT = "module";
    static final String FULL_NAME_ATTRIBUTE = "fullName";
    private static final String VERSION = "1.0.0";
    private static final String ROOT_ELEMENT_NAME = "graph";
    private static final String MODULES_ELEMENT = "modules";
    private static final String VERSION_ATTRIBUTE = "version";

    public void write(CDVGraph graph, OutputStream os) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.setAttribute(VERSION_ATTRIBUTE, VERSION);
        doc.appendChild(rootElement);

        Element modules = doc.createElement(MODULES_ELEMENT);
        rootElement.appendChild(modules);

        List<CDVModule> sortedModules = new ArrayList<>(graph.getModules());
        Collections.sort(sortedModules, new Comparator<CDVModule>() {
            @Override
            public int compare(CDVModule o1, CDVModule o2) {
                return o1.getFullName().compareTo(o2.getFullName());
            }
        });

        for (CDVModule module : sortedModules) {
            Element element = doc.createElement(MODULE_ELEMENT);
            element.setAttribute(FULL_NAME_ATTRIBUTE, module.getFullName());
            modules.appendChild(element);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(os);

        transformer.transform(source, result);
        os.flush();
    }

    public static CharSequence empty() throws Exception {
        CDVGraphFileWriter writer = new CDVGraphFileWriter();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            writer.write(new CDVGraph() {
                @Override
                public Set<CDVModule> getModules() {
                    return Collections.emptySet();
                }
            }, os);

            return IOUtils.toString(os.toByteArray(), "UTF-8");
        }
    }
}
