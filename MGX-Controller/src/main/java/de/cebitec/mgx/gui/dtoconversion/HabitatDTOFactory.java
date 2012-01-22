package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.HabitatDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Habitat;

/**
 *
 * @author sjaenick
 */
public class HabitatDTOFactory extends DTOConversionBase<Habitat, HabitatDTO> {

    protected final static HabitatDTOFactory instance = new HabitatDTOFactory();

    private HabitatDTOFactory() {
    }

    public static HabitatDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final HabitatDTO toDTO(Habitat h) {
        Builder b = HabitatDTO.newBuilder();
        if (h.getId() != null) {
            b.setId(h.getId());
        }
        
        return b.setName(h.getName())
                .setGpsLatitude(h.getLatitude())
                .setGpsLongitude(h.getLongitude())
                .setAltitude(h.getAltitude())
                .setBiome(h.getBiome())
                .setDescription(h.getDescription())
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

        h.setId(dto.getId());
        return h;
    }
}
