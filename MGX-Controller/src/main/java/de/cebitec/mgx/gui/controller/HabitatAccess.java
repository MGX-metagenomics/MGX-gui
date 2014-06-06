package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.HabitatDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class HabitatAccess extends AccessBase<HabitatI> {

    public HabitatAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
    }

    @Override
    public HabitatI create(HabitatI obj) {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Habitat().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        return obj;
    }

    @Override
    public HabitatI fetch(long id) {
        HabitatDTO h = null;
        try {
            h = getDTOmaster().Habitat().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        Habitat ret = HabitatDTOFactory.getInstance().toModel(getMaster(), h);
        return ret;
    }

    @Override
    public Iterator<HabitatI> fetchall() {
        try {
            Iterator<HabitatDTO> fetchall = getDTOmaster().Habitat().fetchall();
            return new BaseIterator<HabitatDTO, HabitatI>(fetchall) {
                @Override
                public HabitatI next() {
                    HabitatI h = HabitatDTOFactory.getInstance().toModel(getMaster(),iter.next());
                    return h;
                }
            };

        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    @Override
    public void update(HabitatI obj) {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Habitat().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI delete(HabitatI obj) {
        TaskI t = null;
        try {
            UUID uuid = getDTOmaster().Habitat().delete(obj.getId());
            t = getMaster().Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
    }
}
