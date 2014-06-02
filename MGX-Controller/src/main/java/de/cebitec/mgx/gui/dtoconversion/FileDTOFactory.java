package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.gui.datamodel.MGXFile;

/**
 *
 * @author sjaenick
 */
public class FileDTOFactory extends DTOConversionBase<MGXFileI, FileDTO> {

    static {
        instance = new FileDTOFactory();
    }
    protected static FileDTOFactory instance;

    private FileDTOFactory() {
    }

    public static FileDTOFactory getInstance() {
        return instance;
    }

    @Override
    public FileDTO toDTO(MGXFileI a) {
        return FileDTO.newBuilder()
                .setName(a.getFullPath())
                .setIsDirectory(a.isDirectory())
                .build();
    }

    @Override
    public MGXFileI toModel(MGXMasterI m, FileDTO dto) {
        return new MGXFile(m, dto.getName(), dto.getIsDirectory(), dto.getSize());
    }
}
