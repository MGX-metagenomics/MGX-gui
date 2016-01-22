/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
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
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class VizGroupNode extends AbstractNodeBase<VisualizationGroupI> {

    public VizGroupNode(VisualizationGroupI vGroup) {
        this(new GroupedSeqRunNodeFactory(vGroup), vGroup);
    }

    private VizGroupNode(GroupedSeqRunNodeFactory nf, VisualizationGroupI vGroup) {
        super(nf, Lookups.singleton(vGroup), vGroup);
        setName(vGroup.getName());
        setDisplayName(vGroup.getName());
    }

    @Override
    public PasteType getDropType(Transferable t, final int action, int index) {
        final Node dropNode = NodeTransfer.node(t, DnDConstants.ACTION_REFERENCE + NodeTransfer.CLIPBOARD_COPY);
        if (dropNode != null) {

            final VisualizationGroupI vg = getLookup().lookup(VisualizationGroupI.class);
            final Collection<? extends SeqRunI> seqruns = dropNode.getLookup().lookupAll(SeqRunI.class);

            // reject, if any run is already present
            Set<SeqRunI> oldRuns = vg.getSeqRuns();
            for (SeqRunI newRun : seqruns) {
                if (oldRuns.contains(newRun)) {
                    return MGXPasteTypes.REJECT;
                }
            }

            if (seqruns != null && !seqruns.isEmpty() && !this.equals(dropNode.getParentNode())) {
                return new PasteType() {

                    @Override
                    public Transferable paste() throws IOException {
                        vg.addSeqRuns(seqruns.toArray(new SeqRunI[]{}));
                        return null;
                    }
                };
            }
        }
        return MGXPasteTypes.REJECT;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{}; //new RemoveVGroupAction()};
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case VisualizationGroupI.VISGROUP_RENAMED:
                setName((String) evt.getNewValue());
                setDisplayName((String) evt.getNewValue());
                break;
            case VisualizationGroupI.VISGROUP_HAS_DIST:
                // ignore
                break;
            default:
                super.propertyChange(evt);
        }
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }

}
