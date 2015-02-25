package de.cebitec.mgx.gui.controller;

import de.cebitec.gpms.rest.RESTMembershipI;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AttributeAccessI;
import de.cebitec.mgx.api.access.DNAExtractAccessI;
import de.cebitec.mgx.api.access.ObservationAccessI;
import de.cebitec.mgx.api.access.SeqRunAccessI;
import de.cebitec.mgx.api.access.TaskAccessI;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.client.MGXDTOMaster;
import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class MGXMaster extends MGXMasterI implements PropertyChangeListener {

    private final MGXDTOMaster dtomaster;
    private static final Logger logger = Logger.getLogger("MGX");
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXMasterI.class, "MGXMasterI");

    public MGXMaster(MGXDTOMaster dtomaster) {
        super(null, DATA_FLAVOR);
        this.dtomaster = dtomaster;
        dtomaster.addPropertyChangeListener(this);
        super.master = this; // ugly
    }

    @Override
    public RESTMembershipI getMembership() {
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

    @Override
    public HabitatAccess Habitat() {
        return new HabitatAccess(this, dtomaster);
    }

    @Override
    public AttributeAccessI Attribute() {
        return new AttributeAccess(dtomaster, this);
    }

    @Override
    public AttributeTypeAccess AttributeType() {
        return new AttributeTypeAccess(this, dtomaster);
    }

    @Override
    public SampleAccess Sample() {
        return new SampleAccess(this, dtomaster);
    }

    @Override
    public DNAExtractAccessI DNAExtract() {
        return new DNAExtractAccess(this, dtomaster);
    }

    @Override
    public SeqRunAccessI SeqRun() {
        return new SeqRunAccess(this, dtomaster);
    }

    @Override
    public ReferenceAccess Reference() {
        return new ReferenceAccess(dtomaster, this);
    }

    @Override
    public MappingAccess Mapping() {
        return new MappingAccess(dtomaster, this);
    }

    @Override
    public ObservationAccessI Observation() {
        return new ObservationAccess(master, dtomaster);
    }

    @Override
    public SequenceAccess Sequence() {
        return new SequenceAccess(master, dtomaster);
    }

    @Override
    public ToolAccess Tool() {
        return new ToolAccess(master, dtomaster);
    }

    @Override
    public JobAccess Job() {
        return new JobAccess(master, dtomaster);
    }

    @Override
    public FileAccess File() {
        return new FileAccess(master, dtomaster);
    }

    @Override
    public TermAccess Term() {
        return new TermAccess(master, dtomaster);
    }

    @Override
    public <T extends ModelBase> TaskAccessI<T> Task() {
        return new TaskAccess<>(master, dtomaster);
    }

    @Override
    public StatisticsAccess Statistics() {
        return new StatisticsAccess(master, dtomaster);
    }

    @Override
    public void log(Level lvl, String msg) {
        logger.log(lvl, msg);
    }

//    private <T extends AccessBase> T getAccessor(Class<T> clazz) {
//        if (!accessors.containsKey(clazz)) {
//            accessors.put(clazz, createDAO(clazz));
//        }
//        return (T) accessors.get(clazz);
//    }
//
//    private <T extends AccessBase> T createDAO(Class<T> clazz) {
//        try {
//            Constructor<T> ctor = clazz.getConstructor();
//            T instance = ctor.newInstance();
////            instance.setDTOmaster(dtomaster);
////            instance.setMaster(this);
//            return instance;
//        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
//            logger.log(Level.SEVERE, null, ex);
//        }
//        throw new UnsupportedOperationException("Could not create accessor for " + clazz);
//    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(MGXMasterI o) {
        return getProject().compareTo(o.getProject());
    }
}
