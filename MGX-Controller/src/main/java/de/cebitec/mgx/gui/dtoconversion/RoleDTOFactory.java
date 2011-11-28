package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.gpms.core.RoleI;
import de.cebitec.mgx.dto.dto.RoleDTO;

/**
 *
 * @author sjaenick
 */
public class RoleDTOFactory extends DTOConversionBase<RoleI, RoleDTO> {

    static {
        instance = new RoleDTOFactory();
    }
    protected static RoleDTOFactory instance;

    private RoleDTOFactory() {
    }

    public static RoleDTOFactory getInstance() {
        return instance;
    }

    @Override
    public RoleDTO toDTO(RoleI r) {
        return RoleDTO.newBuilder()
                .setName(r.getName())
                .build();
    }

    @Override
    public RoleI toModel(RoleDTO dto) {
        // not used
        return null;
    }
}
