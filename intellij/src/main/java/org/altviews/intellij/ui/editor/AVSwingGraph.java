package org.altviews.intellij.ui.editor;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.altviews.core.*;
import org.altviews.intellij.ui.mxGraphUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Created by enrico on 3/8/16.
 */
public class AVSwingGraph implements AVGraph {
    private static final String INTERFACE_STYLE = "INTERFACE";
    private static final String CLASS_STYLE = "CLASS";
    private final Set<AVModule> modules = new HashSet<>();
    private final Collection<AVFileEditorComponentListener> listeners = new ArrayList<>();
    private final AVModuleNavigator navigator;
    private final AVDependenciesFinder finder;
    private final AVModuleTypeProvider typeProvider;
    private final mxGraph graph;
    private final boolean horizontal;
    private final boolean editable;
    private final mxGraphComponent graphComponent;

    public AVSwingGraph(AVModuleNavigator navigator, AVDependenciesFinder finder,
                        AVModuleTypeProvider typeProvider, boolean horizontal, boolean editable) {
        this.navigator = navigator;
        this.finder = finder;
        this.typeProvider = typeProvider;
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
        if (e.getClickCount() != 2) {
            return;
        }
        Object cell = graphComponent.getCellAt(e.getX(), e.getY());

        if (cell != null) {
            if (graph.getModel().isVertex(cell)) {
                AVModule module = (AVModule) graph.getModel().getValue(cell);
                navigator.navigateTo(module);
            }
        }
    }

    public void addModule(final AVModule module) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                addModule(module, null, finder);
            }
        });
    }

    public void addModules(final Collection<AVModule> modules) {
        addModules(modules, new AVDependenciesFinderCached(finder));
    }

    public void addModules(final Collection<AVModule> modules, final AVDependenciesFinder finder) {
        for (final AVModule module : modules) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    addModule(module, null, finder);
                }
            });
        }
    }

    public void addListener(AVFileEditorComponentListener listener) {
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
                final AVModule module = (AVModule) graph.getModel().getValue(cell);

                final Set<AVModuleDependency> dependencies = finder.getDependencies(module);
                for (Iterator<AVModuleDependency> i = dependencies.iterator(); i.hasNext();) {
                    if (modules.contains(i.next().getModule())) {
                        i.remove();
                    }
                }

                if (!dependencies.isEmpty()) {
                    popup.add(item = new JMenuItem("Add all"));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Collection<AVModule> modules = new ArrayList<AVModule>();
                            for (final AVModuleDependency dep : dependencies) {
                                modules.add(dep.getModule());
                            }
                            addModules(modules);
                        }
                    });
                    popup.addSeparator();
                }

                for (final AVModuleDependency dep : dependencies) {
                    popup.add(item = new JMenuItem("Add " + dep.toString()));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            addModule(dep.getModule(), cell, finder);
                        }
                    });
                }



                if (popup.getSubElements().length > 0) {
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
                            for (AVFileEditorComponentListener listener : listeners) {
                                listener.onRemove(module);
                            }

                        }
                    }
                });

                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private void addModule(AVModule module, Object fromCell, AVDependenciesFinder finder)  {
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
            if (AVModuleType.Interface == typeProvider.getType(module)) {
                style = INTERFACE_STYLE;
            } else {
                style = CLASS_STYLE;
            }

            Object v1 = graph.insertVertex(parent, null, module, x, y, adv + 20, 30, style);

            modules.add(module);

            final Set<AVModuleDependency> dependencies = finder.getDependencies(module);

            final Object[] vertices = graph.getChildCells(parent, true, false);

            for (Object vertex : vertices) {
                final AVModule m = (AVModule) graph.getModel().getValue(vertex);

                if (m.equals(module)) {
                    continue;
                }

                for (AVModuleDependency dep : finder.getDependencies(m)) {
                    if (dep.getModule().equals(module)) {
                        graph.insertEdge(parent, null, null, vertex, v1);
                    }
                }

                for (AVModuleDependency dep : dependencies) {
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

        for (AVFileEditorComponentListener listener : listeners) {
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
    public Set<AVModule> getModules() {
        return Collections.unmodifiableSet(modules);
    }

    @Override
    public void clear() {
        modules.clear();
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        } finally {
            graph.getModel().endUpdate();
        }
    }
}
