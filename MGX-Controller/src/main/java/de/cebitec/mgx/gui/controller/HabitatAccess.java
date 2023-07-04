package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.HabitatAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.HabitatDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class HabitatAccess extends AccessBase<HabitatI> implements HabitatAccessI {

    public HabitatAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public HabitatI create(String name, double latitude, double longitude, String biome, String description) throws MGXException {
        Habitat obj = new Habitat(getMaster())
                .setName(name)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setBiome(biome)
                .setDescription(description);
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Habitat().create(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.setId(id);
        return obj;
    }

//    @Override
//    public HabitatI create(HabitatI obj) throws MGXException {
//        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
//        long id = Identifiable.INVALID_IDENTIFIER;
//        try {
//            id = getDTOmaster().Habitat().create(dto);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        obj.setId(id);
//        return obj;
//    }
    @Override
    public HabitatI fetch(long id) throws MGXException {
        HabitatDTO h = null;
        try {
            h = getDTOmaster().Habitat().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        Habitat ret = HabitatDTOFactory.getInstance().toModel(getMaster(), h);
        return ret;
    }

    @Override
    public Iterator<HabitatI> fetchall() throws MGXException {
        try {
            Iterator<HabitatDTO> fetchall = getDTOmaster().Habitat().fetchall().getHabitatList().iterator();
            return new BaseIterator<HabitatDTO, HabitatI>(fetchall) {
                @Override
                public HabitatI next() {
                    HabitatI h = HabitatDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return h;
                }
            };

        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void update(HabitatI obj) throws MGXException {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Habitat().update(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI<HabitatI> delete(HabitatI obj) throws MGXException {
        try {
            UUID uuid = getDTOmaster().Habitat().delete(obj.getId());
            return getMaster().<HabitatI>Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }
}
