/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.genbankexporter;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.GeneObservationI;
import de.cebitec.mgx.dnautils.DNAUtils;
import de.cebitec.mgx.gui.genbankexporter.impl.CDSFeature;
import de.cebitec.mgx.gui.genbankexporter.impl.GBKHeaderFormatter;
import de.cebitec.mgx.gui.genbankexporter.impl.GeneFeature;
import de.cebitec.mgx.gui.genbankexporter.impl.SourceFeature;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.biojava.nbio.core.sequence.AccessionID;
import org.biojava.nbio.core.sequence.ChromosomeSequence;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.DNASequence.DNAType;
import org.biojava.nbio.core.sequence.Strand;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.features.Qualifier;
import org.biojava.nbio.core.sequence.io.GenbankWriter;
import org.biojava.nbio.core.sequence.location.SimpleLocation;

/**
 *
 * @author sj
 */
public class GBKExporter {

    private static final List<String> validTags = new ArrayList<>();

    static {
        validTags.add("EC_number");
    }

    public static void exportContig(File targetFile, ContigI contig) throws Exception {
        exportContig(targetFile, contig, null, false);
    }

    public static void exportContig(File targetFile, ContigI contig, String taxAssignment) throws Exception {
        exportContig(targetFile, contig, taxAssignment, false);
    }

    public static void exportContig(File targetFile, ContigI contig, String taxAssignment, boolean append) throws Exception {

        OutputStream out = new FileOutputStream(targetFile, append);
        ArrayList<DNASequence> seqs = new ArrayList<>();

        MGXMasterI master = contig.getMaster();
        SequenceI dnaSequence = master.Contig().getDNASequence(contig);

        ChromosomeSequence ctg = new ChromosomeSequence(dnaSequence.getSequence());
        ctg.setAccession(new AccessionID(contig.getName()));
        ctg.setDNAType(DNAType.UNKNOWN);
        ctg.setDescription(contig.getName());

        if (taxAssignment != null) {
            ctg.setSource(taxAssignment);
        }

        // add source feature
        SourceFeature sourceFeat = new SourceFeature();
        if (taxAssignment != null && taxAssignment.lastIndexOf(";") != -1) {
            String orgName = taxAssignment.substring(taxAssignment.lastIndexOf(";") + 2);
            sourceFeat.addQualifier("organism", new Qualifier("organism", orgName, true));
        }
        ctg.addFeature(1, contig.getLength(), sourceFeat);

        Iterator<AssembledRegionI> geneIter = master.AssembledRegion().ByContig(contig);
        while (geneIter != null && geneIter.hasNext()) {
            AssembledRegionI gene = geneIter.next();

            GeneFeature geneFeat = new GeneFeature(contig.getName() + "_" + gene.getId());
            CDSFeature cdsFeat = new CDSFeature(contig.getName() + "_" + gene.getId());

            SimpleLocation loc;
            if (gene.getFrame() > 0) {
                loc = new SimpleLocation(gene.getStart() + 1, gene.getStop() + 1);
            } else {
                loc = new SimpleLocation(gene.getStop() + 1, gene.getStart() + 1, Strand.NEGATIVE);
            }

            geneFeat.setLocation(loc);
            cdsFeat.setLocation(loc);

            LinkedHashMap<String, String> observations = new LinkedHashMap<>();
            Iterator<GeneObservationI> gobsIter = master.GeneObservation().ByGene(gene);
            while (gobsIter != null && gobsIter.hasNext()) {
                GeneObservationI obs = gobsIter.next();

                // skip NCBI_ taxonomy assignments
                if (!obs.getAttributeTypeName().startsWith("NCBI_")) {
                    if (observations.containsKey(obs.getAttributeTypeName())) {
                        String tmp = observations.get(obs.getAttributeTypeName());
                        observations.put(obs.getAttributeTypeName(), tmp + ", " + obs.getAttributeName());
                    } else {
                        observations.put(obs.getAttributeTypeName(), obs.getAttributeName());
                    }
                }
            }

            for (Map.Entry<String, String> me : observations.entrySet()) {
                String tagName = me.getKey();
                String tagValue = me.getValue();
                if (!validTags.contains(tagName)) {
                    tagValue = tagName + ": " + tagValue;
                    tagName = "note";
                }
                cdsFeat.addQualifier(tagName, new Qualifier(tagName, tagValue, true));
            }

            SequenceI dnaSequence1 = master.AssembledRegion().getDNASequence(gene);
            String aaSeq = DNAUtils.translate(dnaSequence1.getSequence());
            cdsFeat.addQualifier("translation", new Qualifier("translation", aaSeq, true));

            ctg.addFeature(geneFeat);
            ctg.addFeature(cdsFeat);

        }

        seqs.add(ctg);

        GenbankWriter<DNASequence, NucleotideCompound> gbw = new GenbankWriter<>(out, seqs, new GBKHeaderFormatter());
        gbw.process();

        //GenbankWriterHelper.writeSequence(targetFile, ctg);
        //GenbankWriterHelper.writeNucleotideSequence(out, seqs,
        //        GenbankWriterHelper.LINEAR_DNA);
    }

}
