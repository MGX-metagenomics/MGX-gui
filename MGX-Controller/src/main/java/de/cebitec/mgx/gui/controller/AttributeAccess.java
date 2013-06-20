package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Matrix;
import de.cebitec.mgx.gui.datamodel.misc.SearchRequest;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.datamodel.tree.Checker;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.MatrixDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SearchRequestDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess extends AccessBase<Attribute> {

    public Iterator<Attribute> BySeqRun(final long seqrun_id) {
        try {
            Iterator<AttributeDTO> BySeqRun = getDTOmaster().Attribute().BySeqRun(seqrun_id);

            return new BaseIterator<AttributeDTO, Attribute>(BySeqRun) {
                @Override
                public Attribute next() {
                    Attribute attr = AttributeDTOFactory.getInstance().toModel(iter.next());
                    attr.setMaster(getMaster());
                    return attr;
                }
            };

        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
    public Distribution getDistribution(long attrType_id, long job_id) {
        Map<Attribute, Long> res = new HashMap<>();
        long total = 0;
        try {
            AttributeDistribution distribution = getDTOmaster().Attribute().getDistribution(attrType_id, job_id);

            // convert and save types first
            Map<Long, AttributeType> types = new HashMap<>();
            for (AttributeTypeDTO at : distribution.getAttributeTypeList()) {
                types.put(at.getId(), AttributeTypeDTOFactory.getInstance().toModel(at));
            }

            // convert attribute and fill in the attributetypes
            for (AttributeCount ac : distribution.getAttributeCountsList()) {
                Attribute attr = AttributeDTOFactory.getInstance().toModel(ac.getAttribute());
                attr.setAttributeType(types.get(ac.getAttribute().getAttributeTypeId()));
                attr.setMaster(this.getMaster());
                total += ac.getCount();
                res.put(attr, ac.getCount());
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Distribution(res, total, getMaster());
    }

    @Override
    public long create(Attribute obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Attribute fetch(long id) {
        Attribute ret = null;
        try {
            AttributeDTO dto = getDTOmaster().Attribute().fetch(id);
            ret = AttributeDTOFactory.getInstance().toModel(dto);
            AttributeTypeDTO aType = getDTOmaster().AttributeType().fetch(dto.getAttributeTypeId());
            ret.setAttributeType(AttributeTypeDTOFactory.getInstance().toModel(aType));
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public Iterator<Attribute> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(Attribute obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task delete(Attribute obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Matrix getCorrelation(AttributeType attributeType1, Job job1, AttributeType attributeType2, Job job2) {
        try {
            AttributeCorrelation corr = getDTOmaster().Attribute().getCorrelation(attributeType1.getId(), job1.getId(), attributeType2.getId(), job2.getId());
            return MatrixDTOFactory.getInstance().toModel(corr);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Tree<Long> getHierarchy(long attrType_id, long job_id) {
        Map<Attribute, Long> res = new HashMap<>();
        try {
            AttributeDistribution distribution = getDTOmaster().Attribute().getHierarchy(attrType_id, job_id);

            // convert and save types first
            Map<Long, AttributeType> types = new HashMap<>();
            for (AttributeTypeDTO at : distribution.getAttributeTypeList()) {
                types.put(at.getId(), AttributeTypeDTOFactory.getInstance().toModel(at));
            }

            int numRoots = 0;

            // convert attribute and fill in the attributetypes
            for (AttributeCount ac : distribution.getAttributeCountsList()) {
                Attribute attr = AttributeDTOFactory.getInstance().toModel(ac.getAttribute());
                if (attr.getParentID() == Identifiable.INVALID_IDENTIFIER) {
                    numRoots++;
                }
                attr.setAttributeType(types.get(ac.getAttribute().getAttributeTypeId()));
                attr.setMaster(getMaster());
                assert !res.containsKey(attr); // no duplicates allowed
                res.put(attr, ac.getCount());
            }

            assert numRoots == 1; //only one root

        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        Checker.sanityCheck(res.keySet());
        Tree<Long> ret = TreeFactory.createTree(res);
        return ret;
    }

    public Sequence[] search(SeqRun[] selectedSeqRuns, String text, boolean exact) {
        SearchRequest sr = new SearchRequest();
        sr.setTerm(text);
        sr.setExact(exact);
        sr.setRuns(selectedSeqRuns);
        SearchRequestDTO reqdto = SearchRequestDTOFactory.getInstance().toDTO(sr);

        List<SequenceDTO> searchResult = null;
        try {
            searchResult = getDTOmaster().Attribute().search(reqdto);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }

        Sequence[] ret = new Sequence[searchResult.size()];
        int i = 0;
        for (SequenceDTO dto : searchResult) {
            Sequence seq = SequenceDTOFactory.getInstance().toModel(dto);
            seq.setMaster(getMaster());
            ret[i++] = seq;
        }

        return ret;
    }

    private boolean checkHasValue(Set<Attribute> set, String value) {
        for (Attribute a : set) {
            if (a.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private int countValues(Set<Attribute> set, String val) {
        int cnt = 0;
        for (Attribute a : set) {
            if (a.getValue().equals(val)) {
                cnt++;
            }
        }
        return cnt;
    }
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
