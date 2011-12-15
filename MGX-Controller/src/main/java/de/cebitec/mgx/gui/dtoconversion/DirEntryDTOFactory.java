package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.Directory;
import de.cebitec.mgx.dto.dto.File;
import de.cebitec.mgx.dto.dto.FileOrDirectory;
import de.cebitec.mgx.dto.dto.FileOrDirectory.Builder;
import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.datamodel.MGXDirectory;
import de.cebitec.mgx.gui.datamodel.MGXFile;

/**
 *
 * @author sjaenick
 */
public class DirEntryDTOFactory extends DTOConversionBase<DirEntry, FileOrDirectory> {

    static {
        instance = new DirEntryDTOFactory();
    }
    protected static DirEntryDTOFactory instance;

    private DirEntryDTOFactory() {
    }

    public static DirEntryDTOFactory getInstance() {
        return instance;
    }

    @Override
    public FileOrDirectory toDTO(DirEntry a) {
        Builder b = FileOrDirectory.newBuilder();
        if (a.isDirectory()) {
            Directory dirDTO = DirectoryDTOFactory.getInstance().toDTO(a.getDirectory());
            b.setDirectory(dirDTO);
        } else if (a.isFile()) {
            File fileDTO = FileDTOFactory.getInstance().toDTO(a.getFile());
            b.setFile(fileDTO);
        } else {
            throw new RuntimeException("Unknown object");
        }
        return b.build();
    }

    @Override
    public DirEntry toModel(FileOrDirectory dto) {
        DirEntry d = new DirEntry();
        if (dto.hasDirectory()) {
            MGXDirectory toModel = DirectoryDTOFactory.getInstance().toModel(dto.getDirectory());
            d.setDirectory(toModel);
        } else if (dto.hasFile()) {
            MGXFile toModel = FileDTOFactory.getInstance().toModel(dto.getFile());
            d.setFile(toModel);
        }
        return d;
    }
}
