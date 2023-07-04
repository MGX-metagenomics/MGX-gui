/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.cebitec.mgx.gui.util;

import de.cebitec.mgx.api.misc.ChunkedIterator;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.dto.dto.ContigDTO;
import de.cebitec.mgx.dto.dto.ContigDTOList;
import de.cebitec.mgx.gui.dtoconversion.ContigDTOFactory;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public abstract class ChunkedContigIterator extends ChunkedIterator<ContigI, ContigDTOList, ContigDTO> {

    public ChunkedContigIterator(MGXMasterI master, ContigDTOList dtoList) {
        super(master, dtoList);
    }

    @Override
    public Iterator<ContigDTO> chunkIterator() {
        return currentChunk().getContigList().iterator();
    }

    @Override
    public ContigI convert(ContigDTO v) {
        return ContigDTOFactory.getInstance().toModel(getMaster(), v);
    }

    @Override
    public boolean isLastChunk(ContigDTOList u) {
        return u.getComplete();
    }

}
