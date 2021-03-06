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

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.cdv.core.*;
import org.cdv.ui.CDVModuleChooser;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
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

public class CDVSwingGraph implements CDVGraph {
    private static final String INTERFACE_STYLE = "INTERFACE";
    private static final String CLASS_STYLE = "CLASS";
    private static final String UNKNOWN_STYLE = "UNKNOWN";
    private static final String SELECTED_EDGE_STYLE = "SELECTED_EDGE";
    private final Set<CDVModule> modules = new HashSet<>();
    private final Collection<CDVComponentListener> listeners = new ArrayList<>();
    private final CDVModuleNavigator navigator;
    private final CDVDependenciesFinder finder;
    private final CDVModuleTypeProvider typeProvider;
    private final CDVModuleChooser moduleChooser;
    private final mxGraph graph;
    private final boolean horizontal;
    private final boolean editable;
    private final mxGraphComponent graphComponent;
    private Object selectedEdge;

    public CDVSwingGraph(CDVModuleNavigator navigator, CDVDependenciesFinder finder,
                         CDVModuleTypeProvider typeProvider, CDVModuleChooser moduleChooser, boolean horizontal,
                         boolean editable) {
        this.navigator = navigator;
        this.finder = finder;
        this.typeProvider = typeProvider;
        this.moduleChooser = moduleChooser;
        this.horizontal = horizontal;
        this.editable = editable;

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
            style.put(mxConstants.STYLE_ROUNDED, "true");
            //style.put(mxConstants.STYLE_OPACITY, 50);
            //style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
            //style.put(mxConstants.STYLE_ARCSIZE, "#774400");
            style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
            style.put(mxConstants.STYLE_FONTCOLOR, "#4D6870");
            style.put(mxConstants.STYLE_FILLCOLOR, "#D1F4FF");
            style.put(mxConstants.STYLE_SPACING_TOP, 2);
            style.put(mxConstants.STYLE_STROKEWIDTH, 2);
            styleSheet.putCellStyle(INTERFACE_STYLE, style);
        }

        {
            mxStylesheet styleSheet = graph.getStylesheet();
            Hashtable<String, Object> style = new Hashtable<>();
            style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
            style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
            style.put(mxConstants.STYLE_FONTCOLOR, "#4D6870");
            style.put(mxConstants.STYLE_SPACING_TOP, 2);
            style.put(mxConstants.STYLE_STROKEWIDTH, 2);
            styleSheet.putCellStyle(CLASS_STYLE, style);
        }

        {
            mxStylesheet styleSheet = graph.getStylesheet();
            Hashtable<String, Object> style = new Hashtable<>();
            style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
            style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
            style.put(mxConstants.STYLE_FONTCOLOR, "red");
            style.put(mxConstants.STYLE_SPACING_TOP, 2);
            style.put(mxConstants.STYLE_STROKEWIDTH, 2);
            styleSheet.putCellStyle(UNKNOWN_STYLE, style);
        }

        {
            mxStylesheet styleSheet = graph.getStylesheet();
            Hashtable<String, Object> style = new Hashtable<>();
            style.put(mxConstants.STYLE_STROKECOLOR, "#00FF00");
            styleSheet.putCellStyle(SELECTED_EDGE_STYLE, style);
        }

