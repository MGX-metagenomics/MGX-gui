/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodeactions.RemoveReplicateAction;
import de.cebitec.mgx.gui.nodefactory.GroupedSeqRunNodeFactory;
import de.cebitec.mgx.gui.nodes.util.MGXPasteTypes;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class ReplicateNode extends MGXGroupNodeBase<GroupI<SeqRunI>> {

    public ReplicateNode(ReplicateI replicate) {
        super(new GroupedSeqRunNodeFactory(replicate), Lookups.singleton(replicate), replicate);
        super.setName(replicate.getName());
        super.setDisplayName(replicate.getName());
    }

    @Override
    public final Transferable drag() throws IOException {
        return ExTransferable.EMPTY; // disable DnD
    }

    @Override
    public PasteType getDropType(Transferable t, final int action, int index) {
        final Node dropNode = NodeTransfer.node(t, DnDConstants.ACTION_REFERENCE + NodeTransfer.CLIPBOARD_COPY);
        if (dropNode != null) {

            final ReplicateI replicate = getLookup().lookup(ReplicateI.class);
            final Collection<? extends SeqRunI> seqruns = dropNode.getLookup().lookupAll(SeqRunI.class);

            // reject, if any run is already present
            Set<SeqRunI> oldRuns = replicate.getContent();
            for (SeqRunI newRun : seqruns) {
                if (oldRuns.contains(newRun)) {
                    return MGXPasteTypes.REJECT;
                }
            }

            if (seqruns != null && !seqruns.isEmpty() && !this.equals(dropNode.getParentNode())) {
                return new PasteType() {

                    @Override
                    public Transferable paste() throws IOException {
                        replicate.add(seqruns.toArray(new SeqRunI[]{}));
                        return null;
                    }
                };
            }
        }
        return MGXPasteTypes.REJECT;
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new RemoveReplicateAction()};
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case VisualizationGroupI.VISGROUP_RENAMED:
                setName((String) evt.getNewValue());
                setDisplayName((String) evt.getNewValue());
                break;
            case VisualizationGroupI.VISGROUP_HAS_DIST:
                break;
            case VisualizationGroupI.VISGROUP_CHANGED:
                break;
            default:
                super.propertyChange(evt);
                break;
        }
    }

//    private class RemoveReplicateAction extends AbstractAction {
//
//        public RemoveReplicateAction() {
//            putValue(NAME, "Remove");
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            ReplicateI replicate = Utilities.actionsGlobalContext().lookup(ReplicateI.class);
//            replicate.getReplicateGroup().remove(replicate);
//        }
//    }
}
