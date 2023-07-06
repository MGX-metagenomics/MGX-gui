/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils;

import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ContigListModel extends BaseModel<ContigI> {

    @Serial
    private static final long serialVersionUID = 1L;

    private BinI bin;

    public void setBin(BinI bin) {
        this.bin = bin;
    }

    @Override
    public synchronized void update() {
        if (bin == null || bin.isDeleted()) {
            clear();
            return;
        }

        final ProgressHandle ph = ProgressHandle.createHandle("Fetching contigs for " + bin.getName());
        ph.start();
        ph.switchToDeterminate(bin.getNumContigs());

        SwingWorker<List<ContigI>, Void> sw = new SwingWorker<List<ContigI>, Void>() {
            @Override
            protected List<ContigI> doInBackground() throws Exception {
                int numElements = 0;
                List<ContigI> tmp = new ArrayList<>(bin.getNumContigs());

                Iterator<ContigI> iter = bin.getMaster().Contig().ByBin(bin);
                while (iter != null && iter.hasNext()) {
                    tmp.add(iter.next());
                    numElements++;

                    if (numElements % 500 == 0) {
                        ph.progress(numElements);
                    }
                }

                // we dont need to sort here, as the server delivers the data
                // presorted by length descending
                return tmp;
            }

            @Override
            protected void done() {
                List<ContigI> data;
                try {
                    data = get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    return;
                }

                ph.progress(bin.getNumContigs());
                ph.finish();

                addAll(data);
                if (!data.isEmpty()) {
                    setSelectedItem(data.get(0));
                }
                fireContentsChanged();
            }

        };

        sw.execute();

    }

    @Override
    public void clear() {
        this.bin = null;
        super.clear();
    }

    public int findIndexByID(long id) {
        for (ContigI c : content) {
            if (c.getId() == id) {
                return content.indexOf(c);
            }
        }
        return -1;
    }
}
