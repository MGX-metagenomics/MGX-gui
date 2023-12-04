/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.genbankexporter.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.Strand;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.features.DBReferenceInfo;
import org.biojava.nbio.core.sequence.features.FeatureInterface;
import org.biojava.nbio.core.sequence.features.Qualifier;
import org.biojava.nbio.core.sequence.io.GenericGenbankHeaderFormat;
import org.biojava.nbio.core.sequence.location.template.AbstractLocation;
import org.biojava.nbio.core.sequence.location.template.Location;
import org.biojava.nbio.core.sequence.location.template.Point;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.core.util.StringManipulationHelper;

/**
 *
 * @author sj
 */
public class GBKHeaderFormatter extends GenericGenbankHeaderFormat<DNASequence, NucleotideCompound> {

    private static final String lineSep = "\n";
    private static final int HEADER_WIDTH = 12;

    private String _write_the_first_line(DNASequence sequence) {
        /*
		 * locus = record.name if not locus or locus == "<unknown name>": locus
		 * = record.id if not locus or locus == "<unknown id>": locus =
		 * self._get_annotation_str(record, "accession", just_first=True)\
         */
        String locus;
        try {
            locus = sequence.getAccession().getID();
        } catch (Exception e) {
            locus = "";
        }
        if (locus.length() > 16) {
            throw new RuntimeException("Locus identifier " + locus
                    + " is too long");
        }

        String units = "";
        String mol_type = "";
        if (sequence.getCompoundSet() instanceof DNACompoundSet) {
            units = "bp";
            mol_type = "DNA";
        } else if (sequence.getCompoundSet() instanceof DNACompoundSet) {
            units = "bp";
            mol_type = "RNA";
        } else {
            throw new RuntimeException(
                    "Need a DNACompoundSet, RNACompoundSet");
        }

        String division = _get_data_division(sequence);

        assert units.length() == 2;

        // the next line does not seem right.. seqType == linear
        // uncommenting for now
        //assert division.length() == 3;
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter
                .format("LOCUS       %s %s %s    %s           %s %s" + lineSep,
                        StringManipulationHelper.padRight(locus, 16),
                        StringManipulationHelper.padLeft(
                                Integer.toString(sequence.getLength()), 11),
                        units, StringManipulationHelper.padRight(mol_type, 6), division,
                        _get_date(sequence));
        String output = formatter.toString();
        formatter.close();
        return output;
        /*
		 * assert len(line) == 79+1, repr(line) #plus one for new line
		 *
		 * assert line[12:28].rstrip() == locus, \ 'LOCUS line does not contain
		 * the locus at the expected position:\n' + line assert line[28:29] ==
		 * " " assert line[29:40].lstrip() == str(len(record)), \ 'LOCUS line
		 * does not contain the length at the expected position:\n' + line
		 *
		 * #Tests copied from Bio.GenBank.Scanner assert line[40:44] in [' bp ',
		 * ' aa '] , \ 'LOCUS line does not contain size units at expected
		 * position:\n' + line assert line[44:47] in [' ', 'ss-', 'ds-', 'ms-'],
		 * \ 'LOCUS line does not have valid strand type (Single stranded,
		 * ...):\n' + line assert line[47:54].strip() == "" \ or
		 * line[47:54].strip().find('DNA') != -1 \ or
		 * line[47:54].strip().find('RNA') != -1, \ 'LOCUS line does not contain
		 * valid sequence type (DNA, RNA, ...):\n' + line assert line[54:55] ==
		 * ' ', \ 'LOCUS line does not contain space at position 55:\n' + line
		 * assert line[55:63].strip() in ['', 'linear', 'circular'], \ 'LOCUS
		 * line does not contain valid entry (linear, circular, ...):\n' + line
		 * assert line[63:64] == ' ', \ 'LOCUS line does not contain space at
		 * position 64:\n' + line assert line[67:68] == ' ', \ 'LOCUS line does
		 * not contain space at position 68:\n' + line assert line[70:71] ==
		 * '-', \ 'LOCUS line does not contain - at position 71 in date:\n' +
		 * line assert line[74:75] == '-', \ 'LOCUS line does not contain - at
		 * position 75 in date:\n' + line
         */
    }

