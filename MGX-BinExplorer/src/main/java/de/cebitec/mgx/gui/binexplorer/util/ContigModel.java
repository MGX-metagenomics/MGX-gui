/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.MGX2MasterI;
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
    private MGX2MasterI master;

    public void setBin(BinI bin) {
        this.bin = bin;
    }
    
    public void setMaster(MGX2MasterI master) {
        this.master = master;
    }

    @Override
    public synchronized void update() {
        if (bin == null) {
            clear();
            return;
        }
        clear();

        List<ContigI> tmp  = new ArrayList<>();
        if (master != null && !master.isDeleted()) {
            Iterator<ContigI> iter = null;
            try {
                iter = master.Contig().ByBin(bin);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            while (iter != null && iter.hasNext()) {
                tmp.add(iter.next());
            }
        }

        Collections.sort(tmp);
        addAll(tmp);
        fireContentsChanged();
    }
}
