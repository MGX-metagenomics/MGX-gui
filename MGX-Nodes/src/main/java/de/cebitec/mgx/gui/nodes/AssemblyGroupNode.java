/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.groups.AssemblyGroupI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.nodefactory.GroupedAssembledSeqRunNodeFactory;
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
public class AssemblyGroupNode extends MGXGroupNodeBase<AssemblyGroupI> {

    public AssemblyGroupNode(AssemblyGroupI vGroup) {
        this(new GroupedAssembledSeqRunNodeFactory(vGroup), vGroup);
    }

    private AssemblyGroupNode(GroupedAssembledSeqRunNodeFactory nf, AssemblyGroupI vGroup) {
        super(nf, Lookups.singleton(vGroup), vGroup);
        super.setName(vGroup.getName());
        super.setDisplayName(vGroup.getName());
    }

    @Override
    public PasteType getDropType(Transferable t, final int action, int index) {
        final Node dropNode = NodeTransfer.node(t, DnDConstants.ACTION_REFERENCE + NodeTransfer.CLIPBOARD_COPY);
        if (dropNode != null) {

            final AssemblyGroupI vg = getLookup().lookup(AssemblyGroupI.class);
            final Collection<? extends AssembledSeqRunI> seqruns = dropNode.getLookup().lookupAll(AssembledSeqRunI.class);

            // reject, if any run is already present
            Set<AssembledSeqRunI> oldRuns = vg.getContent();
            for (AssembledSeqRunI newRun : seqruns) {
                for (AssembledSeqRunI oldRun : oldRuns) {
                    if (oldRun.equals(newRun)) {
                        System.err.println("rejecting " + newRun.getName() + ", already present as " + oldRun.getName());
                        return MGXPasteTypes.REJECT;
                    }
                }
            }

            if (seqruns != null && !seqruns.isEmpty() && !this.equals(dropNode.getParentNode())) {
                return new PasteType() {

                    @Override
                    public Transferable paste() throws IOException {
                        vg.add(seqruns.toArray(new AssembledSeqRunI[]{}));
                        return null;
                    }
                };
            }
        }
        return MGXPasteTypes.REJECT;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case AssemblyGroupI.ASMGROUP_RENAMED:
                setName((String) evt.getNewValue());
                setDisplayName((String) evt.getNewValue());
                break;
            case AssemblyGroupI.ASMGROUP_HAS_DIST:
                // ignore
                break;
            case AssemblyGroupI.ASMGROUP_CHANGED:
                this.updateModified();
                break;
            case AssemblyGroupI.ASMGROUP_DEACTIVATED:
            case AssemblyGroupI.ASMGROUP_ACTIVATED:
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
