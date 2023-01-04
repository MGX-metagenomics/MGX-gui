/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.dnautils.DNAUtils;
import de.cebitec.mgx.gui.binexplorer.internal.ContigViewController;
import de.cebitec.mgx.gui.swingutils.SeqPanel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class SequenceDisplayAction implements ActionListener {

    private final ContigViewController vc;
    private final boolean translate_to_aa;

    public SequenceDisplayAction(ContigViewController vc, boolean translate_to_aa) {
        this.vc = vc;
        this.translate_to_aa = translate_to_aa;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        SwingWorker<SequenceI, Void> sw = new SwingWorker<SequenceI, Void>() {

            @Override
            protected SequenceI doInBackground() throws Exception {
                if (vc.getSelectedRegion() != null) {
                    final MGXMasterI master = vc.getSelectedRegion().getMaster();
                    return master.AssembledRegion().getDNASequence(vc.getSelectedRegion());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    SequenceI seq = get();
                    if (seq != null) {
                        String[] opts = new String[]{"Close"};
                        String title = !translate_to_aa
                                ? "DNA sequence for " + seq.getName()
                                : "Amino acid sequence for " + seq.getName();
                        final SeqPanel sp = new SeqPanel();
                        if (translate_to_aa) {
                            String translation = DNAUtils.translate(seq.getSequence());
                            seq.setSequence(translation);
                        }
                        sp.show(seq);

                        DialogDescriptor d = new DialogDescriptor(sp, title, true, DialogDescriptor.DEFAULT_OPTION, opts[0], null);
                        d.setOptions(opts);
                        d.setClosingOptions(opts);
                        d.setAdditionalOptions(new Object[]{"Copy to clipboard"});
                        d.setMessage(sp);
                        d.addPropertyChangeListener(new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent evt) {
                                if (evt.getPropertyName().equals(DialogDescriptor.PROP_VALUE) && evt.getNewValue().equals("Copy to clipboard")) {
                                    Toolkit.getDefaultToolkit()
                                            .getSystemClipboard()
                                            .setContents(sp.getSelection(), null);
                                }

                            }

                        });
                        DialogDisplayer.getDefault().notify(d);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        };
        sw.execute();
    }

}
