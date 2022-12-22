/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
        dispose();
        if (bin == null) {
            clear();
            return;
        }

        List<ContigI> tmp = new ArrayList<>();
        if (!bin.isDeleted()) {
            Iterator<ContigI> iter = null;
            try {
                iter = bin.getMaster().Contig().ByBin(bin);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            while (iter != null && iter.hasNext()) {
                tmp.add(iter.next());
            }
        }
        
        // sort by contig length, descending
        Collections.sort(tmp, new Comparator<ContigI>() {
            @Override
            public int compare(ContigI t1, ContigI t2) {
                return Integer.compare(t2.getLength(), t1.getLength());
            }

        });
        addAll(tmp);
        if (!tmp.isEmpty()) {
            setSelectedItem(tmp.get(0));
        }
        fireContentsChanged();
    }
    
    public int findIndexByID(long id) {
        for (ContigI c : content) {
            if (c.getId() == id) {
                return content.indexOf(c);
            }
        }
        return -1;
    }
    
    public void dispose() {
        for (ContigI ctg : content) {
            ctg.deleted();
        }
        clear();
    }
}
