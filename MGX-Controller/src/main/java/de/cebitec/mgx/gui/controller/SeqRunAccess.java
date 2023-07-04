package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.SeqRunAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.TermI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.qc.QCResultI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.QCResultDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.assembly.AssembledSeqRun;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.QCResultDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SeqRunDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess extends AccessBase<SeqRunI> implements SeqRunAccessI {

    public SeqRunAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public SeqRunI create(DNAExtractI extract, String name, TermI seqMethod, TermI seqTechnology, boolean submittedINSDC, boolean isPaired, String accession) throws MGXException {
        SeqRunI obj = new SeqRun(getMaster())
                .setDNAExtractId(extract.getId())
                .setName(name)
                .setSequencingMethod(seqMethod)
                .setSequencingTechnology(seqTechnology)
                .setSubmittedToINSDC(submittedINSDC)
                .setIsPaired(isPaired)
                .setNumSequences(-1L)
                .setAccession(submittedINSDC ? accession : null);
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().SeqRun().create(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.setId(id);
        return obj;
    }

//    @Override
//    public SeqRunI create(SeqRunI obj) throws MGXException {
//        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
//        long id = Identifiable.INVALID_IDENTIFIER;
//        try {
//            id = getDTOmaster().SeqRun().create(dto);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        obj.setId(id);
//        return obj;
//    }
    @Override
    public SeqRunI fetch(long id) throws MGXException {
        SeqRunDTO dto = null;
        try {
            dto = getDTOmaster().SeqRun().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        SeqRunI ret = SeqRunDTOFactory.getInstance().toModel(getMaster(), dto);
        return ret;
    }

    @Override
    public Iterator<SeqRunI> fetchall() throws MGXException {
        try {
            Iterator<SeqRunDTO> fetchall = getDTOmaster().SeqRun().fetchall().getSeqrunList().iterator();
            return new BaseIterator<SeqRunDTO, SeqRunI>(fetchall) {
                @Override
                public SeqRunI next() {
                    SeqRunI sr = SeqRunDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void update(SeqRunI obj) throws MGXException {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().SeqRun().update(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI<SeqRunI> delete(SeqRunI obj) throws MGXException {
        try {
            UUID uuid = getDTOmaster().SeqRun().delete(obj.getId());
            return getMaster().<SeqRunI>Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<SeqRunI> ByExtract(final DNAExtractI extract) throws MGXException {
        try {
            Iterator<SeqRunDTO> fetchall = getDTOmaster().SeqRun().byExtract(extract.getId());
            return new BaseIterator<SeqRunDTO, SeqRunI>(fetchall) {
                @Override
                public SeqRunI next() {
                    SeqRunI sr = SeqRunDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<SeqRunI> ByJob(final JobI job) throws MGXException {
        try {
            Iterator<SeqRunDTO> iter = getDTOmaster().SeqRun().byJob(job.getId());
            return new BaseIterator<SeqRunDTO, SeqRunI>(iter) {
                @Override
                public SeqRunI next() {
                    SeqRunI sr = SeqRunDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<AssembledSeqRunI> ByAssembly(final AssemblyI assembly) throws MGXException {
        try {
            Iterator<SeqRunDTO> iter = getDTOmaster().SeqRun().byAssembly(assembly.getId());
            return new BaseIterator<SeqRunDTO, AssembledSeqRunI>(iter) {
                @Override
                public AssembledSeqRunI next() {
                    SeqRunI sr = SeqRunDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return new AssembledSeqRun(getMaster(), assembly, sr);
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Map<JobI, Set<AttributeTypeI>> getJobsAndAttributeTypes(SeqRunI run) throws MGXException {
        Map<JobI, Set<AttributeTypeI>> ret = new HashMap<>();

        try {
            for (JobAndAttributeTypes jat : getDTOmaster().SeqRun().getJobsAndAttributeTypes(run.getId())) {
                JobDTO jobdto = jat.getJob();
                JobI job = JobDTOFactory.getInstance().toModel(getMaster(), jobdto);

                List<SeqRunI> seqruns = new ArrayList<>();
                Iterator<SeqRunDTO> byJob = getDTOmaster().SeqRun().byJob(jobdto.getId());
                while (byJob.hasNext()) {
                    SeqRunDTO sr = byJob.next();
                    if (run.getId() == sr.getId()) {
                        seqruns.add(run);
                    } else {
                        seqruns.add(SeqRunDTOFactory.getInstance().toModel(getMaster(), sr));
                    }
                }

                job.setSeqruns(seqruns.toArray(new SeqRunI[]{}));

                Set<AttributeTypeI> all = new HashSet<>();
                for (AttributeTypeDTO atDTO : jat.getAttributeTypes().getAttributeTypeList()) {
                    AttributeTypeI aType = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), atDTO);
                    all.add(aType);
                }

                ret.put(job, all);
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }

        return ret;
    }

    @Override
    public Map<JobI, Set<AttributeTypeI>> getJobsAndAttributeTypes(AssembledSeqRunI run) throws MGXException {
        Map<JobI, Set<AttributeTypeI>> ret = new HashMap<>();

        try {
            for (JobAndAttributeTypes jat : getDTOmaster().SeqRun().getJobsAndAttributeTypes(run.getId(), run.getAssembly().getId())) {
                JobDTO jobdto = jat.getJob();
                JobI job = JobDTOFactory.getInstance().toModel(getMaster(), jobdto);

                job.setAssembly(run.getAssembly());

                Set<AttributeTypeI> all = new HashSet<>();
                for (AttributeTypeDTO atDTO : jat.getAttributeTypes().getAttributeTypeList()) {
                    AttributeTypeI aType = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), atDTO);
                    all.add(aType);
                }

                ret.put(job, all);
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }

        return ret;
    }

    @Override
    public List<QCResultI> getQC(SeqRunI run) throws MGXException {
        List<QCResultI> ret = new ArrayList<>();
        try {
            List<QCResultDTO> qc = getDTOmaster().SeqRun().getQC(run.getId());
            for (QCResultDTO dto : qc) {
                ret.add(QCResultDTOFactory.getInstance().toModel(getMaster(), dto));
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return ret;
    }

    @Override
    public boolean hasQuality(SeqRunI run) throws MGXException {
        try {
            return getDTOmaster().SeqRun().hasQuality(run.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

}
