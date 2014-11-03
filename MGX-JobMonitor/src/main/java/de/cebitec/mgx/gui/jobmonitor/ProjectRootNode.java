package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodefactory.JobBySeqRunNodeFactory;
import de.cebitec.mgx.gui.nodefactory.JobNodeFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class ProjectRootNode extends AbstractNode implements PropertyChangeListener {

    private final SeqRunI run;
    private final JobNodeFactory jnf;

    public ProjectRootNode(MGXMasterI master) {
        this(master, null, new JobNodeFactory(master));
    }

    public ProjectRootNode(SeqRunI run) {
        this(run.getMaster(), run, new JobBySeqRunNodeFactory(run.getMaster(), run));
        run.addPropertyChangeListener(this);
    }
    
    private ProjectRootNode(MGXMasterI master, SeqRunI run, JobNodeFactory jnf) {
        super(Children.create(jnf, true), Lookups.fixed(master));
        setDisplayName(master.getProject());
        this.jnf = jnf;
        this.run = run;
    }

    ProjectRootNode(String no_project_selected) {
        super(Children.LEAF);
        setDisplayName(no_project_selected);
        run = null;
        jnf = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ModelBase.OBJECT_DELETED)) {
            if (run != null) {
                run.removePropertyChangeListener(this);
            }
            fireNodeDestroyed();
        } else {
            System.err.println("ProjectRootNode got event " + evt.getPropertyName());
        }
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        if (jnf != null) {
            jnf.destroy();
        }
    }

}
