/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils;

import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ContigListModel extends BaseModel<ContigI> {

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

        SwingWorker<List<ContigI>, Void> sw = new SwingWorker<List<ContigI>, Void>() {
            @Override
            protected List<ContigI> doInBackground() throws Exception {
                List<ContigI> tmp = new ArrayList<>();

                Iterator<ContigI> iter = bin.getMaster().Contig().ByBin(bin);
                while (iter != null && iter.hasNext()) {
                    tmp.add(iter.next());
                }

                // sort by contig length, descending
                Collections.sort(tmp, new Comparator<ContigI>() {
                    @Override
                    public int compare(ContigI t1, ContigI t2) {
                        return Integer.compare(t2.getLength(), t1.getLength());
                    }

                });

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
                addAll(data);
                if (!data.isEmpty()) {
                    setSelectedItem(data.get(0));
                }
                fireContentsChanged();
            }

        };

        sw.execute();

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
