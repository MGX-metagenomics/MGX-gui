package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AttributeAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.SearchRequestI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Matrix;
import de.cebitec.mgx.gui.datamodel.misc.SearchRequest;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.MatrixDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SearchRequestDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import de.cebitec.mgx.gui.datafactories.TreeFactory;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess implements AttributeAccessI {

    private final MGXDTOMaster dtomaster;
    private final MGXMasterI master;

    public AttributeAccess(MGXDTOMaster dtomaster, MGXMasterI master) throws MGXException {
        this.dtomaster = dtomaster;
        this.master = master;
        if (master.isDeleted()) {
            throw new MGXLoggedoutException("You are disconnected.");
        }
    }

    @Override
    public Iterator<AttributeI> ByJob(JobI job) throws MGXException {
        try {
            Iterator<AttributeDTO> BySeqRun = getDTOmaster().Attribute().byJob(job.getId());

            return new BaseIterator<AttributeDTO, AttributeI>(BySeqRun) {
                @Override
                public AttributeI next() {
                    return AttributeDTOFactory.getInstance().toModel(getMaster(), iter.next());
                }
            };

        } catch (MGXDTOException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<AttributeI> BySeqRun(final SeqRunI seqrun) throws MGXException {
        try {
            Iterator<AttributeDTO> BySeqRun = getDTOmaster().Attribute().bySeqRun(seqrun.getId());

            return new BaseIterator<AttributeDTO, AttributeI>(BySeqRun) {
                @Override
                public AttributeI next() {
                    return AttributeDTOFactory.getInstance().toModel(master, iter.next());
                }
            };

        } catch (MGXDTOException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXException(ex);
        }
    }

    @Override
    public DistributionI<Long> getDistribution(AttributeTypeI attrType, JobI job, SeqRunI run) throws MGXException {
        Map<AttributeI, Long> res;
        long total = 0;
        try {
            AttributeDistribution distribution = dtomaster.Attribute().getDistribution(attrType.getId(), job.getId(), run.getId());
            res = new HashMap<>(distribution.getAttributeCountsCount());

            // convert and save types first
            TLongObjectMap<AttributeTypeI> types = new TLongObjectHashMap<>(distribution.getAttributeTypeCount());
            //Map<Long, AttributeTypeI> types = new HashMap<>(distribution.getAttributeTypeCount());
            for (AttributeTypeDTO at : distribution.getAttributeTypeList()) {
                types.put(at.getId(), AttributeTypeDTOFactory.getInstance().toModel(master, at));
            }

            // convert attribute and fill in the attributetypes
            for (AttributeCount ac : distribution.getAttributeCountsList()) {
                AttributeI attr = AttributeDTOFactory.getInstance().toModel(master, ac.getAttribute());
                attr.setAttributeType(types.get(ac.getAttribute().getAttributeTypeId()));
                total += ac.getCount();
                res.put(attr, ac.getCount());
            }
        } catch (MGXDTOException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXException(ex);
        }
        return new Distribution(master, res, total);
    }

    @Override
    public AttributeI create(JobI job, AttributeTypeI attrType, String attrValue, AttributeI parent) throws MGXException {

        if (!master.equals(job.getMaster()) || !master.equals(attrType.getMaster())) {
            throw new MGXException("MGX master instances need to be equal.");
        }
        if (parent != null && !master.equals(parent.getAttributeType().getMaster())) {
            throw new MGXException("MGX master instances need to be equal.");
        }

        AttributeI attr = new Attribute();
        attr.setAttributeType(attrType);
        attr.setJobId(job.getId());
        attr.setValue(attrValue);

        if (parent != null) {
            attr.setParentID(parent.getId());
        }

        try {
            AttributeDTO dto = AttributeDTOFactory.getInstance().toDTO(attr);
            long objId = dtomaster.Attribute().create(dto);
            attr.setId(objId);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return attr;
    }

    @Override
    public AttributeI fetch(long id) throws MGXException {
        AttributeI ret = null;
        try {
            AttributeDTO dto = dtomaster.Attribute().fetch(id);
            ret = AttributeDTOFactory.getInstance().toModel(master, dto);
            AttributeTypeDTO aType = dtomaster.AttributeType().fetch(dto.getAttributeTypeId());
            ret.setAttributeType(AttributeTypeDTOFactory.getInstance().toModel(master, aType));
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return ret;
    }

    @Override
    public TaskI<AttributeI> delete(AttributeI attr) throws MGXException {
        throw new MGXException("Attribute deletion is not supported. Delete the corresponding job instead.");
    }

    public Matrix getCorrelation(AttributeTypeI attributeType1, JobI job1, AttributeTypeI attributeType2, JobI job2) throws MGXException {
        try {
            AttributeCorrelation corr = dtomaster.Attribute().getCorrelation(attributeType1.getId(), job1.getId(), attributeType2.getId(), job2.getId());
            return MatrixDTOFactory.getInstance().toModel(master, corr);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public TreeI<Long> getHierarchy(AttributeTypeI attrType, JobI job, SeqRunI run) throws MGXException {
        Map<AttributeI, Long> res;
        try {
            AttributeDistribution distribution = dtomaster.Attribute().getHierarchy(attrType.getId(), job.getId(), run.getId());

            res = new HashMap<>(distribution.getAttributeTypeCount());

            // convert and save types first
            TLongObjectMap<AttributeTypeI> types = new TLongObjectHashMap<>(distribution.getAttributeTypeCount());
            //Map<Long, AttributeTypeI> types = new HashMap<>();
            for (AttributeTypeDTO at : distribution.getAttributeTypeList()) {
                types.put(at.getId(), AttributeTypeDTOFactory.getInstance().toModel(master, at));
            }

            // convert attribute and fill in the attributetypes
            for (AttributeCount ac : distribution.getAttributeCountsList()) {
                AttributeI attr = AttributeDTOFactory.getInstance().toModel(master, ac.getAttribute());
                attr.setAttributeType(types.get(ac.getAttribute().getAttributeTypeId()));
                //assert !res.containsKey(attr); // no duplicates allowed
                res.put(attr, ac.getCount());
            }

        } catch (MGXDTOException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXException(ex);
        }

        TreeI<Long> ret = TreeFactory.createTree(res);
        return ret;
    }

    @Override
    public Iterator<String> find(String term, SeqRunI run) throws MGXException {
        if (term == null || term.isEmpty()) {
            return new Iterator<String>() {

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public String next() {
                    return null;
                }

                @Override
                public void remove() {
                }
            };
        }
        SearchRequestI sr = new SearchRequest();
        sr.setTerm(term);
        sr.setRuns(new SeqRunI[]{run});
        try {
            return dtomaster.Attribute().find(SearchRequestDTOFactory.getInstance().toDTO(sr));
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<SequenceI> search(String term, boolean exact, SeqRunI seqrun) throws MGXException {
        SearchRequestI sr = new SearchRequest();
        sr.setTerm(term);
        sr.setExact(exact);
        sr.setRuns(new SeqRunI[]{seqrun});
        SearchRequestDTO reqdto = SearchRequestDTOFactory.getInstance().toDTO(sr);

        Iterator<SequenceDTO> searchResult = null;
        try {
            searchResult = dtomaster.Attribute().search(reqdto);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }

        return new BaseIterator<SequenceDTO, SequenceI>(searchResult) {
            @Override
            public SequenceI next() {
                SequenceI h = SequenceDTOFactory.getInstance().toModel(master, iter.next());
                return h;
            }
        };

//        SequenceI[] ret = new SequenceI[searchResult.size()];
//        int i = 0;
//        for (SequenceDTO dto : searchResult) {
//            SequenceI seq = SequenceDTOFactory.getInstance().toModel(master, dto);
//            ret[i++] = seq;
//        }
//
//        return ret;
    }

    private MGXDTOMaster getDTOmaster() {
        return dtomaster;
    }

    private MGXMasterI getMaster() {
        return master;
    }

//    private boolean checkHasValue(Set<Attribute> set, String value) {
//        for (Attribute a : set) {
//            if (a.getValue().equals(value)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private int countValues(Set<Attribute> set, String val) {
//        int cnt = 0;
//        for (Attribute a : set) {
//            if (a.getValue().equals(val)) {
//                cnt++;
//            }
//        }
//        return cnt;
//    }
//    private static void sanityCheck(Set<Attribute> map) {
//        for (Attribute a : map) {
//            if (a.getId() != Identifiable.INVALID_IDENTIFIER) {
//                boolean hasParent = false;
//                for (Attribute b : map) {
//                    if (a.getParentID() == b.getId()) {
//                        hasParent = true;
//                    }
//                }
//                assert hasParent;
//            }
//        }
//    }
}
