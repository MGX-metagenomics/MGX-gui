///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.controller.assembly;
//
//import de.cebitec.mgx.api.MGXMasterI;
//import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
//import static de.cebitec.mgx.api.access.datatransfer.TransferBaseI.TRANSFER_FAILED;
//import de.cebitec.mgx.api.exception.MGXException;
//import de.cebitec.mgx.api.exception.MGXLoggedoutException;
//import de.cebitec.mgx.api.misc.TaskI;
//import de.cebitec.mgx.api.model.AttributeI;
//import de.cebitec.mgx.api.model.RegionI;
//import de.cebitec.mgx.api.model.SequenceI;
//import de.cebitec.mgx.api.model.assembly.ContigI;
//import de.cebitec.mgx.api.model.assembly.access.RegionAccessI;
//import de.cebitec.mgx.client.MGXDTOMaster;
//import de.cebitec.mgx.client.datatransfer.GeneByAttributeDownloader;
//import de.cebitec.mgx.client.datatransfer.SeqDownloader;
//import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
//import de.cebitec.mgx.client.exception.MGXDTOException;
//import de.cebitec.mgx.dto.dto.AttributeDTOList;
//import de.cebitec.mgx.dto.dto.RegionDTO;
//import de.cebitec.mgx.dto.dto.SequenceDTO;
//import de.cebitec.mgx.gui.controller.AccessBase;
//import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
//import de.cebitec.mgx.gui.dtoconversion.RegionDTOFactory;
//import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
//import de.cebitec.mgx.gui.util.BaseIterator;
//import de.cebitec.mgx.sequence.DNASequenceI;
//import de.cebitec.mgx.sequence.SeqWriterI;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.util.Iterator;
//import java.util.Set;
//
///**
// *
// * @author sj
// */
//public class RegionAccess extends AccessBase<RegionI> implements RegionAccessI {
//
//    public RegionAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
//        super(master, dtomaster);
//    }
//
//    @Override
//    public RegionI fetch(long id) throws MGXException {
//        RegionDTO dto = null;
//        try {
//            dto = getDTOmaster().Region().fetch(id);
//        } catch (MGXClientLoggedOutException mcle) {
//            throw new MGXLoggedoutException(mcle);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        return RegionDTOFactory.getInstance().toModel(getMaster(), dto);
//    }
//
//    @Override
//    public Iterator<RegionI> fetchall() throws MGXException {
//        Iterator<RegionDTO> it;
//        try {
//            it = getDTOmaster().Region().fetchall();
//        } catch (MGXClientLoggedOutException mcle) {
//            throw new MGXLoggedoutException(mcle);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        return new BaseIterator<RegionDTO, RegionI>(it) {
//            @Override
//            public RegionI next() {
//                return RegionDTOFactory.getInstance().toModel(getMaster(), iter.next());
//            }
//        };
//    }
//
//    @Override
//    public Iterator<RegionI> ByAssembledContig(ContigI c) throws MGXException {
//        Iterator<RegionDTO> it;
//        try {
//            it = getDTOmaster().Region().byAssembledContig(c.getId());
//        } catch (MGXClientLoggedOutException mcle) {
//            throw new MGXLoggedoutException(mcle);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        return new BaseIterator<RegionDTO, RegionI>(it) {
//            @Override
//            public RegionI next() {
//                return RegionDTOFactory.getInstance().toModel(getMaster(), iter.next());
//            }
//        };
//    }
//
//    @Override
//    public SequenceI getDNASequence(RegionI gene) throws MGXException {
//        try {
//            SequenceDTO dto = getDTOmaster().Region().getDNASequence(gene.getId());
//            return SequenceDTOFactory.getInstance().toModel(getMaster(), dto);
//        } catch (MGXClientLoggedOutException mcle) {
//            throw new MGXLoggedoutException(mcle);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//    }
//
//    @Override
//    public DownloadBaseI createDownloaderByAttributes(Set<AttributeI> attrs, SeqWriterI<DNASequenceI> writer, boolean closeWriter, Set<String> seenGeneNames) throws MGXException {
//        AttributeDTOList.Builder b = AttributeDTOList.newBuilder();
//        for (AttributeI a : attrs) {
//            b.addAttribute(AttributeDTOFactory.getInstance().toDTO(a));
//        }
//        final GeneByAttributeDownloader dl;
//        try {
//            dl = getDTOmaster().Region().createDownloaderByAttributes(b.build(), writer, closeWriter, seenGeneNames);
//        } catch (MGXClientLoggedOutException mcle) {
//            throw new MGXLoggedoutException(mcle);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        return new ServerGeneDownloader(dl);
//    }
//
//    @Override
//    public void update(RegionI obj) throws MGXException {
//        throw new UnsupportedOperationException("Not supported.");
//    }
//
//    @Override
//    public TaskI<RegionI> delete(RegionI obj) throws MGXException {
//        throw new UnsupportedOperationException("Not supported.");
//    }
//
//    private static class ServerGeneDownloader extends DownloadBaseI implements PropertyChangeListener {
//
//        private final SeqDownloader sd;
//
//        public ServerGeneDownloader(SeqDownloader sd) {
//            this.sd = sd;
//            sd.addPropertyChangeListener(this);
//        }
//
//        @Override
//        public boolean download() {
//            boolean ret = sd.download();
//            if (!ret) {
//                setErrorMessage(sd.getErrorMessage());
//            }
//            sd.removePropertyChangeListener(this);
//            return ret;
//        }
//
//        @Override
//        public long getProgress() {
//            return sd.getProgress();
//        }
//
//        @Override
//        public void propertyChange(PropertyChangeEvent evt) {
//            switch (evt.getPropertyName()) {
//                case TRANSFER_FAILED:
//                    fireTaskChange(evt.getPropertyName(), evt.getNewValue());
//                    break;
//                default:
//                    fireTaskChange(evt.getPropertyName(), sd.getProgress());
//            }
//        }
//
//    }
//}
