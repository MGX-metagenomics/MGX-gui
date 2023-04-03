
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.gui.nodefactory.ReplicateNodeFactory;
import java.awt.dnd.DnDConstants;
import java.io.Serial;
import org.openide.explorer.view.BeanTreeView;

/**
 *
 * @author sjaenick
 */
final class ReplicateGroupTreeView extends BeanTreeView { //implements PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final ReplicateGroupI rGroup;
    private final ReplicateNodeFactory rgnf;
   @Override
    public void setDragSource(boolean state) {
    }
    public ReplicateGroupTreeView(ReplicateGroupI rGroup) {
        this(rGroup, new ReplicateNodeFactory(rGroup));
    }

    public ReplicateGroupTreeView(ReplicateGroupI rGroup, ReplicateNodeFactory rgnf) {
        super();
        this.rGroup = rGroup;
        this.rgnf = rgnf;
        setRootVisible(false);
        setDropTarget(true);
        setAllowedDropActions(DnDConstants.ACTION_COPY + DnDConstants.ACTION_REFERENCE);
        //setDropTarget();
        //rGroup.addPropertyChangeListener(this);
    }
//
//    private void setDropTarget() {
//        DropTarget dt = new DropTarget(this, new DropTargetAdapter() {
//            @Override
//            public void dragEnter(DropTargetDragEvent dtde) {
//
//                Set<SeqRunI> myRuns = new HashSet<>();
//                for (ReplicateI repl : rGroup.getReplicates()) {
//                    myRuns.addAll(repl.getSeqRuns());
//                }
//
//                Transferable t = dtde.getTransferable();
//                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
//                    try {
//                        final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
//
//                        if (mto.areDataFlavorsSupported(new DataFlavor[]{SampleI.DATA_FLAVOR})) {
//                            int elems = mto.getCount();
//                            for (int i = 0; i < elems; i++) {
//                                SampleI sample = (SampleI) mto.getTransferData(i, SampleI.DATA_FLAVOR);
//                                MGXMasterI master = sample.getMaster();
//                                Iterator<DNAExtractI> extractsIter = master.DNAExtract().BySample(sample);
//                                while (extractsIter.hasNext()) {
//                                    DNAExtractI extract = extractsIter.next();
//                                    Iterator<SeqRunI> seqrunIter = master.SeqRun().ByExtract(extract);
//                                    while (seqrunIter.hasNext()) {
//                                        SeqRunI seqrun = seqrunIter.next();
//                                        if (myRuns.contains(seqrun)) {
//                                            dtde.rejectDrag();
//                                            return;
//                                        }
//                                    }
//                                }
//                            }
//                            dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                            return;
//                        }
//                    } catch (UnsupportedFlavorException | IOException | MGXException e) {
//                    }
//                }
//
//                // single sample
//                if (dtde.isDataFlavorSupported(SampleI.DATA_FLAVOR)) {
//                    try {
//                        SampleI sample = (SampleI) dtde.getTransferable().getTransferData(SampleI.DATA_FLAVOR);
//                        MGXMasterI master = sample.getMaster();
//                        Iterator<DNAExtractI> extractsIter = master.DNAExtract().BySample(sample);
//                        while (extractsIter.hasNext()) {
//                            DNAExtractI extract = extractsIter.next();
//                            Iterator<SeqRunI> seqrunIter = master.SeqRun().ByExtract(extract);
//                            while (seqrunIter.hasNext()) {
//                                SeqRunI seqrun = seqrunIter.next();
//                                if (myRuns.contains(seqrun)) {
//                                    dtde.rejectDrag();
//                                    return;
//                                }
//                            }
//                        }
//                    } catch (UnsupportedFlavorException | IOException | MGXException ex) {
//                    }
//                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                    return;
//                }
//
//                // single seqrun
//                if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
//                    try {
//                        SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
//                        if (myRuns.contains(run)) {
//                            dtde.rejectDrag();
//                            return;
//                        }
//                    } catch (UnsupportedFlavorException | IOException ex) {
//                        dtde.rejectDrag();
//                    }
//                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                    return;
//                }
//
//                // default: reject
//                dtde.rejectDrag();
//            }
//
//            @Override
//            public void drop(DropTargetDropEvent dtde) {
//
//                Set<SeqRunI> myRuns = new HashSet<>();
//                for (ReplicateI repl : rGroup.getReplicates()) {
//                    myRuns.addAll(repl.getSeqRuns());
//                }
//
//                Set<SeqRunI> newRuns = new HashSet<>();
//
//                Transferable t = dtde.getTransferable();
//                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
//                    try {
//                        final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
//                        if (mto.areDataFlavorsSupported(new DataFlavor[]{SampleI.DATA_FLAVOR})) {
//                            int elems = mto.getCount();
//                            for (int i = 0; i < elems; i++) {
//                                SampleI sample = (SampleI) mto.getTransferData(i, SampleI.DATA_FLAVOR);
//                                MGXMasterI master = sample.getMaster();
//                                Iterator<DNAExtractI> extractsIter = master.DNAExtract().BySample(sample);
//                                while (extractsIter.hasNext()) {
//                                    DNAExtractI extract = extractsIter.next();
//                                    Iterator<SeqRunI> seqrunIter = master.SeqRun().ByExtract(extract);
//                                    while (seqrunIter.hasNext()) {
//                                        SeqRunI seqrun = seqrunIter.next();
//                                        if (myRuns.contains(seqrun)) {
//                                            dtde.rejectDrop();
//                                        } else {
//                                            newRuns.add(seqrun);
//                                        }
//                                    }
//                                }
//
//                            }
//                        }
//                    } catch (UnsupportedFlavorException | IOException | MGXException e) {
//                    }
//                }
//                if (dtde.isDataFlavorSupported(SampleI.DATA_FLAVOR)) {
//                    try {
//                        SampleI sample = (SampleI) dtde.getTransferable().getTransferData(SampleI.DATA_FLAVOR);
//                        MGXMasterI master = sample.getMaster();
//                        Iterator<DNAExtractI> extractsIter = master.DNAExtract().BySample(sample);
//                        while (extractsIter.hasNext()) {
//                            DNAExtractI extract = extractsIter.next();
//                            Iterator<SeqRunI> seqrunIter = master.SeqRun().ByExtract(extract);
//                            while (seqrunIter.hasNext()) {
//                                SeqRunI seqrun = seqrunIter.next();
//                                if (myRuns.contains(seqrun)) {
//                                    dtde.rejectDrop();
//                                } else {
//                                    newRuns.add(seqrun);
//                                }
//                            }
//                        }
//                    } catch (UnsupportedFlavorException | IOException | MGXException ex) {
//                    }
//                }
//
//                // single seqrun - add as new replicate 
//                if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
//                    try {
//                        SeqRunI seqrun = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
//                        if (myRuns.contains(seqrun)) {
//                            dtde.rejectDrop();
//                        } else {
//                            newRuns.add(seqrun);
//                        }
//                    } catch (UnsupportedFlavorException | IOException ex) {
//                    }
//                }
//
//                // add new runs
//                for (SeqRunI seqrun : newRuns) {
//                    ReplicateI newReplicate = VGroupManager.getInstance().createReplicate(rGroup);
//                    newReplicate.addSeqRun(seqrun);
//                }
//                dtde.dropComplete(true);
//            }
//        });
//        setDropTarget(dt);
//    }



}
