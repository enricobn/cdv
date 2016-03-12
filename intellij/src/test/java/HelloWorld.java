/**
 * Created by enrico on 3/5/16.
 */

import javax.swing.JFrame;

import org.altviews.core.*;
import org.altviews.intellij.ui.editor.AVGraphSwingComponent;
import org.altviews.ui.AVClassChooser;

import java.io.Serializable;
import java.util.*;

public class HelloWorld extends JFrame
{

    /**
     *
     */
    private static final long serialVersionUID = -2707712944901661771L;

    public HelloWorld()
    {
        super("Hello, World!");
//
//        mxGraph graph = new mxGraph() {
//            public boolean isCellSelectable(Object cell)
//            {
//                if (getModel().isEdge(cell))
//                {
//                    return false;
//                }
//                return super.isCellSelectable(cell);
//            }
//
//            @Override
//            public boolean isCellEditable(Object cell) {
//                return false;
////                return super.isCellEditable(cell);
//            }
//        };
//        graph.setDisconnectOnMove(false);
//        graph.setAutoSizeCells(true);
//
//        mxStylesheet stylesheet = graph.getStylesheet();
//        Hashtable<String, Object> style = new Hashtable<>();
//        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
//        style.put(mxConstants.STYLE_ROUNDED, "true");
//        style.put(mxConstants.STYLE_OPACITY, 50);
//        style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
//        style.put(mxConstants.STYLE_ARCSIZE, "#774400");
//        stylesheet.putCellStyle("ROUNDED", style);
//
//
//        Object parent = graph.getDefaultParent();
//
//        graph.getModel().beginUpdate();
//        try
//        {
//            Object v1 = graph.insertVertex(parent, null, Arrays.asList("Hello"), 0, 0,
//                    100, 20, "ROUNDED");
//            Object v2 = graph.insertVertex(parent, null, Arrays.asList("World!"), 0, 50,
//                    100, 20, "ROUNDED");
//            graph.insertEdge(parent, null, null, v1, v2);
//            Object v3 = graph.insertVertex(parent, null, Arrays.asList("Pippo"), 0, 100,
//                    100, 20, "ROUNDED");
//            graph.insertEdge(parent, null, null, v1, v3);
//            graph.insertEdge(parent, null, null, v2, v3);
//        }
//        finally
//        {
//            graph.getModel().endUpdate();
//        }
//
////        mxGraphLayout layout = new mxCompactTreeLayout(graph, false);
////        layout.execute(graph.getDefaultParent());
//
//        mxOrganicLayout layout = new mxOrganicLayout(graph);
////        layout.setOptimizeEdgeDistance(false);
////        layout.setOptimizeBorderLine(false);
////        layout.setOptimizeNodeDistribution(false);
//        layout.execute(graph.getDefaultParent());
//
////        graph.getView().setScale(1.5);
//
//        mxGraphUtils.translate(graph, 5, 5);
//
//        mxGraphComponent graphComponent = new mxGraphComponent(graph);
//        graphComponent.setConnectable(false);
//
//        graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
//        {
//
//            public void mouseReleased(MouseEvent e)
//            {
//                Object cell = graphComponent.getCellAt(e.getX(), e.getY());
//
//                if (cell != null)
//                {
//                    if (graph.getModel().isVertex(cell)) {
//                        List list = (List) graph.getModel().getValue(cell);
//                        System.out.println("cell=" + list);
//                    }
//                }
//            }
//        });
//
////        graphComponent.setZoomFactor(4.0);
////        graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);

        final AVModule second = new AVModuleDummy("org.altviews.SecondClass");
        final AVModule third = new AVModuleDummy("org.altviews.ThirdClass");
        final AVModule first = new AVModuleDummy("org.altviews.FirstClass", new AVModule[]{second, third});

        AVClassChooser chooser = new AVClassChooser() {
            @Override
            public AVModule show(String title) {
                return first;
            }
        };
        AVModuleNavigator navigator = new AVModuleNavigator() {
            @Override
            public void navigateTo(AVModule module) {
            }
        };
        AVDependenciesFinder finder = new AVDependenciesFinder() {
            @Override
            public Set<AVModuleDependency> getDependencies(AVModule module) {
                return ((AVModuleDummy)module).getDependencies();
            }
        };

        AVModuleTypeProvider typeProvider = new AVModuleTypeProvider() {
            @Override
            public AVModuleType getType(AVModule module) {
                if (module.equals(first)) {
                    return AVModuleType.Interface;
                }
                return AVModuleType.Class;
            }
        };
        getContentPane().add(new AVGraphSwingComponent(chooser, navigator, finder, typeProvider));//graphComponent);
    }

    public static void main(String[] args)
    {
        HelloWorld frame = new HelloWorld();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    }

    private static class AVModuleDummy implements AVModule, Serializable {
        private final String fullName;
        private final Set<AVModuleDependency> dependencies = new HashSet<>();

        private AVModuleDummy(String fullName) {
            this.fullName = fullName;
        }

        private AVModuleDummy(String fullName, AVModule[] deps) {
            this.fullName = fullName;
            for (AVModule dep : deps) {
                dependencies.add(new AVModuleDependencyDummy(dep));
            }
        }

        public Set<AVModuleDependency> getDependencies() {
            return dependencies;
        }

        @Override
        public String getSimpleName() {
            return fullName.substring(fullName.lastIndexOf('.') + 1);
        }

        @Override
        public String getFullName() {
            return fullName;
        }

        @Override
        public String toString() {
            return getSimpleName();
        }
    }

    private static class AVModuleDependencyDummy implements AVModuleDependency, Serializable {
        private final AVModule module;

        private AVModuleDependencyDummy(AVModule module) {
            this.module = module;
        }

        @Override
        public AVModule getModule() {
            return module;
        }

        @Override
        public String toString() {
            return getModule().toString();
        }
    }

}
