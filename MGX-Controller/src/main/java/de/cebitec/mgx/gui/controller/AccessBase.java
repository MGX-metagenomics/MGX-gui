package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.datamodel.misc.Task;
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

    public abstract long create(T obj);

    public abstract T fetch(long id);

    public abstract Iterator<T> fetchall();

    public abstract void update(T obj);

    public abstract Task delete(T obj);

//    /*
//     * from http://snippets.dzone.com/posts/show/91
//     */
//    protected static String join(Iterable< ? extends Object> pColl, String separator) {
//        Iterator< ? extends Object> oIter;
//        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
//            return "";
//        }
//        StringBuilder oBuilder = new StringBuilder(String.valueOf(oIter.next()));
//        while (oIter.hasNext()) {
//            oBuilder.append(separator).append(oIter.next());
//        }
//        return oBuilder.toString();
//    }

    protected static List<String> split(String message, String separator) {
        return Arrays.asList(message.split(separator));
        //return new ArrayList<>(Arrays.asList(message.split(separator)));
    }
}
