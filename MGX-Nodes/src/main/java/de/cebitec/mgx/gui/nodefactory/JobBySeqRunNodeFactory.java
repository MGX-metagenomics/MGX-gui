package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    private final Collection<SeqRunI> runs;
    private final PropertyChangeListener stateListener;

    public JobBySeqRunNodeFactory(Collection<SeqRunI> runs) {
        super(null);
        stateListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
                    if (evt.getSource() instanceof SeqRunI) {
                        SeqRunI sr = (SeqRunI) evt.getSource();
                        if (JobBySeqRunNodeFactory.this.runs.contains(sr)) {
                            sr.removePropertyChangeListener(stateListener);
                            JobBySeqRunNodeFactory.this.runs.remove(sr);
                        }
                    }
                }
            }

        };
        this.runs = runs;
        for (final SeqRunI run : runs) {
            run.addPropertyChangeListener(stateListener);
        }
    }

    @Override
    protected synchronized boolean addKeys(List<JobI> toPopulate) {
        Collection<JobI> tmp = new HashSet<>();
        try {
            for (SeqRunI run : runs) {
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
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    public void destroy() {
        for (final SeqRunI run : runs) {
            run.removePropertyChangeListener(stateListener);
        }
        super.destroy();
    }

//    @Override
//    public void resultChanged(LookupEvent le) {
//        if (!lkpInfo.allInstances().isEmpty()) {
//            refreshChildren();
//        }
//    }
}
