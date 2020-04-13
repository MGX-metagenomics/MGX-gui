/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.genbankexporter.impl;

import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.features.AbstractFeature;
import org.biojava.nbio.core.sequence.template.AbstractSequence;

/**
 *
 * @author sj
 */
public class SourceFeature extends AbstractFeature<AbstractSequence<NucleotideCompound>, NucleotideCompound> {

    public SourceFeature() {
        super("source", null);
    }

}
