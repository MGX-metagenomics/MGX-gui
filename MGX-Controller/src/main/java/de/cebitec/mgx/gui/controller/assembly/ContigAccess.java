/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.access.ContigAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.ContigDTO;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.controller.AccessBase;
import de.cebitec.mgx.gui.dtoconversion.ContigDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class ContigAccess extends AccessBase<ContigI> implements ContigAccessI {

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
        Iterator<ContigDTO> it;
        try {
            it = getDTOmaster().Contig().fetchall();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<ContigDTO, ContigI>(it) {
            @Override
            public ContigI next() {
                return ContigDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }

    @Override
    public Iterator<ContigI> ByBin(BinI bin) throws MGXException {
        Iterator<ContigDTO> it;
        try {
            it = getDTOmaster().Contig().byBin(bin.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<ContigDTO, ContigI>(it) {
            @Override
            public ContigI next() {
                return ContigDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
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

    @Override
    public void update(ContigI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI<ContigI> delete(ContigI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
