package de.cebitec.mgx.api.groups;

/**
 *
 * @author sjaenick
 */
public interface SequenceExporterI {

    /*
     * returns: false if export was aborted/cancelled
     *          true otherwise
    */
    public boolean export();
}
