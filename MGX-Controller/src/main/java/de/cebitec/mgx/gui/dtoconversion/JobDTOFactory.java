package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTO.Builder;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.gui.datamodel.Job;
import java.util.ArrayList;

/**
 *
 * @author sjaenick
 */
public class JobDTOFactory extends DTOConversionBase<JobI, JobDTO> {

    static {
        instance = new JobDTOFactory();
    }
    protected static JobDTOFactory instance;

    private JobDTOFactory() {
    }

    public static JobDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final JobDTO toDTO(JobI j) {
        Builder b = JobDTO.newBuilder()
                .setId(j.getId())
                .setToolId(j.getTool().getId())
                .setCreator(j.getCreator())
                .setState(dto.JobState.forNumber(j.getStatus().getValue()));

        // either assembly or seqruns, but not both
        if (j.getAssembly() != null) {
            b.setAssemblyId(j.getAssembly().getId());
        } else {
            for (SeqRunI sr : j.getSeqruns()) {
                b.addSeqrun(sr.getId());
            }
        }

        if (j.getStartDate() != null) {
            b.setStartDate(toUnixTimeStamp(j.getStartDate()));
        }

        if (j.getFinishDate() != null) {
            b.setFinishDate(toUnixTimeStamp(j.getFinishDate()));
        }

        b.setParameters(JobParameterDTOFactory.getInstance().toList(j.getParameters()));

        return b.build();
    }

    @Override
    public final JobI toModel(MGXMasterI m, JobDTO dto) {

        JobI job = new Job(m, dto.getCreator())
                .setStatus(JobState.values()[dto.getState().ordinal()])
                .setStartDate(toDate(dto.getStartDate()))
                .setFinishDate(toDate(dto.getFinishDate()));

        if (dto.hasParameters()) {
            job.setParameters(new ArrayList<>(dto.getParameters().getParameterCount()));
            for (JobParameterDTO jpdto : dto.getParameters().getParameterList()) {
                JobParameterI jp = JobParameterDTOFactory.getInstance().toModel(m, jpdto);
                job.getParameters().add(jp);
            }
        }
        job.setId(dto.getId());

        return job;
    }
}
