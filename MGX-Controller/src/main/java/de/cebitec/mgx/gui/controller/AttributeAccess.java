package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDistribution;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.CorrelatedAttributeCount;
import de.cebitec.mgx.dto.dto.SearchRequestDTO;
import de.cebitec.mgx.dto.dto.SearchRequestDTO.Builder;
import de.cebitec.mgx.dto.dto.SearchResultDTO;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.SearchResult;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess extends AccessBase<Attribute> {

    public List<Attribute> BySeqRun(long seqrun_id) {
        List<Attribute> attrs = new ArrayList<>();
        try {
            for (AttributeDTO adto : getDTOmaster().Attribute().BySeqRun(seqrun_id)) {
                Attribute attr = AttributeDTOFactory.getInstance().toModel(adto);
                attr.setMaster(this.getMaster());
                attrs.add(attr);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return attrs;
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
        return new Distribution(res, total);
    }

    @Override
    public long create(Attribute obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Attribute fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<Attribute> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(Attribute obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Map<Pair<Attribute, Attribute>, Long> getCorrelation(AttributeType attributeType1, Job job1, AttributeType attributeType2, Job job2) {
        Map<Pair<Attribute, Attribute>, Long> ret = new HashMap<>();
        try {
            AttributeCorrelation corr = getDTOmaster().Attribute().getCorrelation(attributeType1.getId(), job1.getId(), attributeType2.getId(), job2.getId());

//            // convert and save types first
//            Map<Long, AttributeType> types = new HashMap<>();
//            for (AttributeTypeDTO at : corr.getAttributeTypeList()) {
//                types.put(at.getId(), AttributeTypeDTOFactory.getInstance().toModel(at));
//            }

            for (CorrelatedAttributeCount cac : corr.getEntryList()) {
                // the restricting attribute
                Attribute attr = AttributeDTOFactory.getInstance().toModel(cac.getRestrictedAttribute());
                //attr.setAttributeType(types.get(cac.getRestrictedAttribute().getAttributeTypeId()));
                attr.setAttributeType(attributeType1);
                attr.setMaster(this.getMaster());

                // the attribute
                Attribute attr2 = AttributeDTOFactory.getInstance().toModel(cac.getAttribute());
                //attr2.setAttributeType(types.get(cac.getAttribute().getAttributeTypeId()));
                attr2.setAttributeType(attributeType2);
                attr2.setMaster(this.getMaster());

                ret.put(new Pair<>(attr, attr2), cac.getCount());
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
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

            // convert attribute and fill in the attributetypes
            for (AttributeCount ac : distribution.getAttributeCountsList()) {
                Attribute attr = AttributeDTOFactory.getInstance().toModel(ac.getAttribute());
                attr.setAttributeType(types.get(ac.getAttribute().getAttributeTypeId()));
                attr.setMaster(this.getMaster());
                res.put(attr, ac.getCount());
            }

        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.err.println("getHierarchy(" +attrType_id+"), job "+job_id+", got "+res.size()+" entries");

        Tree<Long> ret = TreeFactory.createTree(res);

        return ret;
    }

    public List<SearchResult> search(List<SeqRun> selectedSeqRuns, String text, boolean exact) {
        // FIXME move to dtoconverter package
        Builder b = SearchRequestDTO.newBuilder().setExact(exact).setTerm(text);
        for (SeqRun sr : selectedSeqRuns) {
            b.addSeqrunId(sr.getId());
        }
        try {
            List<SearchResultDTO> search = getDTOmaster().Attribute().search(b.build());

            // FIXME - convert back and return 

        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
