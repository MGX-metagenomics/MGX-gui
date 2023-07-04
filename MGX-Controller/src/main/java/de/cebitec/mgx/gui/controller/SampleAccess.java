package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.SampleAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.dtoconversion.SampleDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<SampleI> implements SampleAccessI {

    public SampleAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public SampleI create(HabitatI habitat, Date collectionDate, String material, double temperature, int volume, String volUnit) throws MGXException {
        Sample obj = new Sample(getMaster())
                .setHabitatId(habitat.getId())
                .setCollectionDate(collectionDate)
                .setMaterial(material)
                .setTemperature(temperature)
                .setVolume(volume)
                .setVolumeUnit(volUnit);
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Sample().create(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.setId(id);
        return obj;
    }

//    @Override
//    public SampleI create(SampleI obj) throws MGXException {
//        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
//        long id = Identifiable.INVALID_IDENTIFIER;
//        try {
//            id = getDTOmaster().Sample().create(dto);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        obj.setId(id);
//        return obj;
//    }
    @Override
    public SampleI fetch(long id) throws MGXException {
        SampleDTO dto = null;
        try {
            dto = getDTOmaster().Sample().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        SampleI s = SampleDTOFactory.getInstance().toModel(getMaster(), dto);
        return s;
    }

    @Override
    public Iterator<SampleI> fetchall() throws MGXException {
        try {
            Iterator<SampleDTO> fetchall = getDTOmaster().Sample().fetchall().getSampleList().iterator();
            return new BaseIterator<SampleDTO, SampleI>(fetchall) {
                @Override
                public SampleI next() {
                    SampleI s = SampleDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return s;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void update(SampleI obj) throws MGXException {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Sample().update(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI<SampleI> delete(SampleI obj) throws MGXException {
        TaskI<SampleI> ret = null;
        try {
            UUID uuid = getDTOmaster().Sample().delete(obj.getId());
            ret = getMaster().<SampleI>Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return ret;
    }

    @Override
    public Iterator<SampleI> ByHabitat(final HabitatI habitat) throws MGXException {
        try {
            Iterator<SampleDTO> fetchall = getDTOmaster().Sample().byHabitat(habitat.getId());
            return new BaseIterator<SampleDTO, SampleI>(fetchall) {
                @Override
                public SampleI next() {
                    SampleI s = SampleDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return s;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

}
