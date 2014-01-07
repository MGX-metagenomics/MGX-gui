package de.cebitec.mgx.gui.rarefaction;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class Rarefaction {

    public static Iterator<Point> rarefy(Distribution dist) {
        MGXMaster m = (MGXMaster) dist.getMaster();
        return m.Statistics().Rarefaction(dist);
    }

}
