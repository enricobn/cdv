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
import java.util.Collections;
import java.util.Set;

/**
 * Created by enrico on 3/9/16.
 */
public class CDVGraphFileWriter {
    public static final String VERSION = "1.0.0";
    public static final String ROOT_ELEMENT_NAME = "graph";
    public static final String MODULES_ELEMENT = "modules";
    public static final String MODULE_ELEMENT = "module";
    public static final String FULLNAME_ATTRIBUTE = "fullName";
    public static final String VERSION_ATTRIBUTE = "version";

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

        for (CDVModule module : graph.getModules()) {
            Element element = doc.createElement(MODULE_ELEMENT);
            element.setAttribute(FULLNAME_ATTRIBUTE, module.getFullName());
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
