package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodefactory.JobBySeqRunNodeFactory;
import de.cebitec.mgx.gui.nodefactory.JobNodeFactory;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collection;
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

    private Collection<SeqRunI> runs = new HashSet<>();
    private JobNodeFactory jnf;

    public ProjectRootNode(MGXMasterI master) {
        this(master, null, new JobNodeFactory(master));
    }

    public ProjectRootNode(Set<SeqRunI> runs) {
        this(null, runs, new JobBySeqRunNodeFactory(runs));
    }

    private ProjectRootNode(MGXMasterI master, Set<SeqRunI> seqruns, final JobNodeFactory jnf) {
        super(Children.create(jnf, true), Lookups.fixed(master != null ? master : seqruns));
        String displayName = master != null ? master.getProject() : null;
        super.setDisplayName(displayName);
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
        super.setDisplayName(no_project_selected);
        jnf = null;
    }

    void refresh() {
        if (jnf != null) {
            jnf.refreshChildren();
        }
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
            if (!EventQueue.isDispatchThread()) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        fireNodeDestroyed();
                    }
                });
            } else {
                fireNodeDestroyed();
            }
        }
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        if (jnf != null) {
            jnf.destroy();
            jnf = null;
        }
        if (runs != null) {
            for (SeqRunI run : runs) {
                run.removePropertyChangeListener(this);
            }
            runs.clear();
            runs = null;
        }
    }

}