    private String _get_data_division(DNASequence sequence) {
        return UNKNOWN_DNA;
        /*
		 * try: division = record.annotations["data_file_division"] except
		 * KeyError: division = "UNK" if division in ["PRI", "ROD", "MAM",
		 * "VRT", "INV", "PLN", "BCT", "VRL", "PHG", "SYN", "UNA", "EST", "PAT",
		 * "STS", "GSS", "HTG", "HTC", "ENV", "CON"]: #Good, already GenBank
		 * style # PRI - primate sequences # ROD - rodent sequences # MAM -
		 * other mammalian sequences # VRT - other vertebrate sequences # INV -
		 * invertebrate sequences # PLN - plant, fungal, and algal sequences #
		 * BCT - bacterial sequences [plus archea] # VRL - viral sequences # PHG
		 * - bacteriophage sequences # SYN - synthetic sequences # UNA -
		 * unannotated sequences # EST - EST sequences (expressed sequence tags)
		 * # PAT - patent sequences # STS - STS sequences (sequence tagged
		 * sites) # GSS - GSS sequences (genome survey sequences) # HTG - HTGS
		 * sequences (high throughput genomic sequences) # HTC - HTC sequences
		 * (high throughput cDNA sequences) # ENV - Environmental sampling
		 * sequences # CON - Constructed sequences # #(plus UNK for unknown)
		 * pass else: #See if this is in EMBL style: # Division Code #
		 * ----------------- ---- # Bacteriophage PHG - common # Environmental
		 * Sample ENV - common # Fungal FUN - map to PLN (plants + fungal) #
		 * Human HUM - map to PRI (primates) # Invertebrate INV - common # Other
		 * Mammal MAM - common # Other Vertebrate VRT - common # Mus musculus
		 * MUS - map to ROD (rodent) # Plant PLN - common # Prokaryote PRO - map
		 * to BCT (poor name) # Other Rodent ROD - common # Synthetic SYN -
		 * common # Transgenic TGN - ??? map to SYN ??? # Unclassified UNC - map
		 * to UNK # Viral VRL - common # #(plus XXX for submiting which we can
		 * map to UNK) embl_to_gbk = {"FUN":"PLN", "HUM":"PRI", "MUS":"ROD",
		 * "PRO":"BCT", "UNC":"UNK", "XXX":"UNK", } try: division =
		 * embl_to_gbk[division] except KeyError: division = "UNK" assert
		 * len(division)==3 return division
         */
    }

    @Override
    public String getHeader(DNASequence sequence) {
        String header = _write_the_first_line(sequence);
        String acc_with_version;
        String accession;
        try {
            acc_with_version = sequence.getAccession().getID();
            accession = acc_with_version.split("\\.", 1)[0];
        } catch (Exception e) {
            acc_with_version = "";
            accession = "";
        }
        String description = sequence.getDescription();
        if ("<unknown description>".equals(description) || description == null) {
            description = ".";
        }
        header += _write_multi_line("DEFINITION", description);
        header += _write_multi_line("ACCESSION", accession);
        header += _write_multi_line("VERSION", acc_with_version);

        /*
		 * gi = self._get_annotation_str(record, "gi", just_first=True)
		 *
		 * self._write_single_line("ACCESSION", accession) if gi != ".":
		 * self._write_single_line("VERSION", "%s  GI:%s" \ % (acc_with_version,
		 * gi)) else: self._write_single_line("VERSION", "%s" %
		 * (acc_with_version))
		 *
		 * #The NCBI only expect two types of link so far, #e.g. "Project:28471"
		 * and "Trace Assembly Archive:123456" #TODO - Filter the dbxrefs list
		 * to just these? self._write_multi_entries("DBLINK", record.dbxrefs)
		 *
		 * try: #List of strings #Keywords should be given separated with semi
		 * colons, keywords = "; ".join(record.annotations["keywords"]) #with a
		 * trailing period: if not keywords.endswith(".") : keywords += "."
		 * except KeyError: #If no keywords, there should be just a period:
		 * keywords = "."
         */
        header += _write_multi_line("KEYWORDS", ".");

        /*
		 * if "segment" in record.annotations: #Deal with SEGMENT line found
		 * only in segmented records, #e.g. AH000819 segment =
		 * record.annotations["segment"] if isinstance(segment, list): assert
		 * len(segment)==1, segment segment = segment[0]
		 * self._write_single_line("SEGMENT", segment)
		 *
		 * self._write_multi_line("SOURCE", \ self._get_annotation_str(record,
		 * "source"))
         */
        String taxAssignment = sequence.getSource();
        if (taxAssignment.lastIndexOf(";") != -1) {
            String orgName = taxAssignment.substring(taxAssignment.lastIndexOf(";") + 2);
            header += _write_single_line("SOURCE", orgName);
            header += _write_single_line("  ORGANISM", orgName);

            String lineage = taxAssignment.substring(0, taxAssignment.lastIndexOf(";")) + ".";
            header += _write_single_line("", lineage);

        }
        /*
		 * #The ORGANISM line MUST be a single line, as any continuation is the
		 * taxonomy org = self._get_annotation_str(record, "organism") if
		 * len(org) > self.MAX_WIDTH - self.HEADER_WIDTH: org =
		 * org[:self.MAX_WIDTH - self.HEADER_WIDTH-4]+"..."
		 * self._write_single_line("  ORGANISM", org) try: #List of strings
		 * #Taxonomy should be given separated with semi colons, taxonomy =
		 * "; ".join(record.annotations["taxonomy"]) #with a trailing period: if
		 * not taxonomy.endswith(".") : taxonomy += "." except KeyError:
		 * taxonomy = "." self._write_multi_line("", taxonomy)
		 *
		 * if "references" in record.annotations: self._write_references(record)
         */
        if (!sequence.getNotesList().isEmpty()) {
            header += _write_comment(sequence);
        }

        header += "FEATURES             Location/Qualifiers" + lineSep;
        int rec_length = sequence.getLength();
        for (FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound> feature : sequence
                .getFeatures()) {
            header += _write_single_feature(feature, rec_length);
        }

        return header;
    }

