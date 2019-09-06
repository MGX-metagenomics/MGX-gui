package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.AssemblyGroupI;
import de.cebitec.mgx.gui.attributevisualization.view.NodeMapperI;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JInternalFrame;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class NodeMapperImpl implements NodeMapperI<GroupFrameBase> {

    @Override
    public synchronized GroupFrameBase getComponent(Node n) {
        if (!cache.containsKey(n)) {

            // add nodelistener for cache cleanup
            n.addNodeListener(new NodeAdapter() {

                @Override
                public void nodeDestroyed(NodeEvent ev) {
                    Node node = ev.getNode();
                    node.removeNodeListener(this);
                    if (cache.containsKey(node)) {
                        GroupFrameBase frame = cache.remove(node);
                        if (frame.isSelected()) {
                            //handleFrameSelection(null);
                        }
                        frame.removePropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, maintainSingleSelection);
                        frame.dispose();
                    }
                }

            });

            ReplicateGroupI rGrp = n.getLookup().lookup(ReplicateGroupI.class);
            VisualizationGroupI vGrp = n.getLookup().lookup(VisualizationGroupI.class);
            AssemblyGroupI asmGrp = n.getLookup().lookup(AssemblyGroupI.class);
            if (rGrp != null) {
                final ReplicateGroupFrame rgf = new ReplicateGroupFrame(rGrp);
                rgf.addPropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, maintainSingleSelection);
                cache.put(n, rgf);
            } else if (vGrp != null) {
                final GroupFrame vgf = new GroupFrame(vGrp);
                vgf.addPropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, maintainSingleSelection);
                cache.put(n, vgf);
            } else if (asmGrp != null) {
                final AssemblyGroupFrame agf = new AssemblyGroupFrame(asmGrp);
                agf.addPropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, maintainSingleSelection);
                cache.put(n, agf);
            }
        }

        final GroupFrameBase ret = cache.get(n);

        // propertychangelistener to maintain single-selection strategy
        //ret.addPropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, maintainSingleSelection);
        // auto-select first created entry
        if (cache.size() == 1) {
            try {
                ret.setSelected(true);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return ret;
    }

    private synchronized void handleFrameSelection(final GroupFrameBase selObj) {
        try {
            for (GroupFrameBase other : cache.values()) {
                if (other != selObj) {
                    other.setSelected(false);
                }
            }
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }

//        // simulate click event 
//        Point loc = selObj.getLocation();
//        MouseEvent mev = new MouseEvent(selObj, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
//                0, loc.x, loc.y, 1, false);
//        for (MouseListener ml : selObj.getMouseListeners()) {
//            ml.mouseClicked(mev);
//        }
    }

    @Override
    public void dispose() {
        for (Entry<Node, GroupFrameBase> e : cache.entrySet()) {
            e.getValue().dispose();
        }
        cache.clear();
    }

    private final Map<Node, GroupFrameBase> cache = new HashMap<>();

    private final PropertyChangeListener maintainSingleSelection = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            boolean selected = (boolean) evt.getNewValue();
            if (selected) {
                handleFrameSelection((GroupFrameBase) evt.getSource());
            }
        }
    };

}
