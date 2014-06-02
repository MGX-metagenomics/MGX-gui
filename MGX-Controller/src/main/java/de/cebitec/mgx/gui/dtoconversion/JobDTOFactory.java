package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.JobState;
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
                .setSeqrunId(j.getSeqrun().getId())
                .setToolId(j.getTool().getId())
                .setCreator(j.getCreator())
                .setState(JobDTO.JobState.valueOf(j.getStatus().getValue()));

        if (j.getStartDate() != null) {
            b.setStartDate(toUnixTimeStamp(j.getStartDate()));
        }

        if (j.getFinishDate() != null) {
            b.setFinishDate(toUnixTimeStamp(j.getFinishDate()));
        }

        if (j.getParameters() != null && j.getParameters().size() > 0) {
            b.setParameters(JobParameterDTOFactory.getInstance().toList(j.getParameters()));
        }

        return b.build();
    }

    @Override
    public final JobI toModel(MGXMasterI m, JobDTO dto) {
        JobI j = new Job(m)
                .setStatus(JobState.values()[dto.getState().ordinal()])
                .setCreator(dto.getCreator())
                .setStartDate(toDate(dto.getStartDate()))
                .setFinishDate(toDate(dto.getFinishDate()));

        if (dto.hasParameters()) {
            j.setParameters(new ArrayList<JobParameterI>(dto.getParameters().getParameterCount()));
            for (JobParameterDTO jpdto : dto.getParameters().getParameterList()) {
                JobParameterI jp = JobParameterDTOFactory.getInstance().toModel(m, jpdto);
                j.getParameters().add(jp);
            }
        }
        j.setId(dto.getId());
        return j;
    }
}
