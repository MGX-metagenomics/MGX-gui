package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.gui.genbankexporter.GBKExporter;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.swingutils.util.SuffixFilter;
import java.io.File;
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
@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.actions.ExportBin")
@ActionRegistration(displayName = "Export to GenBank", lazy = true)
public class ExportBin extends NodeAction implements LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup context;
    private Lookup.Result<BinI> lkpInfo;

    public ExportBin() {
        this(Utilities.actionsGlobalContext());
    }

    private ExportBin(Lookup context) {
        putValue(NAME, "Export to GenBank");
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
        return "Export to GenBank";
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

            final String taxAssignment = bin.getTaxonomy();

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
            String suffix = ".gbk";
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
            FileFilter ff = new SuffixFilter(FileType.EMBLGENBANK);
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

                            Iterator<ContigI> it = bin.getMaster().Contig().ByBin(bin);
                            while (it != null && it.hasNext()) {
                                ContigI contig = it.next();
                                GBKExporter.exportContig(target, contig, taxAssignment, true);
                                handle.progress(numComplete.addAndGet(1));
                            }
                        } catch (Exception ex) {
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
