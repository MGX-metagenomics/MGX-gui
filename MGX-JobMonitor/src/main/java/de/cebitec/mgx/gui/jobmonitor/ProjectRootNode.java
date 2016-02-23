package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodefactory.JobBySeqRunNodeFactory;
import de.cebitec.mgx.gui.nodefactory.JobNodeFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class ProjectRootNode extends AbstractNode implements PropertyChangeListener {

    private final Set<SeqRunI> runs = new HashSet<>();
    private final JobNodeFactory jnf;

    public ProjectRootNode(MGXMasterI master) {
        this(master, null, new JobNodeFactory(master));
    }

    public ProjectRootNode(Set<SeqRunI> runs) {
        this(null, runs, new JobBySeqRunNodeFactory(runs));
    }

    private ProjectRootNode(MGXMasterI master, Set<SeqRunI> seqruns, JobNodeFactory jnf) {
        super(Children.create(jnf, true), Lookups.fixed(master != null ? master : seqruns));
        String displayName = master != null ? master.getProject() : null;
        setDisplayName(displayName);
        this.jnf = jnf;
        if (seqruns != null) {
            for (SeqRunI sr : seqruns) {
                sr.addPropertyChangeListener(this);
                runs.add(sr);
            }
        }
    }

    ProjectRootNode(String no_project_selected) {
        super(Children.LEAF);
        setDisplayName(no_project_selected);
        jnf = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
            if (evt.getSource() instanceof SeqRunI) {
                SeqRunI run = (SeqRunI) evt.getSource();
                if (runs != null && runs.contains(run)) {
                    run.removePropertyChangeListener(this);
                    runs.remove(run);
                }
            }
            fireNodeDestroyed();
        } else {
            //System.err.println("ProjectRootNode got event " + evt);
        }
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        if (jnf != null) {
            jnf.destroy();
        }
        if (runs != null) {
            for (SeqRunI run : runs) {
                run.removePropertyChangeListener(this);
            }
            runs.clear();
        }
    }

}
