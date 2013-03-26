package de.cebitec.mgx.gui.util;

/**
 *
 * @author sjaenick
 */
public enum FileType {

    PNG("Portable Network Graphics (.png)", "png"),
    JPEG("JPEG image (.jpg)", "jpg", "jpeg"),
    SVG("Scalable Vector Graphics (.svg)", "svg"),
    FAS("FASTA format (.fas)", "fas", "fna", "fa", "fasta"),
    FASGZ("Compressed FASTA format (.fas.gz)", "fas.gz", "fna.gz", "fa.gz", "fasta.gz"),
    FASTQ("FASTQ format (.fastq, .fq)", "fastq", "fq"),
    FASTQGZ("Compressed FASTQ format (.fastq.gz, .fq.gz)", "fastq.gz", "fq.gz"),
    XML("Conveyor graph definition", "xml");
    private final String[] suffices;
    private final String description;

    private FileType(String description, String... suffix) {
        this.suffices = suffix;
        this.description = description;
    }

    public String[] getSuffices() {
        return suffices;
    }

    public String getDescription() {
        return description;
    }
}
