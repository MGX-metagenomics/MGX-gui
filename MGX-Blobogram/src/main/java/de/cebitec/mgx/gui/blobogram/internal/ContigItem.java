/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.blobogram.internal;

import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.Locale;
import org.jfree.data.xy.XYDataItem;

/**
 *
 * @author sj
 */
public class ContigItem extends XYDataItem {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final NumberFormat nf = NumberFormat.getInstance(Locale.US);
    private final ContigI contig;
    private final BinI bin;

    public ContigItem(BinI bin, ContigI ctg) {
        super(ctg.getGC(), 1d * ctg.getCoverage() / (1d * ctg.getLength() / 1000));
        this.contig = ctg;
        this.bin = bin;
    }

    public final ContigI getContig() {
        return contig;
    }

    public final String getTooltip() {
        return "<html><b>Contig: " + contig.getName() + "</b><br><hr><br>" + "Bin: " + bin.getName() + "<br>" + "Length: " + nf.format(contig.getLength()) + " bp<br>" + "Taxonomy: " + bin.getTaxonomy() + "</html>";
    }

    @Override
    public String toString() {
        return "ContigItem{" + "contig=" + contig.getName() + ", bin=" + bin.getName() + '}';
    }

}
