package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AttributeAccessI;
import de.cebitec.mgx.api.exception.MGXException;
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
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.common.TreeFactory;
import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Matrix;
import de.cebitec.mgx.gui.datamodel.misc.SearchRequest;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.MatrixDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SearchRequestDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
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

    public AttributeAccess(MGXDTOMaster dtomaster, MGXMasterI master) {
        this.dtomaster = dtomaster;
        this.master = master;
    }

    @Override
    public Iterator<AttributeI> BySeqRun(final SeqRunI seqrun) throws MGXException {
        try {
            Iterator<AttributeDTO> BySeqRun = dtomaster.Attribute().BySeqRun(seqrun.getId());

            return new BaseIterator<AttributeDTO, AttributeI>(BySeqRun) {
                @Override
                public AttributeI next() {
                    AttributeI attr = AttributeDTOFactory.getInstance().toModel(master, iter.next());
                    return attr;
                }
            };

        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXException(ex);
        }
    }

//    public Collection<String> listTypes() throws MGXServerException {
//        Collection<String> ret = new HashSet<String>();
//        Collection<MGXString> dtolist = getDTOmaster().Attribute().listTypes();
//        for (MGXString adto : dtolist) {
//            ret.add(adto.getValue());
//        }
//        return ret;
//    }
//
//    public Collection<String> listTypesByJob(Long jobId) throws MGXServerException {
//        Collection<String> ret = new HashSet<String>();
//        Collection<MGXString> dtolist = getDTOmaster().Attribute().listTypesByJob(jobId);
//        for (MGXString adto : dtolist) {
//            ret.add(adto.getValue());
//        }
//        return ret;
//    }
//
//    public Collection<Attribute> listTypesBySeqRun(Long seqrunId) throws MGXServerException {
//        Collection<Attribute> ret = new HashSet<Attribute>();
//        Collection<MGXString> dtolist = getDTOmaster().Attribute().listTypesBySeqRun(seqrunId);
//        for (MGXString adto : dtolist) {
//            ret.add(adto.getValue());
//        }
//        return ret;
//    }
//    public Map<Attribute, Long> getDistributionByRuns(String attributeName, List<Long> seqrun_ids) {
//        Map<Attribute, Long> res = new HashMap<Attribute, Long>();
//        try {
//            for (AttributeCount ac : getDTOmaster().Attribute().getDistributionByRuns(attributeName, seqrun_ids)) {
//                AttributeDTO adto = ac.getAttribute();
//                Attribute attr = AttributeDTOFactory.getInstance().toModel(adto);
//                Long count = ac.getCount();
//                res.put(attr, count);
//            }
//        } catch (MGXServerException ex) {
//            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return res;
//    }
    @Override
    public DistributionI getDistribution(AttributeTypeI attrType, JobI job) throws MGXException {
        Map<AttributeI, Long> res;
        long total = 0;
        try {
            AttributeDistribution distribution = dtomaster.Attribute().getDistribution(attrType.getId(), job.getId());
            res = new HashMap<>(distribution.getAttributeCountsCount());

            // convert and save types first
            Map<Long, AttributeTypeI> types = new HashMap<>(distribution.getAttributeTypeCount());
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
        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXException(ex);
        }
        return new Distribution(res, total, master);
    }

    @Override
    public AttributeI create(AttributeI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public AttributeI fetch(long id) throws MGXException {
        AttributeI ret = null;
        try {
            AttributeDTO dto = dtomaster.Attribute().fetch(id);
            ret = AttributeDTOFactory.getInstance().toModel(master, dto);
            AttributeTypeDTO aType = dtomaster.AttributeType().fetch(dto.getAttributeTypeId());
            ret.setAttributeType(AttributeTypeDTOFactory.getInstance().toModel(master, aType));
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
        return ret;
    }

    @Override
    public Iterator<AttributeI> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(AttributeI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI<AttributeI> delete(AttributeI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Matrix getCorrelation(AttributeTypeI attributeType1, JobI job1, AttributeTypeI attributeType2, JobI job2) throws MGXException {
        try {
            AttributeCorrelation corr = dtomaster.Attribute().getCorrelation(attributeType1.getId(), job1.getId(), attributeType2.getId(), job2.getId());
            return MatrixDTOFactory.getInstance().toModel(master, corr);
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public TreeI<Long> getHierarchy(AttributeTypeI attrType, JobI job) throws MGXException {
        Map<AttributeI, Long> res;
        try {
            AttributeDistribution distribution = dtomaster.Attribute().getHierarchy(attrType.getId(), job.getId());
            res = new HashMap<>(distribution.getAttributeTypeCount());

            // convert and save types first
            Map<Long, AttributeTypeI> types = new HashMap<>();
            for (AttributeTypeDTO at : distribution.getAttributeTypeList()) {
                types.put(at.getId(), AttributeTypeDTOFactory.getInstance().toModel(master, at));
            }

            // convert attribute and fill in the attributetypes
            for (AttributeCount ac : distribution.getAttributeCountsList()) {
                AttributeI attr = AttributeDTOFactory.getInstance().toModel(master, ac.getAttribute());
                attr.setAttributeType(types.get(ac.getAttribute().getAttributeTypeId()));
                assert !res.containsKey(attr); // no duplicates allowed
                res.put(attr, ac.getCount());
            }

        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
            throw new MGXException(ex);
        }

        TreeI<Long> ret = TreeFactory.createTree(res);
        return ret;
    }

    @Override
    public Iterator<String> find(String term, SeqRunI[] targets) throws MGXException {
        if (term == null || term.isEmpty() || targets == null || targets.length == 0) {
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
        sr.setRuns(targets);
        try {
            return dtomaster.Attribute().find(SearchRequestDTOFactory.getInstance().toDTO(sr));
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<SequenceI> search(String term, boolean exact, SeqRunI[] targets) throws MGXException {
        SearchRequestI sr = new SearchRequest();
        sr.setTerm(term);
        sr.setExact(exact);
        sr.setRuns(targets);
        SearchRequestDTO reqdto = SearchRequestDTOFactory.getInstance().toDTO(sr);

        Iterator<SequenceDTO> searchResult = null;
        try {
            searchResult = dtomaster.Attribute().search(reqdto);
        } catch (MGXServerException ex) {
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
