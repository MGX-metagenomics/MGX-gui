package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.gpms.core.ProjectI;
import de.cebitec.mgx.dto.dto.ProjectDTO;

/**
 *
 * @author sjaenick
 */
public class ProjectDTOFactory extends DTOConversionBase<ProjectI, ProjectDTO> {

    static {
        instance = new ProjectDTOFactory();
    }
    protected static ProjectDTOFactory instance;

    private ProjectDTOFactory() {
    }

    public static ProjectDTOFactory getInstance() {
        return instance;
    }

    @Override
    public ProjectDTO toDTO(ProjectI p) {
        return ProjectDTO.newBuilder()
                .setName(p.getName())
                .setProjectClass(ProjectClassDTOFactory.getInstance().toDTO(p.getProjectClass()))
                .build();
    }

    @Override
    public ProjectI toModel(ProjectDTO dto) {
        // not used
        return null;
    }
}
