package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobBySeqRunNodeFactory extends JobNodeFactory {

    private final Collection<SeqRunI> content;
    private final PropertyChangeListener stateListener;

    public JobBySeqRunNodeFactory(Collection<SeqRunI> runs) {
        super(null);
        content = new ArrayList<>();
        stateListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
                    if (evt.getSource() instanceof SeqRunI) {
                        SeqRunI sr = (SeqRunI) evt.getSource();
                        synchronized (content) {
                            if (content.contains(sr)) {
                                //System.err.println(Thread.currentThread().getName() + " propChange(): removing listener " + stateListener + " from run " + sr.getName());
                                sr.removePropertyChangeListener(stateListener);
                                content.remove(sr);
                            }
                        }
                    }
                }
            }

        };
        synchronized (content) {
            this.content.addAll(runs);
            for (final SeqRunI run : content) {
                run.addPropertyChangeListener(stateListener);
            }
        }
    }

    @Override
    protected boolean addKeys(List<JobI> toPopulate) {
        Collection<JobI> tmp = new HashSet<>();
        try {
            SeqRunI[] toArray;
            synchronized (content) {
               toArray = content.toArray(new SeqRunI[]{});
            }
            for (SeqRunI run : toArray) {
                for (JobI j : run.getMaster().Job().BySeqRun(run)) {
                    if (Thread.interrupted()) {
                        run.getMaster().log(Level.INFO, "interrupted in NF");
                        return true;
                    }

                    ToolI t = null;
                    try {
                        t = run.getMaster().Tool().ByJob(j);
                    } catch (MGXException ex) {
                        // if a refresh is triggered while a job is being deleted,
                        // this might fail when the job is already gone. silently
                        // ignore this case..
                    }

                    if (t != null) {
                        j.setTool(t);
                        if (!run.isDeleted()) {
                            tmp.add(j);
                        }
                    }
                }
            }
            toPopulate.addAll(tmp);
            Collections.sort(toPopulate);
        } catch (MGXLoggedoutException ex) {
            toPopulate.clear();
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    public void destroy() {
        synchronized (content) {
            for (final SeqRunI run : content) {
                //System.err.println("destroy(): removing listener " + stateListener + " from run " + run.getName());
                run.removePropertyChangeListener(stateListener);
            }
            content.clear();
        }
        super.destroy();
    }
}
