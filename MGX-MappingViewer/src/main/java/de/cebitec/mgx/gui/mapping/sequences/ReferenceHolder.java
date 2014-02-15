package de.cebitec.mgx.gui.mapping.sequences;

import de.cebitec.mgx.gui.datamodel.Reference;

/**
 *
 * @author ddoppmeier
 */
public class ReferenceHolder {

    private long id;
    private String name;
    private String sequence;
    private int refLength;
    private Reference reference;

    /**
     * Data holder for a reference genome..
     *
     * @param id The database id of the reference.
     * @param name The name of the reference.
     * @param description The additional description of the reference.
     * @param sequence The genome sequence of the reference.
     * @param timestamp The insertion timestamp of the reference.
     */
    public ReferenceHolder(Reference ref, String sequence) {
        this.id = ref.getId();
        this.reference = ref;
        this.name = ref.getName();
        this.refLength = sequence.length();
        this.sequence = sequence;
    }

    /**
     * @return The database id of the reference.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The name of the reference.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The genome sequence of the reference.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * @return the length of the reference sequence
     */
    public int getRefLength() {
        return refLength;
    }

    public Reference getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return name;
    }

    /*
     * Need this to use PersistantReference class as key for HashMap
     * @see http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) id;
    }

    /**
     * Checks if the given reference genome is equal to this one.
     *
     * @param o object to compare to this object
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ReferenceHolder) {
            ReferenceHolder ogenome = (ReferenceHolder) o;
            return (ogenome.getName().equals(this.name)
                    && (ogenome.getId() == this.id)
                    && (ogenome.getRefLength() == this.refLength));
        } else {
            return super.equals(o);
        }
    }
}
