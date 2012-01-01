package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.datamodel.MGXMasterI;
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
public class MGXMaster implements MGXMasterI {

    private final MGXDTOMaster dtomaster;
    private static final Logger logger = Logger.getLogger("MGX");
    private final Map<Class, AccessBase> accessors;

    public MGXMaster(MGXDTOMaster dtomaster) {
        this.dtomaster = dtomaster;
        accessors = new HashMap<Class, AccessBase>();
    }

    public String getProject() {
        return dtomaster.getProject().getName();
    }

    public String getLogin() {
        return dtomaster.getLogin();
    }

    public HabitatAccess Habitat() {
        return getAccessor(HabitatAccess.class);
    }

    public AttributeAccess Attribute() {
        return getAccessor(AttributeAccess.class);
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
            return instance;
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Could not create accessor for " + clazz);
    }
}
