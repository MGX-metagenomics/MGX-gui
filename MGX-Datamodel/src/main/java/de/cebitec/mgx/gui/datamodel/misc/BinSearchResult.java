/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.model.assembly.BinSearchResultI;

/**
 *
 * @author sj
 */
public class BinSearchResult implements BinSearchResultI {

    private final long contig_id;
    private final String contig_name;
    private final long region_id;
    private final String attr_name;
    private final String attrtype_value;

    public BinSearchResult(long contig_id, String contig_name, long region_id, String attr_name, String attrtype_value) {
        this.contig_id = contig_id;
        this.contig_name = contig_name;
        this.region_id = region_id;
        this.attr_name = attr_name;
        this.attrtype_value = attrtype_value;
    }

    @Override
    public long getContigId() {
        return contig_id;
    }

    @Override
    public String getContigName() {
        return contig_name;
    }

    @Override
    public long getRegionId() {
        return region_id;
    }

    @Override
    public String getAttributeName() {
        return attr_name;
    }

    @Override
    public String getAttributeTypeValue() {
        return attrtype_value;
    }

    @Override
    public String toString() {
        return "BinSearchResult{" + "contig_id=" + contig_id + ", contig_name=" + contig_name + ", region_id=" + region_id + ", attr_name=" + attr_name + ", attrtype_value=" + attrtype_value + '}';
    }

}
