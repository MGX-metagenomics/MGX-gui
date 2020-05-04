package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        content = new ArrayList<>(runs.size());
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
        Collection<JobI> jobs = null;
        try {
            synchronized (content) {
                jobs = processRuns(content);
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        toPopulate.addAll(jobs);
        return true;
    }

    @Override
    public void destroy() {
        synchronized (content) {
            for (final SeqRunI run : content) {
                run.removePropertyChangeListener(stateListener);
            }
            content.clear();
        }
        super.destroy();
    }
}
