package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.core.RoleI;
import de.cebitec.mgx.dto.dto.ProjectClassDTO;
import de.cebitec.mgx.dto.dto.RoleDTOList.Builder;
import de.cebitec.mgx.dto.dto.RoleDTOList;

/**
 *
 * @author sjaenick
 */
public class ProjectClassDTOFactory extends DTOConversionBase<ProjectClassI, ProjectClassDTO> {

    static {
        instance = new ProjectClassDTOFactory();
    }
    protected static ProjectClassDTOFactory instance;

    private ProjectClassDTOFactory() {
    }

    public static ProjectClassDTOFactory getInstance() {
        return instance;
    }

    @Override
    public ProjectClassDTO toDTO(ProjectClassI pc) {
        Builder roles = RoleDTOList.newBuilder();
        for (RoleI r : pc.getRoles()) {
            roles.addRole(RoleDTOFactory.getInstance().toDTO(r));
        }
        return ProjectClassDTO.newBuilder()
                .setName(pc.getName())
                .setRoles(roles.build())
                .build();
    }

    @Override
    public ProjectClassI toModel(ProjectClassDTO dto) {
        // not used
        return null;
    }
}
