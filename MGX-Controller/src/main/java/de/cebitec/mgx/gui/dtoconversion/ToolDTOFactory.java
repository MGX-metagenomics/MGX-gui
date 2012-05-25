package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.gui.datamodel.Tool;

/**
 *
 * @author sjaenick
 */
public class ToolDTOFactory extends DTOConversionBase<Tool, ToolDTO> {

    static {
        instance = new ToolDTOFactory();
    }
    protected static ToolDTOFactory instance;

    private ToolDTOFactory() {}

    public static ToolDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final ToolDTO toDTO(Tool s) {
        return ToolDTO.newBuilder()
                .setId(s.getId())
                .setName(s.getName())
                .setDescription(s.getDescription())
                .setVersion(s.getVersion())
                .setAuthor(s.getAuthor())
                .setUrl(s.getUrl())
                .setXml(s.getXMLFile())
                .build();
    }

    public final Tool toDB(ToolDTO dto, boolean copyID) {
        Tool t = new Tool()
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setVersion(dto.getVersion())
                .setAuthor(dto.getAuthor())
                .setUrl(dto.getUrl());
        
        // XML data is not present in the tools received from the
        // server
        
        if (copyID && dto.hasId()) {
            t.setId(dto.getId());
        }
        return t;
    }

    @Override
    public Tool toModel(ToolDTO dto) {
        return toDB(dto, true);
    }
}
