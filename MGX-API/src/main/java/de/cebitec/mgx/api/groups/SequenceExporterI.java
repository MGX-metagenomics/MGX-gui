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

    public interface Provider {

        /**
         * @return exporter instances able to export (sub)sequences from the
         * chart
         */
        public SequenceExporterI[] getSequenceExporters();
    }
}
