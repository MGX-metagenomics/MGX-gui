/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ContigModel extends BaseModel<ContigI> {

    private BinI bin;

    public void setBin(BinI bin) {
        this.bin = bin;
    }

    @Override
    public synchronized void update() {
        if (bin == null) {
            clear();
            return;
        }
        clear();

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

        Collections.sort(tmp);
        addAll(tmp);
        if (!tmp.isEmpty()) {
            setSelectedItem(tmp.get(0));
        }
        fireContentsChanged();
    }
}
