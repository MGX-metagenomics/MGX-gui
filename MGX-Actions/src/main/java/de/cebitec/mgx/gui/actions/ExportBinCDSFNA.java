package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.common.RegionType;
import de.cebitec.mgx.dnautils.DNAUtils;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.swingutils.util.SuffixFilter;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.DNASequence;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.DNASequenceI;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import static javax.swing.Action.NAME;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author sjaenick
 */
@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.actions.ExportBinCDSFNA")
@ActionRegistration(displayName = "Export CDS to nucleotide FASTA", lazy = true)
public class ExportBinCDSFNA extends NodeAction implements LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup context;
    private Lookup.Result<BinI> lkpInfo;

    public ExportBinCDSFNA() {
        this(Utilities.actionsGlobalContext());
    }

    private ExportBinCDSFNA(Lookup context) {
        putValue(NAME, "Export CDS to nucleotide FASTA");
        this.context = context;
        init();
    }

    private void init() {
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(BinI.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return "Export genes to amino-acid FASTA";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        Collection<? extends BinI> bins = lkpInfo.allInstances();
        if (bins.isEmpty()) {
            return;
        }

        for (final BinI bin : bins) {

            JFileChooser fchooser = new JFileChooser();
            fchooser.setDialogType(JFileChooser.SAVE_DIALOG);

            // try to restore last directory selection
            String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
            if (last != null) {
                File f = new File(last);
                if (f.exists() && f.isDirectory() && f.canWrite()) {
                    fchooser.setCurrentDirectory(f);
                }
            }

            // suggest a file name
            String suffix = ".fas";
            File suggestedName = new File(fchooser.getCurrentDirectory(), cleanupName(bin.getName()) + suffix);
            int cnt = 1;
            while (suggestedName.exists()) {
                String newName = new StringBuilder(cleanupName(bin.getName()))
                        .append(" (")
                        .append(cnt++)
                        .append(")")
                        .append(suffix)
                        .toString();
                suggestedName = new File(fchooser.getCurrentDirectory(), newName);
            }
            fchooser.setSelectedFile(suggestedName);
            FileFilter ff = new SuffixFilter(FileType.FAS);
            fchooser.addChoosableFileFilter(ff);
            fchooser.setFileFilter(ff);

            if (fchooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                // save directory
                NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath());

                final File target = fchooser.getSelectedFile();
                if (target.exists()) {
                    // ask if file should be overwritten, else return
                    String msg = new StringBuilder("A file named ")
                            .append(target.getName())
                            .append(" already exists. Should this ")
                            .append("file be overwritten?")
                            .toString();
                    NotifyDescriptor nd = new NotifyDescriptor(msg,
                            "Overwrite file?",
                            NotifyDescriptor.OK_CANCEL_OPTION,
                            NotifyDescriptor.WARNING_MESSAGE,
                            null, null);
                    Object ret = DialogDisplayer.getDefault().notify(nd);
                    if (!NotifyDescriptor.OK_OPTION.equals(ret)) {
                        continue;
                    }
                }

                ProgressHandle handle = ProgressHandle.createHandle("Export to " + target.getName());
                handle.start(bin.getNumContigs());
                final AtomicInteger numComplete = new AtomicInteger(0);

                MGXPool.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // make sure we start with an empty file
                            if (target.exists()) {
                                target.delete();
                            }

                            try ( FastaWriter writer = new FastaWriter(target.getAbsolutePath())) {

                                Iterator<ContigI> it = bin.getMaster().Contig().ByBin(bin);

                                while (it != null && it.hasNext()) {
                                    ContigI contig = it.next();
                                    SequenceI contigSeq = bin.getMaster().Contig().getDNASequence(contig);
                                    String contigDNAseq = contigSeq.getSequence();

                                    Iterator<AssembledRegionI> regionIter = bin.getMaster().AssembledRegion().ByContig(contig);
                                    while (regionIter != null && regionIter.hasNext()) {
                                        AssembledRegionI region = regionIter.next();
                                        if (region.getType() == RegionType.CDS) {
                                            
                                            // instead of invoking
                                            // getMaster().AssembledRegion().getDNASequence(region);
                                            // for each region, we extract the dna sequence for
                                            // the genes from the contig, which is faster

                                            String dnaSeq;

                                            if (region.getStart() < region.getStop()) {
                                                // forward strand
                                                dnaSeq = contigDNAseq.substring(region.getStart(), region.getStop() + 1);
                                            } else {
                                                // reverse
                                                dnaSeq = contigDNAseq.substring(region.getStop(), region.getStart() + 1);
                                                dnaSeq = DNAUtils.reverseComplement(dnaSeq);
                                            }

                                            DNASequenceI s = new DNASequence(dnaSeq.getBytes(), false);
                                            String name = contig.getName() + "_" + region.getId();
                                            s.setName(name.getBytes());
                                            writer.addSequence(s);
                                        }
                                    }

                                    handle.progress(numComplete.addAndGet(1));
                                }
                            }
                        } catch (MGXException | SequenceException | IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                        handle.finish();
                    }
                });

            }

        }
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled() && !lkpInfo.allInstances().isEmpty();
    }

    private String cleanupName(String name) {
        if (name.contains(File.separator)) {
            name = name.replace(File.separator, "_");
        }
        return name;
    }

}
