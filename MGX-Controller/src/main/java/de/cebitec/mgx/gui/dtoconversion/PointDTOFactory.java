/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.PointDTO;
import de.cebitec.mgx.gui.datamodel.misc.Point;

/**
 *
 * @author sj
 */
public class PointDTOFactory extends DTOConversionBase<Point, PointDTO> {

    static {
        instance = new PointDTOFactory();
    }
    protected static PointDTOFactory instance;

    private PointDTOFactory() {
    }

    public static PointDTOFactory getInstance() {
        return instance;
    }

    @Override
    public PointDTO toDTO(Point a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Point toModel(PointDTO dto) {
        return new Point(dto.getX(), dto.getY());
    }
}
