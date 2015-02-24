package de.cebitec.mgx.gui.treeview.retry;

import de.cebitec.mgx.gui.treeview.TreeView;
import java.awt.Rectangle;
import java.awt.Shape;
import org.apache.commons.math3.util.FastMath;
import prefuse.render.AbstractShapeRenderer;
import prefuse.visual.VisualItem;

/**
 *
 * @author sj
 */
public class NodeRenderer extends AbstractShapeRenderer {

    private final Rectangle area = new Rectangle(100, 100);
    private final static int MIN_CIRCLE_SIZE = 30;

    @Override
    @SuppressWarnings("unchecked")
    protected Shape getRawShape(VisualItem item) {
        long totalCount = item.getLong(TreeView.nodeTotalElements);
        int size = MIN_CIRCLE_SIZE + (int) (10d * FastMath.log(Math.round(2 * totalCount)));
        area.setFrame(item.getX(), item.getY(), size, size);

        //Map<VisualizationGroupI, Long> content = (Map<VisualizationGroupI, Long>) item.get(TreeView.nodeContent);

        return area;
    }
}
