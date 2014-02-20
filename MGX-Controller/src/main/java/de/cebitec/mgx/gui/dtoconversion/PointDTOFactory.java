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
public class PointDTOFactory extends DTOConversionBase<double[], PointDTO> {

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
    public PointDTO toDTO(double[] a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public double[] toModel(PointDTO dto) {
        return new double[]{dto.getX(), dto.getY()};
    }
}
