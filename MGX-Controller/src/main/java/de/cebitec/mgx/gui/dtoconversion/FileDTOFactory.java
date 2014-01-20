package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import java.io.File;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class FileDTOFactory extends DTOConversionBase<MGXFile, FileDTO> {

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
    public FileDTO toDTO(MGXFile a) {
        return FileDTO.newBuilder()
                .setName(a.getFullPath())
                .setIsDirectory(a.isDirectory())
                .build();
    }

    @Override
    public MGXFile toModel(FileDTO dto) {
        return new MGXFile(dto.getName(), dto.getIsDirectory(), dto.getSize());
    }
}
