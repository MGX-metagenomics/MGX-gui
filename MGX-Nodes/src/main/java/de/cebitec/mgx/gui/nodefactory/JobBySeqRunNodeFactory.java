package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.nodes.JobNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobBySeqRunNodeFactory extends JobNodeFactory {

    private final SeqRunI run;

    public JobBySeqRunNodeFactory(SeqRunI run) {
        super();
        this.run = run;
        Timer timer = new Timer(1000 * 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshChildren();
            }
        });
        timer.start();
    }

    @Override
    protected synchronized boolean createKeys(List<JobI> toPopulate) {

        if (!busy) {
            busy = true;
            MGXMasterI master = run.getMaster();
            try {
                for (JobI j : master.Job().BySeqRun(run)) {
                    //j.setSeqrun(run);
                    ToolI t = master.Tool().ByJob(j);
                    j.setTool(t);
                    toPopulate.add(j);
                }
                Collections.sort(toPopulate);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            busy = false;
            return true;
        } else {
            return false;
        }
    }
}
