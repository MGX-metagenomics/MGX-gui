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
public abstract class SequenceI implements Comparable<SequenceI> { //extends Identifiable<SequenceI> {

    public static final long INVALID_IDENTIFIER = Identifiable.INVALID_IDENTIFIER;
    protected long id = INVALID_IDENTIFIER;

    public final void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getSequence();

    public abstract void setSequence(String sequence);

    public abstract int getLength();

    public abstract void setLength(int length);

    @Override
    public int hashCode() {
        int hash = 0;
        hash = (int) (31 * hash + this.id);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Identifiable) {
            Identifiable<?> other = (Identifiable) o;

            if ((this.id == INVALID_IDENTIFIER && other.getId() != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.getId())) {
                return false;
            }

            return this.getId() == other.getId();
        }
        return false;
    }

}
