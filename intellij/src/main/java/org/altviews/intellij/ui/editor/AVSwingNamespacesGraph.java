package org.altviews.intellij.ui.editor;

import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.altviews.core.*;
import org.altviews.intellij.ui.mxGraphUtils;
import org.altviews.ui.AVModuleChooser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by enrico on 3/8/16.
 */
public class AVSwingNamespacesGraph  {
    private static final String STYLE = "CLASS";
//    private final Collection<AVFileEditorComponentListener> listeners = new ArrayList<>();
    private final AVNamespaceNavigator navigator;
    private final AVDependenciesFinder finder;
//    private final AVModuleTypeProvider typeProvider;
//    private final AVModuleChooser moduleChooser;
    private final mxGraph graph;
    private final boolean horizontal;
//    private final boolean editable;
    private final mxGraphComponent graphComponent;
    private final Set<String> namespaces = new HashSet<>();
//    private AVSwingGraphType type;

    public AVSwingNamespacesGraph(AVNamespaceNavigator navigator, AVDependenciesFinder finder,
                                  boolean horizontal) {
        this.navigator = navigator;
        this.finder = finder;
        this.horizontal = horizontal;

        graph = new mxGraph() {
            public boolean isCellSelectable(Object cell)
            {
                if (getModel().isEdge(cell))
                {
                    return false;
                }
                return super.isCellSelectable(cell);
            }

            @Override
            public boolean isCellEditable(Object cell) {
                return false;
            }

            @Override
            public boolean isCellResizable(Object cell) {
                return false;
            }
        };

        graph.setDisconnectOnMove(false);
//        graph.setAutoSizeCells(true);

        {
            mxStylesheet styleSheet = graph.getStylesheet();
            Hashtable<String, Object> style = new Hashtable<>();
            style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
            style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
            style.put(mxConstants.STYLE_FONTCOLOR, "#4D6870");
            style.put(mxConstants.STYLE_SPACING_TOP, 2);
            style.put(mxConstants.STYLE_STROKEWIDTH, 2);
            styleSheet.putCellStyle(STYLE, style);
        }

        graphComponent = new mxGraphComponent(graph);
//        graphComponent.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        graphComponent.setConnectable(false);
        graphComponent.getGraphHandler().setMoveEnabled(true);

//        graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
//        {
//            public void mousePressed(MouseEvent e)
//            {
//                handleMouse(e, graphComponent);
//            }
//
//            public void mouseReleased(MouseEvent e)
//            {
//                handleMouse(e, graphComponent);
//            }
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                handleMouseClicked(e, graphComponent);
//            }
//        });
    }

    public JComponent getComponent() {
        return graphComponent;
    }

//    private void handleMouseClicked(MouseEvent e, mxGraphComponent graphComponent) {
//        if (e.getClickCount() != 2) {
//            return;
//        }
//        Object cell = graphComponent.getCellAt(e.getX(), e.getY());
//
//        if (cell != null) {
//            if (graph.getModel().isVertex(cell)) {
//                AVModule module = (AVModule) graph.getModel().getValue(cell);
//                navigator.navigateTo(module);
//            }
//        }
//    }

//    public void addModule(final AVModule module) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                addModule(module, null, finder);
//            }
//        });
//    }
//
//    public void addModules(final Collection<AVModule> modules) {
//        addModules(modules, new AVDependenciesFinderCached(finder));
//    }
//
//    public void addModules(final Collection<AVModule> modules, final AVDependenciesFinder finder) {
//        for (final AVModule module : modules) {
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    addModule(module, null, finder);
//                }
//            });
//        }
//    }

//    public void addListener(AVFileEditorComponentListener listener) {
//        listeners.add(listener);
//    }


    public void setGraph(final AVGraph avGraph) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clear();
                AVNamespacesGraph nsGraph = new AVNamespacesGraph(avGraph, finder);
                for (String ns : nsGraph.getNamespaces()) {
                    addNamespace(ns, null, nsGraph);
                }
                doGraphLayout();

                graph.setAutoSizeCells(true);
                graphComponent.repaint();
            }
        });
    }

    private void addNamespace(String namespace, Object fromCell, AVNamespacesGraph nsGraph)  {
        if (namespaces.contains(namespace)) {
            return;
        }
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            FontMetrics metrics = graphComponent.getFontMetrics(graphComponent.getFont());
            int adv = metrics.stringWidth(namespace);

            double x;
            double y;
            if (fromCell == null) {
                x = 20;
                y = 20;
            } else {
                final mxGeometry geometry = graph.getCellGeometry(fromCell);
                x = geometry.getX();
                y = geometry.getY() + 30;
            }

            Object v1 = graph.insertVertex(parent, null, namespace, x, y, adv + 20, 30, STYLE);

            namespaces.add(namespace);

            final Set<String> dependencies = nsGraph.getDependencies(namespace);

            final Object[] vertices = graph.getChildCells(parent, true, false);

            for (Object vertex : vertices) {
                final String ns = (String) graph.getModel().getValue(vertex);

                if (ns.equals(namespace)) {
                    continue;
                }

                for (String dep : nsGraph.getDependencies(ns)) {
                    if (dep.equals(namespace)) {
                        graph.insertEdge(parent, null, null, vertex, v1);
                    }
                }

                for (String dep : dependencies) {
                    if (dep.equals(ns)) {
                        graph.insertEdge(parent, null, null, v1, vertex);
                    }
                }
            }

        } finally {
            graph.getModel().endUpdate();
        }

        doGraphLayout();

        graph.setAutoSizeCells(true);
        graphComponent.repaint();

//        for (AVFileEditorComponentListener listener : listeners) {
//            listener.onAdd(module);
//        }
    }

    private void doGraphLayout() {
//        mxOrganicLayout layout = new mxOrganicLayout(graph);
        int orientation;
        if (horizontal) {
            orientation = SwingConstants.WEST;
        } else {
            orientation = SwingConstants.NORTH;
        }
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph, orientation);

        layout.execute(graph.getDefaultParent());

        mxGraphUtils.translate(graph, 5, 5);
    }

//    @Override
//    public Set<AVModule> getModules() {
//        return Collections.unmodifiableSet(modules);
//    }

    public void clear() {
        namespaces.clear();
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        } finally {
            graph.getModel().endUpdate();
        }
    }

    public void exportToSVG(File file) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("svg");
            doc.appendChild(rootElement);

            mxSvgCanvas canvas = new mxSvgCanvas(doc);
            graph.drawGraph(canvas);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            // Output to console for testing
//            StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
        } catch (Exception e1) {
            // TODO
            throw new RuntimeException(e1);
        }
    }

    public void exportToPng(File file) {
        BufferedImage img = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, false, null);
        try {
            ImageIO.write(img, "png", new FileImageOutputStream(file));
        } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

}