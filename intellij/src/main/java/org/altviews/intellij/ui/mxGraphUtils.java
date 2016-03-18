package org.altviews.intellij.ui;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

/**
 * Created by enrico on 3/8/16.
 */
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

}
