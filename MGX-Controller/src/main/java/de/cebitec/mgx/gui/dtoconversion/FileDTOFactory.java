package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.File;
import de.cebitec.mgx.gui.datamodel.MGXFile;

/**
 *
 * @author sjaenick
 */
public class FileDTOFactory extends DTOConversionBase<MGXFile, File> {

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
    public File toDTO(MGXFile a) {
        return File.newBuilder().setName(a.getName()).build();
    }

    @Override
    public MGXFile toModel(File dto) {
        MGXFile f = new MGXFile();
        f.setName(dto.getName());
        return f;
    }
}
