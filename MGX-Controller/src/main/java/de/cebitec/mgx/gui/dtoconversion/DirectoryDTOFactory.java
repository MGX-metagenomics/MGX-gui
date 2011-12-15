package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.Directory;
import de.cebitec.mgx.dto.dto.Directory.Builder;
import de.cebitec.mgx.dto.dto.FileOrDirectory;
import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.datamodel.MGXDirectory;

/**
 *
 * @author sjaenick
 */
public class DirectoryDTOFactory extends DTOConversionBase<MGXDirectory, Directory> {

    static {
        instance = new DirectoryDTOFactory();
    }
    protected static DirectoryDTOFactory instance;

    private DirectoryDTOFactory() {
    }

    public static DirectoryDTOFactory getInstance() {
        return instance;
    }

    @Override
    public Directory toDTO(MGXDirectory a) {
        Builder b = Directory.newBuilder().setName(a.getName());
        for (DirEntry d : a.getEntries()) {
            FileOrDirectory fod = DirEntryDTOFactory.getInstance().toDTO(d);
            b.addFile(fod);
        }
        return b.build();
    }

    @Override
    public MGXDirectory toModel(Directory dto) {
        MGXDirectory dir = new MGXDirectory();
        dir.setName(dto.getName());
        for (FileOrDirectory fod : dto.getFileList()) {
            DirEntry dirEntry = DirEntryDTOFactory.getInstance().toModel(fod);
            dir.getEntries().add(dirEntry);
        }
        return dir;
    }
}
