package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.MGXDTOMaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public abstract class AccessBase<T> {

    private MGXMaster master;
    private MGXDTOMaster dtomaster;

    public MGXDTOMaster getDTOmaster() {
        return dtomaster;
    }

    public void setDTOmaster(MGXDTOMaster dtomaster) {
        this.dtomaster = dtomaster;
    }

    public MGXMaster getMaster() {
        return master;
    }

    public void setMaster(MGXMaster master) {
        this.master = master;
    }

    public abstract Long create(T obj);

    public abstract T fetch(Long id);

    public abstract List<T> fetchall();
    
    public abstract void update(T obj);

    public abstract void delete(Long id);

    /*
     * from http://snippets.dzone.com/posts/show/91
     */
    protected static String join(Iterable< ? extends Object> pColl, String separator) {
        Iterator< ? extends Object> oIter;
        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
            return "";
        }
        StringBuilder oBuilder = new StringBuilder(String.valueOf(oIter.next()));
        while (oIter.hasNext()) {
            oBuilder.append(separator).append(oIter.next());
        }
        return oBuilder.toString();
    }

    protected static List<String> split(String message, String separator) {
        return new ArrayList<String>(Arrays.asList(message.split(separator)));
    }
}
