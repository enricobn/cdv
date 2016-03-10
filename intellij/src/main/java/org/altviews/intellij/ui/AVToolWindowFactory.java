package org.altviews.intellij.ui;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiClass;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.altviews.core.AVModule;
import org.altviews.core.AVModuleDependency;
import org.altviews.intellij.core.AVJavaIDEAModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by enrico on 3/4/16.
 */
public class AVToolWindowFactory implements ToolWindowFactory {
    private static final String ROUNDED = "ROUNDED";
    private final Set<AVModule> modules = new HashSet<>();
    private JPanel toolWindowContent;
    private JButton addViewButton;
    private JComboBox viewCombo;
    private JButton addFileButton;
    private JPanel modulesPanel;
    private Project project;
    private ToolWindow toolWindow;
    private mxGraph graph;

    public AVToolWindowFactory() {
        addViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addView();
            }
        });
        addFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFile();
            }
        });
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        initialize();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void initialize() {
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
        graph.setAutoSizeCells(true);

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
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        modulesPanel.add(graphComponent, gbc);
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
                for (AVModuleDependency dep : module.getDependencies()) {
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
                            doLayout();
                        }
                    }
                });

                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private void addView() {
        final String viewName = Messages.showInputDialog(toolWindowContent, "New view", "Alt views", null);
        if (viewName != null) {
            viewCombo.addItem(viewName);
        }
    }

    private void addFile() {
        final TreeClassChooser fileChooser =
                TreeClassChooserFactory.getInstance(project).createAllProjectScopeChooser("Add class");
        fileChooser.showDialog();
        final PsiClass selected = fileChooser.getSelected();
        if (selected != null) {
            AVModule module = new AVJavaIDEAModule(selected);
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
            FontMetrics metrics = modulesPanel.getGraphics().getFontMetrics(modulesPanel.getFont());
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

                for (AVModuleDependency dep : m.getDependencies()) {
                    if (dep.getModule().equals(module)) {
                        graph.insertEdge(parent, null, null, vertex, v1);
                    }
                }
                for (AVModuleDependency dep : module.getDependencies()) {
                    if (dep.getModule().equals(m)) {
                        graph.insertEdge(parent, null, null, v1, vertex);
                    }
                }
            }

        } finally {
            graph.getModel().endUpdate();
        }

        doLayout();

        graph.setAutoSizeCells(true);
    }

    private void doLayout() {
        mxOrganicLayout layout = new mxOrganicLayout(graph);
//        layout.setOptimizeEdgeDistance(false);
//        layout.setOptimizeBorderLine(false);

        layout.execute(graph.getDefaultParent());

        UIUtils.translate(graph, 5, 5);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        toolWindowContent = new JPanel();
        toolWindowContent.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        addViewButton = new JButton();
        addViewButton.setText("Add view");
        toolWindowContent.add(addViewButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("View");
        toolWindowContent.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewCombo = new JComboBox();
        toolWindowContent.add(viewCombo, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addFileButton = new JButton();
        addFileButton.setText("Add file");
        toolWindowContent.add(addFileButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modulesPanel = new JPanel();
        modulesPanel.setLayout(new GridBagLayout());
        toolWindowContent.add(modulesPanel, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return toolWindowContent;
    }
}
