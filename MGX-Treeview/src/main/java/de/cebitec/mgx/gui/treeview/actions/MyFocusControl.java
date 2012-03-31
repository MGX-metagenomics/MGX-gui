package de.cebitec.mgx.gui.treeview.actions;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import prefuse.Visualization;
import prefuse.controls.FocusControl;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableEdgeItem;

/**
 * focus control
 * @author rbisdorf
 */
public class MyFocusControl extends FocusControl{
    
    private String group = Visualization.FOCUS_ITEMS;
    private boolean activeNodeBehaviour = false;

    public MyFocusControl(int clicks, String act) {
        super(clicks, act);
    }
    
    @Override
    public void itemClicked(VisualItem item, MouseEvent e) {
        if (UILib.isButtonPressed(e, button)) {
            if (item != curFocus) {
                Visualization vis = item.getVisualization();
                TupleSet ts = vis.getFocusGroup(group);

                boolean ctrl = e.isControlDown();
                if (!ctrl) {
                    curFocus = item;
                    ts.setTuple(item);
                } else if (ts.containsTuple(item)) {
                    ts.removeTuple(item);
                } else {
                    ts.addTuple(item);
                }
                runActivity(vis);
            }
        } else {
            // multiple selction with right clkick ...
            if (item != curFocus) {
                Visualization vis = item.getVisualization();
                TupleSet ts = vis.getFocusGroup(group);

                if (ts.containsTuple(item)) {
                    ts.removeTuple(item);
                } else {
                    curFocus = item;
                    addTuple(ts, item);
                }
                runActivity(vis);
            } else {
                Visualization vis = item.getVisualization();
                TupleSet ts = vis.getFocusGroup(group);
                ts.removeTuple(item);
                curFocus = null;
                runActivity(vis);
            }
        }
    }

    // if control down or activeNodeBehaviour set, focus node will be added at the end of the list by default
    // so we invert the list to center (focus the last added node)
    private void addTuple(TupleSet ts, VisualItem item) {
        Iterator tuples = ts.tuples();
        ArrayList<Tuple> tupleList = new ArrayList<Tuple>();
        while (tuples.hasNext()) {
            Tuple currTuble = (Tuple) tuples.next();
            tupleList.add(0, currTuble);
        }
        tupleList.add(0, item);
        ts.clear();
        for (Tuple tuple : tupleList) {
            ts.addTuple(tuple);
        }
    }

    private void runActivity(Visualization vis) {
        if (activity != null) {
            vis.run(activity);
        }
    }

    public boolean isActiveNodeBehaviour() {
        return activeNodeBehaviour;
    }

    public void setActiveNodeBehaviour(boolean activeNodeBehaviour) {
        this.activeNodeBehaviour = activeNodeBehaviour;
    }

    // in the eyes of an bloody java beginner this is necessary !!!
    // otherwise real strange thinks will be performed when clickin an edge ....
    //TODO look at Predicates and use them 
    @Override
    protected boolean filterCheck(VisualItem item) {
        if (item.getClass().equals(TableEdgeItem.class)) {
            return false;
        }
        return true;
    }
    
}
