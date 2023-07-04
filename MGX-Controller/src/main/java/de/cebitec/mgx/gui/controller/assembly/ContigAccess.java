/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.access.ContigAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.ContigDTO;
import de.cebitec.mgx.dto.dto.ContigDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.controller.MasterHolder;
import de.cebitec.mgx.gui.dtoconversion.ContigDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.gui.util.ChunkedContigIterator;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public class ContigAccess extends MasterHolder implements ContigAccessI {//extends AccessBase<ContigI> implements ContigAccessI {

    public ContigAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public ContigI fetch(long id) throws MGXException {
        ContigDTO dto = null;
        try {
            dto = getDTOmaster().Contig().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return ContigDTOFactory.getInstance().toModel(getMaster(), dto);
    }

    @Override
    public Iterator<ContigI> fetchall() throws MGXException {
        ContigDTOList dto;
        try {
            dto = getDTOmaster().Contig().fetchall();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }

        return new ChunkedContigIterator(getMaster(), dto) {

            @Override
            public ContigDTOList nextChunk() throws MGXException {
                return continueSession(UUID.fromString(currentChunk().getUuid()));
            }

        };
    }

    @Override
    public Iterator<ContigI> ByBin(BinI bin) throws MGXException {
        ContigDTOList dto;
        try {
            dto = getDTOmaster().Contig().byBin(bin.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }

        return new ChunkedContigIterator(getMaster(), dto) {

            @Override
            public ContigDTOList nextChunk() throws MGXException {
                return continueSession(UUID.fromString(currentChunk().getUuid()));
            }

        };
    }

    private ContigDTOList continueSession(UUID sessionId) throws MGXException {
        try {
            return getDTOmaster().Contig().continueSession(sessionId);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public SequenceI getDNASequence(ContigI contig) throws MGXException {
        try {
            SequenceDTO dto = getDTOmaster().Contig().getDNASequence(contig.getId());
            return SequenceDTOFactory.getInstance().toModel(getMaster(), dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

//    @Override
//    public void update(ContigI obj) throws MGXException {
//        throw new UnsupportedOperationException("Not supported.");
//    }
//
//    @Override
//    public TaskI<ContigI> delete(ContigI obj) throws MGXException {
//        throw new UnsupportedOperationException("Not supported.");
//    }
}
