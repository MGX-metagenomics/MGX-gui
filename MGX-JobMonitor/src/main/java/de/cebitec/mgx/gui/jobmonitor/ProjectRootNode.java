package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodefactory.JobByAssemblyNodeFactory;
import de.cebitec.mgx.gui.nodefactory.JobBySeqRunNodeFactory;
import de.cebitec.mgx.gui.nodefactory.JobNodeFactory;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
class ProjectRootNode extends AbstractNode implements PropertyChangeListener {

    private final Collection<SeqRunI> runs = new HashSet<>();
    private final Collection<AssemblyI> assemblies = new HashSet<>();
    private JobNodeFactory jnf;

    public ProjectRootNode(MGXMasterI master) {
        this(master, null, null, new JobNodeFactory(master));
    }

    public ProjectRootNode(Set<SeqRunI> runs) {
        this(null, runs, null, new JobBySeqRunNodeFactory(runs));
    }

    ProjectRootNode(List<AssemblyI> assemblies) {
        this(null, null, assemblies, new JobByAssemblyNodeFactory(assemblies));
    }

    private ProjectRootNode(MGXMasterI master, Set<SeqRunI> seqruns, Collection<AssemblyI> assemb, final JobNodeFactory jnf) {
        super(Children.create(jnf, true),
                Lookups.fixed(master != null ? master
                        : seqruns != null ? seqruns : assemb));
        String displayName = master != null ? master.getProject() : null;
        super.setDisplayName(displayName);
        this.jnf = jnf;
        if (seqruns != null) {
            for (SeqRunI sr : seqruns) {
                sr.addPropertyChangeListener(this);
                runs.add(sr);
            }
        }
        if (assemb != null) {
            for (AssemblyI ass : assemb) {
                ass.addPropertyChangeListener(this);
                assemblies.add(ass);
            }
        }
    }

    ProjectRootNode(String no_project_selected) {
        super(Children.LEAF);
        super.setDisplayName(no_project_selected);
        jnf = null;
    }

//    void refresh() {
//        if (jnf != null) {
//            jnf.refresh();
//        }
//    }
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
            if (evt.getSource() instanceof AssemblyI) {
                AssemblyI ass = (AssemblyI) evt.getSource();
                if (assemblies != null && assemblies.contains(ass)) {
                    ass.removePropertyChangeListener(this);
                    assemblies.remove(ass);
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
    public boolean canDestroy() {
        return true;
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
        }
    }

}
