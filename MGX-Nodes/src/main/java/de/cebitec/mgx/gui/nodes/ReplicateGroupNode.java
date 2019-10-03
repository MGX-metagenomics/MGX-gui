/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodefactory.ReplicateNodeFactory;
import de.cebitec.mgx.gui.nodes.util.MGXPasteTypes;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Exceptions;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class ReplicateGroupNode extends MGXGroupNodeBase<ReplicateGroupI> {

    public ReplicateGroupNode(ReplicateGroupI rGroup) {
        this(new ReplicateNodeFactory(rGroup), rGroup);
    }

    private ReplicateGroupNode(ReplicateNodeFactory rnf, ReplicateGroupI rg) {
        super(rnf, Lookups.singleton(rg), rg);
        super.setName(rg.getName());
        super.setDisplayName(rg.getName());
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case VisualizationGroupI.VISGROUP_RENAMED:
                setName((String) evt.getNewValue());
                setDisplayName((String) evt.getNewValue());
                break;
            case VisualizationGroupI.VISGROUP_HAS_DIST:
            case VisualizationGroupI.VISGROUP_CHANGED:
                // ignore
                break;
            case ReplicateGroupI.REPLICATEGROUP_ACTIVATED:
            case ReplicateGroupI.REPLICATEGROUP_DEACTIVATED:
                // ignore;
                break;
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_ADDED:
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_REMOVED:
                //ignore
                break;
            default:
                super.propertyChange(evt);
        }
    }

    @Override
    public void updateModified() {
        setName(getContent().getName());
        setDisplayName(getContent().getName());
    }

    @Override
    public PasteType getDropType(Transferable t, final int action, int index) {
        final Node dropNode = NodeTransfer.node(t, DnDConstants.ACTION_REFERENCE + NodeTransfer.CLIPBOARD_COPY);
        if (dropNode != null) {

            final ReplicateGroupI replGroup = getLookup().lookup(ReplicateGroupI.class);

            // check for replicates being added
            final Collection<? extends ReplicateI> replicates = dropNode.getLookup().lookupAll(ReplicateI.class);
            if (replicates != null && !replicates.isEmpty() && !this.equals(dropNode.getParentNode())) {
                return new PasteType() {

                    @Override
                    public Transferable paste() throws IOException {
                        for (ReplicateI r : replicates) {
                            replGroup.add(r);
                        }
                        return null;
                    }
                };
            }

            //
            // if a dna extract is being transferred, obtain all sequencing runs generated from
            // it and add them individually as new replicates
            //
            final Collection<? extends DNAExtractI> extracts = dropNode.getLookup().lookupAll(DNAExtractI.class);

            if (extracts != null && !extracts.isEmpty() && !this.equals(dropNode.getParentNode())) {
                return new PasteType() {

                    @Override
                    public Transferable paste() throws IOException {
                        for (DNAExtractI e : extracts) {
                            MGXMasterI master = e.getMaster();
                            Iterator<SeqRunI> iter;
                            try {
                                iter = master.SeqRun().ByExtract(e);
                            } catch (MGXException ex) {
                                Exceptions.printStackTrace(ex);
                                return ExTransferable.EMPTY;
                            }
                            while (iter != null && iter.hasNext()) {
                                ReplicateI newReplicate = VGroupManager.getInstance().createReplicate(replGroup);
                                newReplicate.add(iter.next());
                            }
                        }
                        return null;
                    }
                };
            }
        }
        return MGXPasteTypes.REJECT;
    }

}
