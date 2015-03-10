package de.cebitec.mgx.gui.rarefaction;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Point;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class Rarefaction {

    public static Iterator<Point> rarefy(DistributionI<Long> dist) throws MGXException {
        MGXMasterI m = dist.getMaster();
        return m.Statistics().Rarefaction(dist);
    }

}
