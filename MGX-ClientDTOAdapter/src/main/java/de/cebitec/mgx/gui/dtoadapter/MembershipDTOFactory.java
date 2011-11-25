package de.cebitec.mgx.gui.dtoadapter;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.mgx.dto.dto.MembershipDTO;

/**
 *
 * @author sjaenick
 */
public class MembershipDTOFactory extends DTOConversionBase<MembershipI, MembershipDTO> {

    static {
        instance = new MembershipDTOFactory();
    }
    protected static MembershipDTOFactory instance;

    private MembershipDTOFactory() {
    }

    public static MembershipDTOFactory getInstance() {
        return instance;
    }

    @Override
    public MembershipDTO toDTO(MembershipI m) {
        return MembershipDTO.newBuilder()
                .setProject(ProjectDTOFactory.getInstance().toDTO(m.getProject()))
                .setRole(RoleDTOFactory.getInstance().toDTO(m.getRole()))
                .build();
    }

    @Override
    public MembershipI toModel(MembershipDTO dto) {
        // not used
        return null;
    }
}
