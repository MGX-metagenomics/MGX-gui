package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodes.JobNode;
import de.cebitec.mgx.gui.pool.MGXPool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import javax.swing.Timer;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobNodeFactory extends MGXNodeFactoryBase<MGXMasterI, JobI> {

    private final Timer timer;

    public JobNodeFactory(MGXMasterI master) {
        super(master);
        timer = new Timer(1000 * 20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(false);
            }
        });
        timer.start();
    }

    @Override
    protected boolean addKeys(List<JobI> toPopulate) {
        final MGXMasterI master = getMaster();
        //
        // collect all seqruns for this master
        //
        List<SeqRunI> seqruns = new ArrayList<>();
        try {
            Iterator<SeqRunI> runIter = master.SeqRun().fetchall();
            while (runIter != null && runIter.hasNext()) {
                if (Thread.interrupted()) {
                    getMaster().log(Level.INFO, "interrupted in NF");
                    return true;
                }
                seqruns.add(runIter.next());
            }

            Collection<JobI> jobs = processRuns(seqruns);
            toPopulate.addAll(jobs);
        } catch (MGXLoggedoutException ex) {
            toPopulate.clear();
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        return true;
    }

    protected final static Collection<JobI> processRuns(Collection<SeqRunI> seqruns) throws MGXException {
        //
        // parallel fetch of missing data
        //
        List<JobI> jobs = new ArrayList<>();
        CountDownLatch allDone = new CountDownLatch(seqruns.size());
        for (SeqRunI run : seqruns) {
            MGXPool.getInstance().submit(new FillSeqRun(run, jobs, allDone));
        }
        try {
            allDone.await();
        } catch (InterruptedException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof MGXException) {
                throw (MGXException) ex.getCause();
            }
        }
        return jobs;
    }

    @Override
    protected JobNode createNodeFor(JobI job) {
        return new JobNode(job);
    }

    public void destroy() {
        timer.stop();
    }

    private final static class FillSeqRun implements Runnable {

        private final SeqRunI run;
        private final List<JobI> jobs;
        private final CountDownLatch done;

        public FillSeqRun(SeqRunI run, List<JobI> jobs, CountDownLatch done) {
            this.run = run;
            this.jobs = jobs;
            this.done = done;
        }

        @Override
        public void run() {
            List<JobI> tmp = new ArrayList<>();
            try {
                for (JobI j : run.getMaster().Job().BySeqRun(run)) {
                    if (j.getTool() == null) {
                        // trigger fetch of tool
                        run.getMaster().Tool().ByJob(j);
                    }
                    tmp.add(j);
                }

            } catch (MGXException ex) {
                tmp.clear();
                // silently ignore exception here, since it might
                // be cause by an intermediate refresh while a 
                // deletion of one of the objects is in progress
            }

            if (!run.isDeleted()) {
                synchronized (jobs) {
                    jobs.addAll(tmp);
                }
            }
            done.countDown();
        }

    }

}
