package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTO.Builder;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.JobState;

/**
 *
 * @author sjaenick
 */
public class JobDTOFactory extends DTOConversionBase<Job, JobDTO> {

    static {
        instance = new JobDTOFactory();
    }
    protected static JobDTOFactory instance;

    private JobDTOFactory() {}

    public static JobDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final JobDTO toDTO(Job j) {
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
        
        b.setParameters(JobParameterDTOFactory.getInstance().toList(j.getParameters()));

        return b.build();
    }

    @Override
    public final Job toModel(JobDTO dto) {
        Job j = new Job()
                .setStatus(JobState.values()[dto.getState().ordinal()])
                .setCreator(dto.getCreator())
                .setStartDate(toDate(dto.getStartDate()))
                .setFinishDate(toDate(dto.getFinishDate()));

        for (JobParameterDTO jpdto : dto.getParameters().getParameterList()) {
            JobParameter jp = JobParameterDTOFactory.getInstance().toModel(jpdto);
            j.getParameters().add(jp);
        }
        j.setId(dto.getId());
        return j;
    }
}
