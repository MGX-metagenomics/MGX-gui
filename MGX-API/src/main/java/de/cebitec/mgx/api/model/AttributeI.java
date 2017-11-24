/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

/**
 *
 * @author sj
 */
public abstract class AttributeI implements Comparable<AttributeI> { //extends MGXDataModelBase<AttributeI> {

    //
    //public static final DataFlavor DATA_FLAVOR = new DataFlavor(AttributeI.class, "AttributeI");
    protected long id = Identifiable.INVALID_IDENTIFIER;

    public AttributeI() { //MGXMasterI m) {
        //super(null, null);
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    public abstract long getParentID();

    public abstract void setParentID(long parent_id);

    public abstract AttributeTypeI getAttributeType();

    public abstract AttributeI setAttributeType(AttributeTypeI atype);

    public abstract String getValue();

    public abstract AttributeI setValue(String value);

    public abstract long getJobId();

    public abstract AttributeI setJobId(long job_id);

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int compareTo(AttributeI o);

}
