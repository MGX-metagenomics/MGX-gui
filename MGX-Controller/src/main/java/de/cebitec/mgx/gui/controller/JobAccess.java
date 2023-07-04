package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.JobAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.dto.dto.AssemblyDTO;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.dtoconversion.AssemblyDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SeqRunDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends MasterHolder implements JobAccessI {

    public JobAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public boolean verify(JobI obj) throws MGXException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret;
        try {
            ret = getDTOmaster().Job().verify(obj.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        obj.modified();
        return ret;
    }

    @Override
    public boolean execute(JobI obj) throws MGXException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret;
        try {
            ret = getDTOmaster().Job().execute(obj.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        obj.modified();
        return ret;
    }

    @Override
    public TaskI<JobI> restart(JobI job) throws MGXException {
        TaskI<JobI> ret = null;
        boolean noRuns = false;
        if (job.getSeqruns() == null || job.getSeqruns().length == 0) {
            noRuns = true;
        }
        if (job.getAssembly() == null && noRuns) {
            throw new MGXException("Internal error: Job has neither seqrun nor assembly.");
        }

        if (job.getTool() == null) {
            throw new MGXException("Internal error: Job has no tool.");
        }
        try {
            UUID uuid = getDTOmaster().Job().restart(job.getId());
            ret = getMaster().<JobI>Task().get(job, uuid, TaskType.MODIFY);
            if (!job.isDeleted()) {
                job.modified();
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return ret;
    }

    @Override
    public boolean cancel(JobI obj) throws MGXException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret;
        try {
            ret = getDTOmaster().Job().cancel(obj.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        if (ret) {
            obj.modified();
        }
        return ret;
    }

    @Override
    public JobI create(ToolI tool, List<JobParameterI> params, SeqRunI... seqruns) throws MGXException {

        if (seqruns == null || seqruns.length == 0) {
            throw new MGXException("Cannot create job for zero sequencing runs.");
        }

        JobI job = new Job(tool.getMaster(), tool.getMaster().getLogin());
        job.setTool(tool);
        job.setStatus(JobState.CREATED);
        job.setSeqruns(seqruns);
        job.setParameters(params);

        JobDTO dto = JobDTOFactory.getInstance().toDTO(job);
        long id;
        try {
            id = getDTOmaster().Job().create(dto);
            job.setId(id);
            job.modified();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }

        return job;
    }

    @Override
    public JobI create(ToolI tool, List<JobParameterI> params, AssemblyI assembly) throws MGXException {

        if (assembly == null) {
            throw new MGXException("Cannot create job for no assembly.");
        }

        JobI job = new Job(tool.getMaster(), tool.getMaster().getLogin());
        job.setTool(tool);
        job.setStatus(JobState.CREATED);
        job.setAssembly(assembly);
        job.setParameters(params);

        JobDTO dto = JobDTOFactory.getInstance().toDTO(job);
        long id;
        try {
            id = getDTOmaster().Job().create(dto);
            job.setId(id);
            job.modified();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }

        return job;
    }

    @Override
    public JobI fetch(long id) throws MGXException {
        try {
            JobDTO jobdto = getDTOmaster().Job().fetch(id);
            JobI j = JobDTOFactory.getInstance().toModel(getMaster(), jobdto);

            if (jobdto.getAssemblyId() > 0) {
                AssemblyDTO asmdto = getDTOmaster().Assembly().fetch(jobdto.getAssemblyId());
                AssemblyI asm = AssemblyDTOFactory.getInstance().toModel(getMaster(), asmdto);
                j.setAssembly(asm);
            }

            if (jobdto.getSeqrunCount() > 0) {
                SeqRunI[] runs = new SeqRunI[jobdto.getSeqrunCount()];
                int i = 0;
                Iterator<SeqRunI> runIter = getMaster().SeqRun().ByJob(j);
                while (runIter.hasNext()) {
                    runs[i++] = runIter.next();
                }
                j.setSeqruns(runs);
            }
            return j;
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }

    }

    @Override
    public Iterator<JobI> fetchall() throws MGXException {
        try {
            Iterator<JobDTO> fetchall = getDTOmaster().Job().fetchall().getJobList().iterator();

            return new BaseIterator<JobDTO, JobI>(fetchall) {
                @Override
                public JobI next() {
                    JobDTO jobdto = iter.next();
                    JobI j = JobDTOFactory.getInstance().toModel(getMaster(), jobdto);

                    if (jobdto.getAssemblyId() > 0) {
                        try {
                            AssemblyDTO asmdto = getDTOmaster().Assembly().fetch(jobdto.getAssemblyId());
                            AssemblyI asm = AssemblyDTOFactory.getInstance().toModel(getMaster(), asmdto);
                            j.setAssembly(asm);
                        } catch (MGXDTOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }

                    if (jobdto.getSeqrunCount() > 0) {
                        SeqRunI[] runs = new SeqRunI[jobdto.getSeqrunCount()];
                        int i = 0;
                        try {
                            Iterator<SeqRunI> runIter = getMaster().SeqRun().ByJob(j);
                            while (runIter.hasNext()) {
                                runs[i++] = runIter.next();
                            }
                            j.setSeqruns(runs);
                        } catch (MGXException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                    return j;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public void update(JobI obj) throws MGXException {

        if (obj.getSeqruns() == null && obj.getAssembly() == null) {
            throw new MGXException("Job refers to neither sequencing runs nor assemblies.");
        }

        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Job().update(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        obj.modified();
    }

    @Override
    public TaskI<JobI> delete(JobI obj) throws MGXException {
        try {
            UUID uuid = getDTOmaster().Job().delete(obj.getId());
            return getMaster().<JobI>Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public List<JobI> ByAttributeTypeAndSeqRun(AttributeTypeI atype, SeqRunI run) throws MGXException {
        List<JobI> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().byAttributeTypeAndSeqRun(atype.getId(), run.getId())) {
                JobI j = JobDTOFactory.getInstance().toModel(getMaster(), dto);
                Iterator<SeqRunDTO> runs = getDTOmaster().SeqRun().byJob(j.getId());
                List<SeqRunI> tmp = new ArrayList<>(1);
                while (runs.hasNext()) {
                    SeqRunDTO sr = runs.next();
                    if (sr.getId() == run.getId()) {
                        tmp.add(run);
                    } else {
                        SeqRunI s = SeqRunDTOFactory.getInstance().toModel(getMaster(), sr);
                        tmp.add(s);
                    }

                }
                j.setSeqruns(tmp.toArray(new SeqRunI[]{}));
                all.add(j);
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return all;
    }

    @Override
    public List<JobI> BySeqRun(SeqRunI run) throws MGXException {
        List<JobI> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().bySeqRun(run.getId())) {
                JobI j = JobDTOFactory.getInstance().toModel(getMaster(), dto);

                Iterator<SeqRunDTO> runs = getDTOmaster().SeqRun().byJob(j.getId());
                List<SeqRunI> tmp = new ArrayList<>(1);
                while (runs.hasNext()) {
                    SeqRunDTO sr = runs.next();
                    if (sr.getId() == run.getId()) {
                        tmp.add(run);
                    } else {
                        SeqRunI s = SeqRunDTOFactory.getInstance().toModel(getMaster(), sr);
                        tmp.add(s);
                    }

                }
                j.setSeqruns(tmp.toArray(new SeqRunI[]{}));
                all.add(j);
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return all;
    }

    @Override
    public List<JobI> ByAssembly(AssemblyI ass) throws MGXException {
        List<JobI> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().byAssembly(ass.getId())) {
                JobI j = JobDTOFactory.getInstance().toModel(getMaster(), dto);
                j.setAssembly(ass);
                all.add(j);
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return all;
    }

    @Override
    public String getErrorMessage(JobI job) throws MGXException {
        try {
            MGXString err = getDTOmaster().Job().getError(job.getId());
            return err.getValue();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public void runDefaultTools(SeqRunI seqrun) throws MGXException {
        try {
            getDTOmaster().Job().runDefaultTools(seqrun.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

}
