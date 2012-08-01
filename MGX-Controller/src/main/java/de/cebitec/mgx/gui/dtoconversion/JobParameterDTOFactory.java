package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.ChoicesDTO;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterDTO.Builder;
import de.cebitec.mgx.dto.dto.KVPair;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class JobParameterDTOFactory extends DTOConversionBase<JobParameter, JobParameterDTO> {

    protected final static JobParameterDTOFactory instance = new JobParameterDTOFactory();

    private JobParameterDTOFactory() {
    }

    public static JobParameterDTOFactory getInstance() {
        return instance;
    }

    @Override
    public JobParameterDTO toDTO(JobParameter p) {
        Builder b = JobParameterDTO.newBuilder()
                .setNodeId(p.getNodeId())
                .setUserName(p.getUserName())
                .setUserDesc(p.getUserDescription())
                .setDisplayName(p.getDisplayName())
                .setClassName(p.getClassName())
                .setConfigitemName(p.getConfigItemName())
                .setType(p.getType())
                .setIsOptional(p.isOptional());
        if (p.getId() != Identifiable.INVALID_IDENTIFIER) {
            b = b.setId(p.getId());
        }
        
        // choices
        if (p.getChoices() != null) {
            ChoicesDTO.Builder choices = ChoicesDTO.newBuilder();
            for (Entry<String, String> e : p.getChoices().entrySet()) {
                KVPair kv = KVPair.newBuilder()
                        .setKey(e.getKey())
                        .setValue(e.getValue())
                        .build();
                choices.addEntry(kv);
            }
            b.setChoices(choices.build());
        }
        
        if (p.getConfigItemValue() != null) {
            b.setConfigitemValue(p.getConfigItemValue());
        }
                
        if (p.getDefaultValue() != null) {
            b.setDefaultValue(p.getDefaultValue());
        }
        return b.build();
    }

    @Override
    public JobParameter toModel(JobParameterDTO dto) {
        JobParameter jp = new JobParameter();
        if (dto.hasId()) {
            jp.setId(dto.getId());
        }
        jp.setNodeId(dto.getNodeId());
        jp.setUserName(dto.getUserName());
        jp.setUserDescription(dto.getUserDesc());
        jp.setDisplayName(dto.getDisplayName());
        jp.setClassName(dto.getClassName());
        jp.setConfigItemName(dto.getConfigitemName());
        jp.setType(dto.getType());
        jp.setOptional(dto.getIsOptional());
        
        if (dto.hasChoices()) {
            jp.setChoices(new HashMap<String, String>());
            for (KVPair kv : dto.getChoices().getEntryList()) {
                jp.getChoices().put(kv.getKey(), kv.getValue());
            }
        }
        
        if (dto.hasConfigitemValue()) {
            jp.setConfigItemValue(dto.getConfigitemValue());
        }
        
        if (dto.hasDefaultValue()) {
            jp.setDefaultValue(dto.getDefaultValue());
        }
        
        return jp;
    }
}
