package de.cebitec.mgx.gui.dtoconversion;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author sjaenick
 */
public class JobDTOFactory extends DTOConversionBase<JobI, JobDTO> {

    static {
        instance = new JobDTOFactory();
    }
    protected static JobDTOFactory instance;
    private final Cache<CacheKey, JobI> instanceCache;

    private JobDTOFactory() {
        instanceCache = CacheBuilder.<CacheKey, JobI>newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .concurrencyLevel(10)
                .build();
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

        JobI job = instanceCache.getIfPresent(new CacheKey(dto.getId(), m));

        if (job != null) {
            job.setStatus(JobState.values()[dto.getState().ordinal()]);
            job.setStartDate(new Date(1000L * dto.getStartDate()));
            job.setFinishDate(new Date(1000L * dto.getFinishDate()));
        } else {
            job = new Job(m)
                    .setStatus(JobState.values()[dto.getState().ordinal()])
                    .setCreator(dto.getCreator())
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
            instanceCache.put(new CacheKey(dto.getId(), m), job);
        }

        return job;
    }

    private final static class CacheKey {

        private final long jobId;
        private final MGXMasterI master;

        public CacheKey(long jobId, MGXMasterI master) {
            this.jobId = jobId;
            this.master = master;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (int) (this.jobId ^ (this.jobId >>> 32));
            hash = 97 * hash + Objects.hashCode(this.master);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CacheKey other = (CacheKey) obj;
            if (this.jobId != other.jobId) {
                return false;
            }
            if (!Objects.equals(this.master, other.master)) {
                return false;
            }
            return true;
        }

    }
}