    private String _get_date(DNASequence sequence) {
        Date sysdate = Calendar.getInstance().getTime();

        // String default_date =
        // sysdate.get(Calendar.DAY_OF_MONTH)+"-"+sysdate.get(Calendar.MONTH)+"-"+sysdate.get(Calendar.YEAR);
        String default_date = new SimpleDateFormat("dd-MMM-yyyy")
                .format(sysdate);
        return default_date;
        /*
		 * try : date = record.annotations["date"] except KeyError : return
		 * default #Cope with a list of one string: if isinstance(date, list)
		 * and len(date)==1 : date = date[0] #TODO - allow a Python date object
		 * if not isinstance(date, str) or len(date) != 11 \ or date[2] != "-"
		 * or date[6] != "-" \ or not date[:2].isdigit() or not
		 * date[7:].isdigit() \ or int(date[:2]) > 31 \ or date[3:6] not in
		 * ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP",
		 * "OCT", "NOV", "DEC"] : #TODO - Check is a valid date (e.g. not 31
		 * Feb) return default return date
         */
    }

    private String _write_comment(DNASequence sequence) {
        ArrayList<String> comments = sequence.getNotesList();
        String output = _write_multi_line("COMMENT", comments.remove(0));
        for (String comment : comments) {
            output += _write_multi_line("", comment);
        }

        return output;
    }

    private String _write_multi_line(String tag, String text) {
        if (text == null) {
            text = "";
        }
        int max_len = MAX_WIDTH - HEADER_WIDTH;
        ArrayList<String> lines = _split_multi_line(text, max_len);
        String output = _write_single_line(tag, lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            output += _write_single_line("", lines.get(i));
        }
        return output;
    }

    private String _write_single_line(String tag, String text) {
        assert tag.length() < HEADER_WIDTH;
        return StringManipulationHelper.padRight(tag, HEADER_WIDTH)
                + text.replace('\n', ' ') + lineSep;
    }

    private String _write_single_feature(FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound> feature, int record_length) {
        String location = _insdc_feature_location_string(feature, record_length);
        String f_type = feature.getType().replace(" ", "_");
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(QUALIFIER_INDENT_TMP, f_type);
        String line = formatter.toString().substring(0, QUALIFIER_INDENT) + _wrap_location(location) + lineSep;
        formatter.close();

        //Now the qualifiers...
        for (List<Qualifier> qualifiers : feature.getQualifiers().values()) {
            for (Qualifier q : qualifiers) {
                if (q instanceof DBReferenceInfo) {
                    DBReferenceInfo db = (DBReferenceInfo) q;
                    line += _write_feature_qualifier(q.getName().replaceAll("%", "%%"), db.getDatabase().replaceAll("%", "%%") + ":" + db.getId().replaceAll("%", "%%"), db.needsQuotes());
                } else {
                    line += _write_feature_qualifier(q.getName().replaceAll("%", "%%"), q.getValue().replaceAll("%", "%%"), q.needsQuotes());
                }
            }
        }
        return line;
        /*
		self.handle.write(line)
		#Now the qualifiers...
		for key, values in feature.qualifiers.items():
			if isinstance(values, list) or isinstance(values, tuple):
				for value in values:
					self._write_feature_qualifier(key, value)
			elif values:
				#String, int, etc
				self._write_feature_qualifier(key, values)
			else:
				#e.g. a /psuedo entry
				self._write_feature_qualifier(key)
         */
    }

