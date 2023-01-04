package de.cebitec.mgx.gui.controller;

import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AttributeAccessI;
import de.cebitec.mgx.api.access.AttributeTypeAccessI;
import de.cebitec.mgx.api.access.DNAExtractAccessI;
import de.cebitec.mgx.api.access.FileAccessI;
import de.cebitec.mgx.api.access.HabitatAccessI;
import de.cebitec.mgx.api.access.JobAccessI;
import de.cebitec.mgx.api.access.MappingAccessI;
import de.cebitec.mgx.api.access.ObservationAccessI;
import de.cebitec.mgx.api.access.ReferenceAccessI;
import de.cebitec.mgx.api.access.ReferenceRegionAccessI;
import de.cebitec.mgx.api.access.SeqRunAccessI;
import de.cebitec.mgx.api.access.SequenceAccessI;
import de.cebitec.mgx.api.access.TaskAccessI;
import de.cebitec.mgx.api.access.ToolAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.assembly.access.AssembledRegionAccessI;
import de.cebitec.mgx.api.model.assembly.access.AssemblyAccessI;
import de.cebitec.mgx.api.model.assembly.access.BinAccessI;
import de.cebitec.mgx.api.model.assembly.access.ContigAccessI;
import de.cebitec.mgx.api.model.assembly.access.GeneCoverageAccessI;
import de.cebitec.mgx.api.model.assembly.access.GeneObservationAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.controller.assembly.AssembledRegionAccess;
import de.cebitec.mgx.gui.controller.assembly.AssemblyAccess;
import de.cebitec.mgx.gui.controller.assembly.BinAccess;
import de.cebitec.mgx.gui.controller.assembly.ContigAccess;
import de.cebitec.mgx.gui.controller.assembly.GeneCoverageAccess;
import de.cebitec.mgx.gui.controller.assembly.GeneObservationAccess;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class MGXMaster extends MGXMasterI implements PropertyChangeListener {

    protected final MGXDTOMaster dtomaster;
    private final String serverName;
    private final String projectName;
    private static final Logger logger = Logger.getLogger("MGX");
    //

    MGXMaster(RESTMasterI restMaster) {
        this(new MGXDTOMaster(restMaster));
    }

    protected MGXMaster(MGXDTOMaster dtomaster) {
        super();
        this.serverName = dtomaster.getServerName();
        this.projectName = dtomaster.getProject().getName();
        this.dtomaster = dtomaster;
        dtomaster.addPropertyChangeListener(this);
    }

    @Override
    public final String getServerName() {
        return serverName;
    }

    @Override
    public synchronized void close() {
        if (toolAccess != null) {
            toolAccess.dispose();
        }
        dtomaster.close();
    }

    @Override
    public boolean isDeleted() {
        return super.isDeleted() || dtomaster.isClosed();
    }

    @Override
    public final String getProject() {
        return projectName;
    }

    @Override
    public final String getRoleName() {
        return dtomaster.getRole().getName();
    }

    @Override
    public String getLogin() {
        return dtomaster.getLogin();
    }

    @Override
    public HabitatAccessI Habitat() throws MGXException {
        return new HabitatAccess(this, dtomaster);
    }

    @Override
    public AttributeAccessI Attribute() throws MGXException {
        return new AttributeAccess(dtomaster, this);
    }

    @Override
    public AttributeTypeAccessI AttributeType() throws MGXException {
        return new AttributeTypeAccess(this, dtomaster);
    }

    @Override
    public SampleAccess Sample() throws MGXException {
        return new SampleAccess(this, dtomaster);
    }

    @Override
    public DNAExtractAccessI DNAExtract() throws MGXException {
        return new DNAExtractAccess(this, dtomaster);
    }

    @Override
    public SeqRunAccessI SeqRun() throws MGXException {
        return new SeqRunAccess(this, dtomaster);
    }

    @Override
    public ReferenceAccessI Reference() throws MGXException {
        return new ReferenceAccess(dtomaster, this);
    }

    @Override
    public ReferenceRegionAccessI ReferenceRegion() throws MGXException {
        return new ReferenceRegionAccess(dtomaster, this);
    }

    @Override
    public MappingAccessI Mapping() throws MGXException {
        return new MappingAccess(dtomaster, this);
    }

    @Override
    public ObservationAccessI Observation() throws MGXException {
        return new ObservationAccess(this, dtomaster);
    }

    @Override
    public SequenceAccessI Sequence() throws MGXException {
        return new SequenceAccess(this, dtomaster);
    }

    private ToolAccessI toolAccess = null;

    @Override
    public ToolAccessI Tool() throws MGXException {
        if (toolAccess == null) {
            toolAccess = new ToolAccess(this, dtomaster);
        }
        return toolAccess;
    }

    @Override
    public JobAccessI Job() throws MGXException {
        return new JobAccess(this, dtomaster);
    }

    @Override
    public FileAccessI File() throws MGXException {
        return new FileAccess(this, dtomaster);
    }

    @Override
    public TermAccess Term() throws MGXException {
        return new TermAccess(this, dtomaster);
    }

    @Override
    public <T extends MGXDataModelBaseI<T>> TaskAccessI<T> Task() throws MGXException {
        return new TaskAccess<>(this, dtomaster);
    }

    @Override
    public StatisticsAccess Statistics() throws MGXException {
        return new StatisticsAccess(this, dtomaster);
    }

    @Override
    public AssemblyAccessI Assembly() throws MGXException {
        return new AssemblyAccess(getMaster(), dtomaster);
    }

    @Override
    public BinAccessI Bin() throws MGXException {
        return new BinAccess(getMaster(), dtomaster);
    }

    @Override
    public ContigAccessI Contig() throws MGXException {
        return new ContigAccess(getMaster(), dtomaster);
    }

    @Override
    public AssembledRegionAccessI AssembledRegion() throws MGXException {
        return new AssembledRegionAccess(getMaster(), dtomaster);
    }

    @Override
    public GeneCoverageAccessI GeneCoverage() throws MGXException {
        return new GeneCoverageAccess(getMaster(), dtomaster);
    }

    @Override
    public GeneObservationAccessI GeneObservation() throws MGXException {
        return new GeneObservationAccess(getMaster(), dtomaster);
    }

    @Override
    public void log(Level lvl, String msg) {
        logger.log(lvl, msg);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelBaseI.OBJECT_DELETED:
                dtomaster.removePropertyChangeListener(this);
                deleted();
                break;
            case MGXDTOMaster.PROP_LOGGEDIN:
                if (evt.getSource() == dtomaster && evt.getNewValue() instanceof Boolean) {
                    Boolean isLoggedIn = (Boolean) evt.getNewValue();
                    if (!isDeleted() && !isLoggedIn) {
                        dtomaster.removePropertyChangeListener(this);
                        deleted();
                    }
                }
                break;
            default:
                System.err.println("MGXMaster received event " + evt);
        }
    }

    @Override
    public int compareTo(MGXMasterI o) {
        return getProject().compareTo(o.getProject());
    }

    @Override
    public String toString() {
        return "MGXMaster{" + "projectName=" + projectName + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.dtomaster);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MGXMaster other = (MGXMaster) obj;
        if (!Objects.equals(this.dtomaster, other.dtomaster)) {
            return false;
        }
        return true;
    }

}
