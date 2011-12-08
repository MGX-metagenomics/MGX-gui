package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SampleDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Sample;

/**
 *
 * @author sjaenick
 */
public class SampleDTOFactory extends DTOConversionBase<Sample, SampleDTO> {

    static {
        instance = new SampleDTOFactory();
    }
    protected static SampleDTOFactory instance;

    private SampleDTOFactory() {}

    public static SampleDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final SampleDTO toDTO(Sample s) {
        Builder b = SampleDTO.newBuilder();
        if (s.getId() != null) {
            b.setId(s.getId());
        }
        return b.setHabitatId(s.getHabitat().getId())
                .setTemperature(s.getTemperature())
                .setMaterial(s.getMaterial())
                .setVolume(s.getVolume())
                .setVolumeUnit(s.getVolumeUnit())
                .setCollectiondate(toUnixTimeStamp(s.getCollectionDate()))
                .build();
    }

    @Override
    public final Sample toModel(SampleDTO dto) {
        Sample s = new Sample()
                .setCollectionDate(toDate(dto.getCollectiondate()))
                .setMaterial(dto.getMaterial())
                .setTemperature(dto.getTemperature())
                .setVolume(dto.getVolume())
                .setVolumeUnit(dto.getVolumeUnit());

        if (dto.hasId())
            s.setId(dto.getId());

        return s;
        // cannot set habitat here
    }
}