    private String _insdc_feature_location_string(FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound> feature, int record_length) {
        if (feature.getChildrenFeatures().isEmpty()) {
            if (feature.getLocations().getSubLocations().isEmpty()) {
                //Non-recursive.
                String location = _insdc_location_string_ignoring_strand_and_subfeatures(feature.getLocations(), record_length);
                if (feature.getLocations().getStrand() == Strand.NEGATIVE) {
                    StringBuilder sb = new StringBuilder();
                    Formatter formatter = new Formatter(sb, Locale.US);
                    formatter.format("complement(%s)", location);
                    String output = formatter.toString();
                    formatter.close();
                    location = output;
                }
                return location;

            } else if (feature.getLocations().getStrand() == Strand.NEGATIVE) {

                // As noted above, treat reverse complement strand features carefully:
                // check if any of the sublocations strand differs from the parent features strand
                for (Location l : feature.getLocations().getSubLocations()) {
                    if (l.getStrand() != Strand.NEGATIVE) {
                        StringBuilder sb = new StringBuilder();
                        Formatter formatter = new Formatter(sb, Locale.US);
                        formatter.format("Inconsistent strands: %s for parent, %s for child",
                                feature.getLocations().getStrand(), l.getStrand());
                        String output = formatter.toString();
                        formatter.close();
                        throw new RuntimeException(output);
                    }
                }

                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                ArrayList<String> locations = new ArrayList<>();
                for (Location l : feature.getLocations().getSubLocations()) {
                    locations.add(_insdc_location_string_ignoring_strand_and_subfeatures((AbstractLocation) l, record_length));
                }
                String location = StringManipulationHelper.join(locations, ",");
                formatter.format("complement(%s(%s))", /* feature.location_operator */ "join", location);
                String output = formatter.toString();
                formatter.close();
                return output;

            } else {
                //Convert feature sub-locations into joins
                //This covers typical forward strand features, and also an evil mixed strand:
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                ArrayList<String> locations = new ArrayList<>();
                for (Location l : feature.getLocations().getSubLocations()) {
                    locations.add(_insdc_location_string_ignoring_strand_and_subfeatures((AbstractLocation) l, record_length));
                }
                String location = StringManipulationHelper.join(locations, ",");
                formatter.format("%s(%s)", /*feature.location_operator*/ "join", location);
                String output = formatter.toString();
                formatter.close();
                return output;
            }
        }
        // As noted above, treat reverse complement strand features carefully:
        if (feature.getLocations().getStrand() == Strand.NEGATIVE) {
            for (FeatureInterface<?, ?> f : feature.getChildrenFeatures()) {
                if (f.getLocations().getStrand() != Strand.NEGATIVE) {
                    StringBuilder sb = new StringBuilder();
                    Formatter formatter = new Formatter(sb, Locale.US);
                    formatter.format("Inconsistent strands: %s for parent, %s for child", feature.getLocations().getStrand(), f.getLocations().getStrand());
                    String output = formatter.toString();
                    formatter.close();
                    throw new RuntimeException(output);
                }
            }
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            ArrayList<String> locations = new ArrayList<>();
            for (FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound> f : feature.getChildrenFeatures()) {
                locations.add(_insdc_location_string_ignoring_strand_and_subfeatures(f.getLocations(), record_length));
            }
            String location = StringManipulationHelper.join(locations, ",");
            formatter.format("complement(%s(%s))", /*feature.location_operator*/ "join", location);
            String output = formatter.toString();
            formatter.close();
            return output;
        }
        //This covers typical forward strand features, and also an evil mixed strand:
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        ArrayList<String> locations = new ArrayList<>();
        for (FeatureInterface<AbstractSequence<NucleotideCompound>, NucleotideCompound> f : feature.getChildrenFeatures()) {
            locations.add(_insdc_location_string_ignoring_strand_and_subfeatures(f.getLocations(), record_length));
        }
        String location = StringManipulationHelper.join(locations, ",");
        formatter.format("%s(%s)", /*feature.location_operator*/ "join", location);
        String output = formatter.toString();
        formatter.close();
        return output;
    }

