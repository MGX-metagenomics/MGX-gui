package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.PointDTO;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.PointDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class StatisticsAccess extends AccessBase<Point> {

    public Iterator<Point> Rarefaction(Distribution dist) {
        try {
            Iterator<PointDTO> fetchall = getDTOmaster().Statistics().Rarefaction(dist.values());
            return new BaseIterator<PointDTO, Point>(fetchall) {
                @Override
                public Point next() {
                    Point h = PointDTOFactory.getInstance().toModel(iter.next());
                    return h;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    @Override
    public long create(Point obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Point fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<Point> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(Point obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task delete(Point obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

}
