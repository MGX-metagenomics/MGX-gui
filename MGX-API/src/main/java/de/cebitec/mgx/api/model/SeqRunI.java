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
public abstract class SeqRunI extends Identifiable<SeqRunI> {

    //

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(SeqRunI.class, "SeqRunI");

    public SeqRunI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract String getName();

    public abstract SeqRunI setName(String n);

    public abstract SeqRunI setDNAExtractId(long extractId);

    public abstract long getExtractId();

    public abstract String getAccession();

    public abstract SeqRunI setAccession(String database_accession);

    public abstract TermI getSequencingMethod();

    public abstract SeqRunI setSequencingMethod(TermI sequencing_method);

    public abstract TermI getSequencingTechnology();

    public abstract SeqRunI setSequencingTechnology(TermI sequencing_technology);

    public abstract boolean getSubmittedToINSDC();

    public abstract SeqRunI setSubmittedToINSDC(Boolean submitted_to_insdc);

    public abstract long getNumSequences();

    public abstract SeqRunI setNumSequences(long numSequences);

}
