package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess extends AccessBase<Attribute> {

    public List<Attribute> BySeqRun(Long id) {
        List<Attribute> attrs = new ArrayList<Attribute>();
        try {
            for (AttributeDTO adto : getDTOmaster().Attribute().BySeqRun(id)) {
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
    public Map<Attribute, Long> getDistribution(AttributeType attributeType, Job job) {
        Map<Attribute, Long> res = new HashMap<Attribute, Long>();
        try {
            for (AttributeCount ac : getDTOmaster().Attribute().getDistribution(attributeType.getId(), job.getId())) {
                Attribute attr = AttributeDTOFactory.getInstance().toModel(ac.getAttribute());
                attr.setMaster(this.getMaster());
                res.put(attr, ac.getCount());
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public Long create(Attribute obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Attribute fetch(Long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<Attribute> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(Attribute obj) {
    }

    @Override
    public void delete(Long id) {
    }

    public Map<Attribute, Long> getHierarchy(AttributeType attributeType, Job job) {
        Map<Attribute, Long> res = new HashMap<Attribute, Long>();
        try {
            for (AttributeCount ac : getDTOmaster().Attribute().getHierarchy(attributeType.getId(), job.getId())) {
                Attribute attr = AttributeDTOFactory.getInstance().toModel(ac.getAttribute());
                attr.setMaster(this.getMaster());
                res.put(attr, ac.getCount());
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
}
