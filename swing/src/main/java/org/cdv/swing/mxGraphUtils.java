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
package org.cdv.swing;

import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public abstract class mxGraphUtils {

    public static void translate(mxGraph graph, double x, double y) {
        final Object[] vertices = graph.getChildCells(graph.getDefaultParent(), true, false);

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        for (Object vertex : vertices) {
            final mxGeometry geometry = graph.getCellGeometry(vertex);
            if (geometry.getX() < minx) {
                minx = geometry.getX();
            }
            if (geometry.getY() < miny) {
                miny = geometry.getY();
            }
            for (Object edge : graph.getEdges(vertex)) {
                mxRectangle rect = graph.getBoundingBox(edge);
                if (rect.getX() < minx) {
                    minx = rect.getX();
                }
                if (rect.getY() < miny) {
                    miny = rect.getY();
                }
            }

        }

        graph.getView().setTranslate(new mxPoint(x - minx, y - miny));
    }

    public static void exportToSVG(mxGraph graph, File file)
            throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("svg");
        doc.appendChild(rootElement);

        mxSvgCanvas canvas = new mxSvgCanvas(doc);
        graph.drawGraph(canvas);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(file);

        transformer.transform(source, result);
    }

}
