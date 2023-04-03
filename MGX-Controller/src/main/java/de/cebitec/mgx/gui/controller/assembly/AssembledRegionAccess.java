/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import static de.cebitec.mgx.api.access.datatransfer.TransferBaseI.TRANSFER_FAILED;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.BinSearchResultI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.access.AssembledRegionAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.GeneByAttributeDownloader;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AssembledRegionDTO;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.BinSearchResultDTO;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.controller.MasterHolder;
import de.cebitec.mgx.gui.dtoconversion.AssembledRegionDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.BinSearchResultDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author sj
 */
public class AssembledRegionAccess extends MasterHolder implements AssembledRegionAccessI {

    public AssembledRegionAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

//    @Override
//    public AssembledRegionI fetch(long id) throws MGXException {
//        AssembledRegionDTO dto = null;
//        try {
//            dto = getDTOmaster().AssembledRegion().fetch(id);
//        } catch (MGXClientLoggedOutException mcle) {
//            throw new MGXLoggedoutException(mcle);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        return AssembledRegionDTOFactory.getInstance().toModel(getMaster(), dto);
//    }

//    @Override
//    public Iterator<AssembledRegionI> fetchall() throws MGXException {
//        Iterator<AssembledRegionDTO> it;
//        try {
//            it = getDTOmaster().AssembledRegion().fetchall();
//        } catch (MGXClientLoggedOutException mcle) {
//            throw new MGXLoggedoutException(mcle);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        return new BaseIterator<AssembledRegionDTO, AssembledRegionI>(it) {
//            @Override
//            public AssembledRegionI next() {
//                return AssembledRegionDTOFactory.getInstance().toModel(getMaster(), iter.next());
//            }
//        };
//    }

    @Override
    public Iterator<AssembledRegionI> ByContig(ContigI c) throws MGXException {
        Iterator<AssembledRegionDTO> it;
        try {
            it = getDTOmaster().AssembledRegion().byAssembledContig(c.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<AssembledRegionDTO, AssembledRegionI>(it) {
            @Override
            public AssembledRegionI next() {
                return AssembledRegionDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }

    @Override
    public SequenceI getDNASequence(AssembledRegionI gene) throws MGXException {
        try {
            SequenceDTO dto = getDTOmaster().AssembledRegion().getDNASequence(gene.getId());
            return SequenceDTOFactory.getInstance().toModel(getMaster(), dto);} catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<BinSearchResultI> search(BinI bin, String term) throws MGXException {
        Iterator<BinSearchResultDTO> it;
        try {
            it = getDTOmaster().AssembledRegion().search(bin.getId(), term);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        
         return new BaseIterator<BinSearchResultDTO, BinSearchResultI>(it) {
            @Override
            public BinSearchResultI next() {
                return BinSearchResultDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }
    
    

    @Override
    public DownloadBaseI createDownloaderByAttributes(Set<AttributeI> attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter, Set<String> seenGeneNames) throws MGXException {
        AttributeDTOList.Builder b = AttributeDTOList.newBuilder();
        for (AttributeI a : attrs) {
            b.addAttribute(AttributeDTOFactory.getInstance().toDTO(a));
        }
        final GeneByAttributeDownloader dl;
        try {
            dl = getDTOmaster().AssembledRegion().createDownloaderByAttributes(b.build(), writer, closeWriter, seenGeneNames);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new ServerGeneDownloader(dl);
    }

    private static class ServerGeneDownloader extends DownloadBaseI implements PropertyChangeListener {

        private final SeqDownloader sd;

        public ServerGeneDownloader(SeqDownloader sd) {
            this.sd = sd;
            sd.addPropertyChangeListener(this);
        }

        @Override
        public boolean download() {
            boolean ret = sd.download();
            if (!ret) {
                setErrorMessage(sd.getErrorMessage());
            }
            sd.removePropertyChangeListener(this);
            return ret;
        }

        @Override
        public long getProgress() {
            return sd.getProgress();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case TRANSFER_FAILED:
                    fireTaskChange(evt.getPropertyName(), evt.getNewValue());
                    break;
                default:
                    fireTaskChange(evt.getPropertyName(), sd.getProgress());
            }
        }

    }
}
