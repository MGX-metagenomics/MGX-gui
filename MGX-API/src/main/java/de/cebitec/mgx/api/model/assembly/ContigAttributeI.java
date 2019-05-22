/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.model.AttributeI;

/**
 *
 * @author sj
 */
public abstract class ContigAttributeI extends AttributeI {

    @Override
    public abstract ContigAttributeTypeI getAttributeType();

    public abstract ContigAttributeTypeI setAttributeType(ContigAttributeTypeI atype);

    @Override
    public abstract String getValue();

    public abstract void setValue(String value);
}
