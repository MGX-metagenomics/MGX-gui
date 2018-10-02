package de.cebitec.mgx.api.groups;

/**
 *
 * @author sjaenick
 */
public interface ImageExporterI {

    public enum Result {
        SUCCESS,
        ERROR,
        ABORT;
    }

    public FileType[] getSupportedTypes();

    public Result export(FileType type, String fName) throws Exception;

    public interface Provider {

        /**
         * @return exporter instance able to save the visualization
         */
        public ImageExporterI getImageExporter();

    }
}