        graphComponent = new mxGraphComponent(graph);
//        graphComponent.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        graphComponent.setConnectable(false);
        graphComponent.getGraphHandler().setMoveEnabled(true);

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                handleMouse(e, graphComponent);
            }

            public void mouseReleased(MouseEvent e)
            {
                handleMouse(e, graphComponent);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e, graphComponent);
            }
        });
    }

    public JComponent getComponent() {
        return graphComponent;
    }

    private void handleMouseClicked(MouseEvent e, mxGraphComponent graphComponent) {
        Object cell = graphComponent.getCellAt(e.getX(), e.getY());

        if (cell != null) {
            if (graph.getModel().isVertex(cell)) {
                if (e.getClickCount() != 2) {
                    return;
                }
                CDVModule module = (CDVModule) graph.getModel().getValue(cell);
                navigator.navigateTo(module);
            } else if (graph.getModel().isEdge(cell)) {
                if (selectedEdge != null) {
                    graph.setCellStyle(null, new Object[]{selectedEdge});
                }

                if (selectedEdge != cell) {
                    graph.setCellStyle(SELECTED_EDGE_STYLE, new Object[]{cell});
                    selectedEdge = cell;
                }
            }
        }
    }

    public void addModule(final CDVModule module) {
        try {
            CDVSwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    addModule(module, null, finder);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addModules(final Collection<CDVModule> modules) {
        addModules(modules, new CDVDependenciesFinderCached(finder));
    }

    public void addModules(final Collection<CDVModule> modules, final CDVDependenciesFinder finder) {
        for (final CDVModule module : modules) {
            try {
                CDVSwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        addModule(module, null, finder);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addListener(CDVComponentListener listener) {
        listeners.add(listener);
    }

    private void handleMouse(MouseEvent e, final mxGraphComponent graphComponent) {
        if (!editable || !e.isPopupTrigger()) {
            return;
        }

        final Object cell = graphComponent.getCellAt(e.getX(), e.getY());

        if (cell != null) {
            if (graph.getModel().isVertex(cell)) {
                JPopupMenu popup = new JPopupMenu();
                JMenuItem item;
                final CDVModule module = (CDVModule) graph.getModel().getValue(cell);

                final Set<CDVModuleDependency> dependencies = finder.getDependencies(module);
                for (Iterator<CDVModuleDependency> i = dependencies.iterator(); i.hasNext();) {
                    if (modules.contains(i.next().getModule())) {
                        i.remove();
                    }
                }

                if (!dependencies.isEmpty()) {
                    final List<CDVModule> depModules = new ArrayList<>();
                    for (final CDVModuleDependency dep : dependencies) {
                        depModules.add(dep.getModule());
                    }

                    Collections.sort(depModules, new Comparator<CDVModule>() {
                        @Override
                        public int compare(CDVModule o1, CDVModule o2) {
                            return o1.getSimpleName().compareTo(o2.getSimpleName());
                        }
                    });

                    popup.add(item = new JMenuItem("Add ..."));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final CDVModule module = moduleChooser.show("Add module", depModules);
                            if (module != null) {
                                addModule(module);
                            }
                        }
                    });
                    popup.addSeparator();

                    popup.add(item = new JMenuItem("Add all"));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            addModules(depModules);
                        }
                    });
                    popup.addSeparator();

                    for (final CDVModule dep : depModules) {
                        popup.add(item = new JMenuItem("Add " + dep.toString()));
                        item.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                addModule(dep, cell, finder);
                            }
                        });
                    }
                    popup.addSeparator();
                }

                popup.add(item = new JMenuItem("Delete"));
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        graph.getModel().beginUpdate();
                        try {
                            graph.removeCells(new Object[]{cell});
                        } finally {
                            graph.getModel().endUpdate();
                            modules.remove(module);
                            doGraphLayout();
                            graphComponent.repaint();
                            for (CDVComponentListener listener : listeners) {
                                listener.onRemove(module);
                            }

                        }
                    }
                });

                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private void addModule(CDVModule module, Object fromCell, CDVDependenciesFinder finder)  {
        if (modules.contains(module)) {
            return;
        }
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            FontMetrics metrics = graphComponent.getFontMetrics(graphComponent.getFont());
            int adv = metrics.stringWidth(module.toString());

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

            String style;
            if (CDVModuleType.Interface == typeProvider.getType(module)) {
                style = INTERFACE_STYLE;
            } else if (CDVModuleType.Class== typeProvider.getType(module)) {
                style = CLASS_STYLE;
            } else {
                style = UNKNOWN_STYLE;
            }

            Object v1 = graph.insertVertex(parent, null, module, x, y, adv + 20, 30, style);

            modules.add(module);

            final Set<CDVModuleDependency> dependencies = finder.getDependencies(module);

            final Object[] vertices = graph.getChildCells(parent, true, false);

            for (Object vertex : vertices) {
                final CDVModule m = (CDVModule) graph.getModel().getValue(vertex);

                if (m.equals(module)) {
                    continue;
                }

                for (CDVModuleDependency dep : finder.getDependencies(m)) {
                    if (dep.getModule().equals(module)) {
                        graph.insertEdge(parent, null, null, vertex, v1);
                    }
                }

                for (CDVModuleDependency dep : dependencies) {
                    if (dep.getModule().equals(m)) {
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

        for (CDVComponentListener listener : listeners) {
            listener.onAdd(module);
        }
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

    @Override
    public Set<CDVModule> getModules() {
        return Collections.unmodifiableSet(modules);
    }

    public void clear() {
        modules.clear();
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        } finally {
            graph.getModel().endUpdate();
        }
    }

    public void exportToSVG(File file) {
        try {
            mxGraphUtils.exportToSVG(graph, file);
        } catch (Exception e1) {
            // TODO
            throw new RuntimeException(e1);
        }
    }

    public void exportToPNG(File file) {
        BufferedImage img = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, false, null);
        try {
            ImageIO.write(img, "png", new FileImageOutputStream(file));
        } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

}
