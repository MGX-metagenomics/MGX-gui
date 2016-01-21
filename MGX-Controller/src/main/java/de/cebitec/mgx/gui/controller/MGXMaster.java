package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AttributeAccessI;
import de.cebitec.mgx.api.access.DNAExtractAccessI;
import de.cebitec.mgx.api.access.ObservationAccessI;
import de.cebitec.mgx.api.access.SeqRunAccessI;
import de.cebitec.mgx.api.access.TaskAccessI;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.client.MGXDTOMaster;
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

    public MGXMaster(MGXDTOMaster dtomaster) {
        super();
        this.dtomaster = dtomaster;
        dtomaster.addPropertyChangeListener(this);
    }

    @Override
    public String getProject() {
        return dtomaster.getProject().getName();
    }

    @Override
    public String getRoleName() {
        return dtomaster.getRole().getName();
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
        return new ObservationAccess(this, dtomaster);
    }

    @Override
    public SequenceAccess Sequence() {
        return new SequenceAccess(this, dtomaster);
    }

    @Override
    public ToolAccess Tool() {
        return new ToolAccess(this, dtomaster);
    }

    @Override
    public JobAccess Job() {
        return new JobAccess(this, dtomaster);
    }

    @Override
    public FileAccess File() {
        return new FileAccess(this, dtomaster);
    }

    @Override
    public TermAccess Term() {
        return new TermAccess(this, dtomaster);
    }

    @Override
    public <T extends MGXDataModelBaseI<T>> TaskAccessI<T> Task() {
        return new TaskAccess<>(this, dtomaster);
    }

    @Override
    public StatisticsAccess Statistics() {
        return new StatisticsAccess(this, dtomaster);
    }

    @Override
    public void log(Level lvl, String msg) {
        logger.log(lvl, msg);
    }

    @Override
    public int compareTo(MGXMasterI o) {
        return getProject().compareTo(o.getProject());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelBaseI.OBJECT_DELETED:
                dtomaster.removePropertyChangeListener(this);
                deleted();
            default:
                System.err.println("MGXMaster received event " + evt);
        }
    }
}
