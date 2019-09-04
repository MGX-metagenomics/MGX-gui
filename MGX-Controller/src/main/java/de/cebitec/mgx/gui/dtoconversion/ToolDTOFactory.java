package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.common.ToolScope;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.gui.datamodel.Tool;

/**
 *
 * @author sjaenick
 */
public class ToolDTOFactory extends DTOConversionBase<ToolI, ToolDTO> {

    static {
        instance = new ToolDTOFactory();
    }
    protected static ToolDTOFactory instance;

    private ToolDTOFactory() {
    }

    public static ToolDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final ToolDTO toDTO(ToolI s) {
        return ToolDTO.newBuilder()
                .setId(s.getId())
                .setName(s.getName())
                .setDescription(s.getDescription())
                .setVersion(s.getVersion())
                .setAuthor(s.getAuthor())
                .setUrl(s.getUrl())
                .setContent(s.getDefinition())
                .setScope(ToolDTO.ToolScope.forNumber(s.getScope().getValue()))
                .build();
    }

    public final ToolI toDB(MGXMasterI m, ToolDTO dto, boolean copyID) {
        ToolI t = new Tool(m)
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setVersion(dto.getVersion())
                .setAuthor(dto.getAuthor())
                .setScope(ToolScope.values()[dto.getScope().ordinal()])
                .setUrl(dto.getUrl());

        // XML data might not be present in the tools received from the
        // server
        if (!dto.getContent().isEmpty()) {
            t.setDefinition(dto.getContent());
        }

        if (copyID && dto.getId() != 0) {
            t.setId(dto.getId());
        }
        return t;
    }

    @Override
    public ToolI toModel(MGXMasterI m, ToolDTO dto) {
        return toDB(m, dto, true);
    }
}
