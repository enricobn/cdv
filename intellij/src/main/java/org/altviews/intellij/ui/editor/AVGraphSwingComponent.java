package org.altviews.intellij.ui.editor;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.altviews.core.*;
import org.altviews.intellij.ui.mxGraphUtils;
import org.altviews.ui.AVClassChooser;

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
public class AVGraphSwingComponent extends JPanel implements AVGraph {
    private static final String ROUNDED = "ROUNDED";
    private final Set<AVModule> modules = new HashSet<>();
    private final Collection<AVFileEditorComponentListener> listeners = new ArrayList<>();
    private final AVClassChooser classChooser;
    private final AVModuleNavigator navigator;
    private final AVDependenciesFinder finder;
    private final mxGraph graph;

    public AVGraphSwingComponent(AVClassChooser classChooser, AVModuleNavigator navigator, AVDependenciesFinder finder) {
        this.classChooser = classChooser;
        this.navigator = navigator;
        this.finder = finder;
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
//                return super.isCellEditable(cell);
            }
        };

        graph.setDisconnectOnMove(false);
//        graph.setAutoSizeCells(true);

        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_ROUNDED, "true");
        //style.put(mxConstants.STYLE_OPACITY, 50);
        //style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        //style.put(mxConstants.STYLE_ARCSIZE, "#774400");
        stylesheet.putCellStyle(ROUNDED, style);

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        graphComponent.setConnectable(false);

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

        JButton addFileButton = new JButton("Add");
        addFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFile();
            }
        });

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        add(addFileButton, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(graphComponent, gbc);
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

    public void addModule(AVModule module) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                addModule(module, null);
            }
        });
    }

    public void addListener(AVFileEditorComponentListener listener) {
        listeners.add(listener);
    }

    private void handleMouse(MouseEvent e, mxGraphComponent graphComponent) {
        if (!e.isPopupTrigger()) {
            return;
        }

        Object cell = graphComponent.getCellAt(e.getX(), e.getY());

        if (cell != null)
        {
            if (graph.getModel().isVertex(cell)) {
                JPopupMenu popup = new JPopupMenu();
                JMenuItem item;
                AVModule module = (AVModule) graph.getModel().getValue(cell);
                for (AVModuleDependency dep : finder.getDependencies(module)) {
                    if (modules.contains(dep.getModule())) {
                        continue;
                    }
                    popup.add(item = new JMenuItem(dep.toString()));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            addModule(dep.getModule(), cell);
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
                            repaint();
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

    private void addFile() {
        final AVModule module = classChooser.show("Add class");
        if (module != null) {
            if (modules.contains(module)) {
                // TODO message
                return;
            }
            addModule(module, null);
        }
    }

    private void addModule(AVModule module, Object fromCell)  {
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            // get metrics from the graphics
            FontMetrics metrics = getGraphics().getFontMetrics(getFont());
            // get the height of a line of text in this
            // font and render context
//            int hgt = metrics.getHeight();
            // get the advance of my text in this font
            // and render context
            int adv = metrics.stringWidth(module.toString());

            Object v1;
            if (fromCell == null) {
                v1 = graph.insertVertex(parent, null, module, 20, 20, adv + 20, 30, ROUNDED);
            } else {
                final mxGeometry geometry = graph.getCellGeometry(fromCell);
                v1 = graph.insertVertex(parent, null, module, geometry.getX(), geometry.getY() + 30, adv + 20, 30, ROUNDED);
            }

            modules.add(module);

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
                for (AVModuleDependency dep : finder.getDependencies(module)) {
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
        repaint();

        for (AVFileEditorComponentListener listener : listeners) {
            listener.onAdd(module);
        }

    }

    private void doGraphLayout() {
        mxOrganicLayout layout = new mxOrganicLayout(graph);
//        layout.setOptimizeEdgeDistance(false);
//        layout.setOptimizeBorderLine(false);

        layout.execute(graph.getDefaultParent());

        mxGraphUtils.translate(graph, 5, 5);
    }

    @Override
    public Set<AVModule> getModules() {
        return modules;
    }
}
