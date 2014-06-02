package de.cebitec.mgx.gui.treeview.retry;

import de.cebitec.mgx.gui.treeview.TreeView;
import prefuse.action.assignment.SizeAction;
import prefuse.visual.VisualItem;

/**
 *
 * @author sj
 */
public class NodeSizeAction extends SizeAction {

    private final static int MIN_CIRCLE_SIZE = 30;

    @Override
    public double getSize(VisualItem item) {
        long totalCount = item.getLong(TreeView.nodeTotalElements);
        return MIN_CIRCLE_SIZE + 10 *  Math.log(Math.round(2 * totalCount));
    }
}
