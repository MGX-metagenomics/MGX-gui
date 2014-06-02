package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SampleDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Sample;

/**
 *
 * @author sjaenick
 */
public class SampleDTOFactory extends DTOConversionBase<SampleI, SampleDTO> {

    static {
        instance = new SampleDTOFactory();
    }
    protected static SampleDTOFactory instance;

    private SampleDTOFactory() {
    }

    public static SampleDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final SampleDTO toDTO(SampleI s) {
        Builder b = SampleDTO.newBuilder();
        if (s.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(s.getId());
        }
        b = b.setHabitatId(s.getHabitatId())
                .setTemperature(s.getTemperature())
                .setMaterial(s.getMaterial())
                .setVolume(s.getVolume());

        b = b.setVolumeUnit(s.getVolumeUnit())
                .setCollectiondate(toUnixTimeStamp(s.getCollectionDate()));
        return b.build();
    }

    @Override
    public final SampleI toModel(MGXMasterI m, SampleDTO dto) {
        SampleI s = new Sample(m)
                .setHabitatId(dto.getHabitatId())
                .setCollectionDate(toDate(dto.getCollectiondate()))
                .setMaterial(dto.getMaterial())
                .setTemperature(dto.getTemperature())
                .setVolume(dto.getVolume())
                .setVolumeUnit(dto.getVolumeUnit());

        s.setId(dto.getId());
        return s;
    }
}
