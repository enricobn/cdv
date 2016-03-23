package org.altviews.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by enrico on 3/9/16.
 */
public class AVGraphFileReader {

    public AVGraph read(InputStream is) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        final Set<AVModule> modules = new HashSet<>();

        NodeList modulesList = doc.getElementsByTagName(AVGraphFileWriter.MODULE_ELEMENT);

        for (int i = 0; i < modulesList.getLength(); i++) {
            Element element = (Element) modulesList.item(i);
            modules.add(new AVModuleImpl(element.getAttribute(AVGraphFileWriter.FULLNAME_ATTRIBUTE)));
        }

        return new AVGraph() {
            @Override
            public Set<AVModule> getModules() {
                return modules;
            }
        };
    }
}
