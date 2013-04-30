package de.cebitec.mgx.gui.controller;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.datamodel.MGXMasterI;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class MGXMaster extends ModelBase implements MGXMasterI, PropertyChangeListener {

    private final MGXDTOMaster dtomaster;
    private static final Logger logger = Logger.getLogger("MGX");
    private final Map<Class, AccessBase> accessors;
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXMaster.class, "MGXMaster");

    public MGXMaster(MGXDTOMaster dtomaster) {
        super(DATA_FLAVOR);
        this.dtomaster = dtomaster;
        dtomaster.addPropertyChangeListener(this);
        accessors = new HashMap<>();
    }

    @Override
    public MembershipI getMembership() {
        return dtomaster.getMembership();
    }

    @Override
    public String getProject() {
        return dtomaster.getProject().getName();
    }

    @Override
    public String getLogin() {
        return dtomaster.getLogin();
    }

    public HabitatAccess Habitat() {
        return getAccessor(HabitatAccess.class);
    }

    public AttributeAccess Attribute() {
        return getAccessor(AttributeAccess.class);
    }

    public AttributeTypeAccess AttributeType() {
        return getAccessor(AttributeTypeAccess.class);
    }

    public SampleAccess Sample() {
        return getAccessor(SampleAccess.class);
    }

    public DNAExtractAccess DNAExtract() {
        return getAccessor(DNAExtractAccess.class);
    }

    public SeqRunAccess SeqRun() {
        return getAccessor(SeqRunAccess.class);
    }

    public ObservationAccess Observation() {
        return getAccessor(ObservationAccess.class);
    }

    public SequenceAccess Sequence() {
        return getAccessor(SequenceAccess.class);
    }

    public ToolAccess Tool() {
        return getAccessor(ToolAccess.class);
    }

    public JobAccess Job() {
        return getAccessor(JobAccess.class);
    }

    public FileAccess File() {
        return getAccessor(FileAccess.class);
    }

    public TermAccess Term() {
        return getAccessor(TermAccess.class);
    }

    public TaskAccess Task() {
        return getAccessor(TaskAccess.class);
    }

    void log(Level lvl, String msg) {
        logger.log(lvl, msg);
    }

    private <T extends AccessBase> T getAccessor(Class<T> clazz) {
        if (!accessors.containsKey(clazz)) {
            accessors.put(clazz, createDAO(clazz));
        }
        return (T) accessors.get(clazz);
    }

    private <T extends AccessBase> T createDAO(Class<T> clazz) {
        try {
            Constructor<T> ctor = clazz.getConstructor();
            T instance = ctor.newInstance();
            instance.setDTOmaster(dtomaster);
            instance.setMaster(this);
            return instance;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Could not create accessor for " + clazz);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
