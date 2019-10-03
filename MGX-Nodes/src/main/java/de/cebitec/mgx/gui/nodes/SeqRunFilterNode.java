/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Action;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.FilterNode;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class SeqRunFilterNode extends FilterNode implements NodeListener {

    private final SeqRunNode n;
    private final GroupI<SeqRunI> vGroup;

    public SeqRunFilterNode(SeqRunNode node, GroupI<SeqRunI> vGroup) {
        super(node, Children.LEAF, Lookups.fixed(node, node.getContent(), vGroup));
        disableDelegation(DELEGATE_SET_DISPLAY_NAME + DELEGATE_GET_ACTIONS);
        n = node;
        this.vGroup = vGroup;
        super.setShortDescription(getToolTipText(n.getContent()));
        node.addNodeListener(this);
    }

    @Override
    public void destroy() throws IOException {
        vGroup.remove(n.getContent());
        fireNodeDestroyed();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public boolean canCut() {
        return true;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    private String getToolTipText(SeqRunI run) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        String numSeqs = formatter.format(run.getNumSequences());

        return new StringBuilder("<html><b>Sequencing run: </b>").append(run.getName())
                .append("<br>")
                .append("<b>Server: </b>").append(run.getMaster().getServerName())
                .append("<br><b>Project: </b>").append(run.getMaster().getProject())
                .append("<br><hr><br>")
                .append(run.getSequencingTechnology().getName()).append(" ")
                .append(run.getSequencingMethod().getName())
                .append("<br>")
                .append(numSeqs).append(" reads")
                .append("</html>").toString();
    }

    @Override
    public String getDisplayName() {
        MGXMasterI m = n.getContent().getMaster();
        return n.getContent().getName() + " (" + m.getProject() + ")";
    }

    @Override
    public Action[] getActions(boolean context) {
        Action a = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-RemoveSeqRunFromGroupAction.instance", Action.class);
        return new Action[]{a};
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    fireNodeDestroyed();
                }
            });
        } else {
            fireNodeDestroyed();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

}
