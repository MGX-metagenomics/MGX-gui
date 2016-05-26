/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class AttributeTypeI extends Identifiable<AttributeTypeI> {

    public static final char VALUE_NUMERIC = 'N';
    public static final char VALUE_DISCRETE = 'D';
    //
    public static final char STRUCTURE_BASIC = 'B';
    public static final char STRUCTURE_HIERARCHICAL = 'H';
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(AttributeTypeI.class, "AttributeTypeI");

    public AttributeTypeI(MGXMasterI master) {
        super(master, DATA_FLAVOR);
    }

    public abstract String getName();

    public abstract void setName(String name);

    public abstract char getValueType();

    public abstract void setValueType(char value_type);

    public abstract char getStructure();

    public abstract AttributeTypeI setStructure(char structure);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    @Override
    public abstract int compareTo(AttributeTypeI o);

}
