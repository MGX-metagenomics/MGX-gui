package de.cebitec.mgx.api.groups;

/**
 *
 * @author sjaenick
 */
public enum FileType {

    PNG("Portable Network Graphics (.png)", "png"),
    JPEG("JPEG image (.jpg)", "jpg", "jpeg"),
    SVG("Scalable Vector Graphics (.svg)", "svg"),
    FAS("FASTA format (.fas)", "fas", "fna", "fa", "fasta"),
    SFF("Standard flowgram format (.sff)", "sff"),
    FASGZ("Compressed FASTA format (.fas.gz)", "fas.gz", "fna.gz", "fa.gz", "fasta.gz"),
    FASTQ("FASTQ format (.fastq, .fq)", "fastq", "fq"),
    FASTQGZ("Compressed FASTQ format (.fastq.gz, .fq.gz)", "fastq.gz", "fq.gz"),
    EMBLGENBANK("GenBank/EMBL format (.embl, .gbk)", "embl", "gbk"),
    XML("Conveyor graph definition", "xml"),
    TSV("tab-separated values", "tsv"),
    MGS("MGX gold standard (.mgs)", "mgs");
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
