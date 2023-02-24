package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.HabitatDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.api.model.Identifiable;

/**
 *
 * @author sjaenick
 */
public class HabitatDTOFactory extends DTOConversionBase<HabitatI, HabitatDTO> {

    protected final static HabitatDTOFactory instance = new HabitatDTOFactory();

    private HabitatDTOFactory() {
    }

    public static HabitatDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final HabitatDTO toDTO(HabitatI h) {
        Builder b = HabitatDTO.newBuilder();
        if (h.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(h.getId());
        }

        return b.setName(h.getName())
                .setGpsLatitude(h.getLatitude())
                .setGpsLongitude(h.getLongitude())
                .setBiome(h.getBiome())
                .setDescription(h.getDescription())
                .build();
    }

    @Override
    public final Habitat toModel(MGXMasterI m, HabitatDTO dto) {
        Habitat h = new Habitat(m)
                .setName(dto.getName())
                .setLatitude(dto.getGpsLatitude())
                .setLongitude(dto.getGpsLongitude())
                .setBiome(dto.getBiome());

        if (!dto.getDescription().isEmpty()) {
            h.setDescription(dto.getDescription());
        }

        h.setId(dto.getId());
        return h;
    }
}