    private String _insdc_location_string_ignoring_strand_and_subfeatures(
            //SequenceLocation<AbstractSequence<C>, C> sequenceLocation,
            AbstractLocation sequenceLocation,
            int record_length) {
        /*
	if location.ref:
		ref = "%s:" % location.ref
	else:
		ref = ""
	assert not location.ref_db
         */
        String ref = "";
        if (!sequenceLocation.getStart().isUncertain() && !sequenceLocation.getEnd().isUncertain() && sequenceLocation.getStart() == sequenceLocation.getEnd()) {
            //Special case, for 12:12 return 12^13
            //(a zero length slice, meaning the point between two letters)
            if (sequenceLocation.getEnd().getPosition() == record_length) {
                //Very special case, for a between position at the end of a
                //sequence (used on some circular genomes, Bug 3098) we have
                //N:N so return N^1
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%s%d^1", ref, record_length);
                String output = formatter.toString();
                formatter.close();
                return output;
            } else {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%s%d^%d", ref, sequenceLocation.getStart().getPosition(), sequenceLocation.getEnd().getPosition());
                String output = formatter.toString();
                formatter.close();
                return output;
            }
        }
        if (!sequenceLocation.getStart().isUncertain() && !sequenceLocation.getEnd().isUncertain() && sequenceLocation.getStart().getPosition() + 1 == sequenceLocation.getEnd().getPosition()) {
            //Special case, for 11:12 return 12 rather than 12..12
            //(a length one slice, meaning a single letter)
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%s%d", ref, sequenceLocation.getEnd().getPosition());
            String output = formatter.toString();
            formatter.close();
            return output;
        } else if (sequenceLocation.getStart().isUnknown() || sequenceLocation.getEnd().isUnknown()) {
            //Special case for features from SwissProt/UniProt files
            if (sequenceLocation.getStart().isUnknown() && sequenceLocation.getEnd().isUnknown()) {
                throw new RuntimeException("Feature with unknown location");
            } else if (sequenceLocation.getStart().isUnknown()) {
                //Treat the unknown start position as a BeforePosition
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%s<%d..%s", ref, sequenceLocation.getEnd().getPosition(), _insdc_feature_position_string(sequenceLocation.getEnd()));
                String output = formatter.toString();
                formatter.close();
                return output;
            } else {
                //Treat the unknown start position as an AfterPosition
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%s%s..>%d", ref, _insdc_feature_position_string(sequenceLocation.getStart()), sequenceLocation.getStart().getPosition());
                String output = formatter.toString();
                formatter.close();
                return output;
            }
        } else {
            //Typical case, e.g. 12..15 gets mapped to 11:15
            String start = _insdc_feature_position_string(sequenceLocation.getStart());
            String end = _insdc_feature_position_string(sequenceLocation.getEnd());

            if (sequenceLocation.isPartial()) {
                if (sequenceLocation.isPartialOn5prime()) {
                    start = "<" + start;
                }

                if (sequenceLocation.isPartialOn3prime()) {
                    end = ">" + end;
                }
            }

            return ref + start + ".." + end;
        }
    }

    private String _insdc_feature_position_string(Point location) {
        // TODO Auto-generated method stub
        return _insdc_feature_position_string(location, 0);
    }

    private String _insdc_feature_position_string(Point location, int increment) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%s", location.getPosition() + increment);
        String output = formatter.toString();
        formatter.close();
        return output;
    }

    private String _wrap_location(String location) {
        int length = MAX_WIDTH - QUALIFIER_INDENT;
        if (location.length() <= length) {
            return location;
        }
        int index = location.substring(0, length).lastIndexOf(",");
        if (-1 == index) {
            //No good place to split (!)
            return location;
        }
        return location.substring(0, index + 1) + lineSep + QUALIFIER_INDENT_STR + _wrap_location(location.substring(index + 1));
    }

    private String _write_feature_qualifier(String key, String value, boolean quote) {
        String line = "";
        if (null == value) {
            line = QUALIFIER_INDENT_STR + "/" + key + lineSep;
            return line;
        }
        if (quote) {  // quote should be true for numerics
            line = QUALIFIER_INDENT_STR + "/" + key + "=\"" + value + "\"";
        } else {
            line = QUALIFIER_INDENT_STR + "/" + key + "=" + value;
        }
        if (line.length() <= MAX_WIDTH) {
            return line + lineSep;
        }
        String goodlines = "";
        while (!"".equals(line.replaceAll("^\\s+", ""))) {
            if (line.length() <= MAX_WIDTH) {
                goodlines += line + lineSep;
                break;
            }
            //Insert line break...
            int index;
            for (index = Math.min(line.length() - 1, MAX_WIDTH); index > QUALIFIER_INDENT; index--) {
                if (' ' == line.charAt(index)) {
                    break;
                }
            }
            if (' ' != line.charAt(index)) {
                //no nice place to break...
                index = MAX_WIDTH;
            }
            assert index <= MAX_WIDTH;
            goodlines += line.substring(0, index) + lineSep;
            line = QUALIFIER_INDENT_STR + line.substring(index).replaceAll("^\\s+", "");
        }
        return goodlines;
    }
}
