package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;

/**
 *
 * @author sjaenick
 */
public class HabitatDTOFactory extends DTOConversionBase<Habitat, HabitatDTO> {

    static {
        instance = new HabitatDTOFactory();
    }
    protected static HabitatDTOFactory instance;

    private HabitatDTOFactory() {
    }

    public static HabitatDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final HabitatDTO toDTO(Habitat h) {
        return HabitatDTO.newBuilder()
                .setId(h.getId())
                .setName(h.getName())
                .setGpsLatitude(h.getLatitude())
                .setGpsLongitude(h.getLongitude())
                .setAltitude(h.getAltitude())
                .setBiome(h.getBiome())
                .setDescription(h.getDescription())
                .setId(h.getId())
                .build();
    }

    @Override
    public final Habitat toModel(HabitatDTO dto) {
        Habitat h = new Habitat()
                .setName(dto.getName())
                .setLatitude(dto.getGpsLatitude())
                .setLongitude(dto.getGpsLongitude())
                .setAltitude(dto.getAltitude())
                .setBiome(dto.getBiome());
        
        if (dto.hasDescription()) {
                h.setDescription(dto.getDescription());
        }

        if (dto.hasId()) {
            h.setId(dto.getId());
        }

        return h;
    }
}
