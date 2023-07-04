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
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.access.BinAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.BinDTO;
import de.cebitec.mgx.gui.controller.AccessBase;
import de.cebitec.mgx.gui.dtoconversion.BinDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class BinAccess extends AccessBase<BinI> implements BinAccessI {

    public BinAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public BinI fetch(long id) throws MGXException {
        BinDTO dto = null;
        try {
            dto = getDTOmaster().Bin().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return BinDTOFactory.getInstance().toModel(getMaster(), dto);
    }

    @Override
    public Iterator<BinI> fetchall() throws MGXException {
        Iterator<BinDTO> it;
        try {
            it = getDTOmaster().Bin().fetchall().getBinList().iterator();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<BinDTO, BinI>(it) {
            @Override
            public BinI next() {
                return BinDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }

    @Override
    public Iterator<BinI> ByAssembly(AssemblyI a) throws MGXException {
        Iterator<BinDTO> it;
        try {
            it = getDTOmaster().Bin().byAssembly(a.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<BinDTO, BinI>(it) {
            @Override
            public BinI next() {
                return BinDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }

    @Override
    public void update(BinI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI<BinI> delete(BinI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
