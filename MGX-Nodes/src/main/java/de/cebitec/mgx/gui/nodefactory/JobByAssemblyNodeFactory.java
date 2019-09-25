package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
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
public class JobByAssemblyNodeFactory extends JobNodeFactory {

    private final Collection<AssemblyI> content;
    private final PropertyChangeListener stateListener;

    public JobByAssemblyNodeFactory(Collection<AssemblyI> ass) {
        super(null);
        content = new ArrayList<>(ass.size());
        stateListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
                    if (evt.getSource() instanceof AssemblyI) {
                        AssemblyI a = (AssemblyI) evt.getSource();
                        synchronized (content) {
                            if (content.contains(a)) {
                                //System.err.println(Thread.currentThread().getName() + " propChange(): removing listener " + stateListener + " from run " + sr.getName());
                                a.removePropertyChangeListener(stateListener);
                                content.remove(a);
                            }
                        }
                    }
                }
            }

        };
        this.content.addAll(ass);
        for (final AssemblyI a : content) {
            a.addPropertyChangeListener(stateListener);
        }
    }

    @Override
    protected boolean addKeys(List<JobI> toPopulate) {
        Collection<JobI> jobs = null;
        try {
            jobs = processAssemblies(content);
        } catch (MGXLoggedoutException ex) {
            toPopulate.clear();
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        toPopulate.addAll(jobs);
        return true;
    }

    @Override
    public void destroy() {
        synchronized (content) {
            for (final AssemblyI ass : content) {
                ass.removePropertyChangeListener(stateListener);
            }
            content.clear();
        }
        super.destroy();
    }
}
