package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.SampleAccessI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.dtoconversion.SampleDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<SampleI> implements SampleAccessI {

    public SampleAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
    }

    @Override
    public SampleI create(HabitatI habitat, Date collectionDate, String material, double temperature, int volume, String volUnit) {
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
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        return obj;
    }

    @Override
    public SampleI create(SampleI obj) {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Sample().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        return obj;
    }

    @Override
    public SampleI fetch(long id) {
        SampleDTO dto = null;
        try {
            dto = getDTOmaster().Sample().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        SampleI s = SampleDTOFactory.getInstance().toModel(getMaster(), dto);
        return s;
    }

    @Override
    public Iterator<SampleI> fetchall() {
        try {
            Iterator<SampleDTO> fetchall = getDTOmaster().Sample().fetchall();
            return new BaseIterator<SampleDTO, SampleI>(fetchall) {
                @Override
                public SampleI next() {
                    SampleI s = SampleDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return s;
                }
            };

        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    @Override
    public void update(SampleI obj) {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Sample().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI delete(SampleI obj) {
        TaskI ret = null;
        try {
            UUID uuid = getDTOmaster().Sample().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    public Iterator<SampleI> ByHabitat(final long hab_id) {
        try {
            Iterator<SampleDTO> fetchall = getDTOmaster().Sample().ByHabitat(hab_id);
            return new BaseIterator<SampleDTO, SampleI>(fetchall) {
                @Override
                public SampleI next() {
                    SampleI s = SampleDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return s;
                }
            };

        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

}
