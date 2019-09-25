package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodes.JobNode;
import de.cebitec.mgx.gui.pool.MGXPool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
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
        // collect all seqruns and assemblies for this master
        //
        List<SeqRunI> seqruns = new ArrayList<>();
        List<AssemblyI> assemblies = new ArrayList<>();
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

            Iterator<AssemblyI> asmIter = master.Assembly().fetchall();
            while (asmIter != null && asmIter.hasNext()) {
                if (Thread.interrupted()) {
                    getMaster().log(Level.INFO, "interrupted in NF");
                    return true;
                }
                assemblies.add(asmIter.next());
            }

            Collection<JobI> jobs2 = processAssemblies(assemblies);

            toPopulate.addAll(jobs);
            toPopulate.addAll(jobs2);
        } catch (MGXLoggedoutException ex) {
            toPopulate.clear();
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        return true;
    }

    protected final static Collection<JobI> processAssemblies(Collection<AssemblyI> assemblies) throws MGXException {
        //
        // parallel fetch of missing data
        //
        Set<JobI> jobs = new HashSet<>();
        try {
            CountDownLatch allDone = new CountDownLatch(assemblies.size());
            for (AssemblyI a : assemblies) {
                MGXPool.getInstance().submit(new FillAssembly(a, jobs, allDone));
            }
            allDone.await();
        } catch (RejectedExecutionException ree) {
            // happens when the pool is already shut down during application exit
            jobs.clear();
            return jobs;
        } catch (InterruptedException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof MGXException) {
                throw (MGXException) ex.getCause();
            }
        }
        return jobs;
    }

    protected final static Collection<JobI> processRuns(Collection<SeqRunI> seqruns) throws MGXException {
        //
        // parallel fetch of missing data
        //
        Set<JobI> jobs = new HashSet<>();
        try {
            CountDownLatch allDone = new CountDownLatch(seqruns.size());
            for (SeqRunI run : seqruns) {
                MGXPool.getInstance().submit(new FillSeqRun(run, jobs, allDone));
            }
            allDone.await();
        } catch (RejectedExecutionException ree) {
            // happens when the pool is already shut down during application exit
            jobs.clear();
            return jobs;
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

    private final static class FillAssembly implements Runnable {

        private final AssemblyI ass;
        private final Set<JobI> jobs;
        private final CountDownLatch done;

        public FillAssembly(AssemblyI a, Set<JobI> jobs, CountDownLatch done) {
            this.ass = a;
            this.jobs = jobs;
            this.done = done;
        }

        @Override
        public void run() {
            Set<JobI> tmp = new HashSet<>();
            try {
                for (JobI j : ass.getMaster().Job().ByAssembly(ass)) {
                    if (!tmp.contains(j)) {
                        if (j.getTool() == null) {
                            // trigger fetch of tool
                            ass.getMaster().Tool().ByJob(j);
                        }
                        tmp.add(j);
                    }
                }

            } catch (MGXException ex) {
                tmp.clear();
                Exceptions.printStackTrace(ex);
                // silently ignore exception here, since it might
                // be cause by an intermediate refresh while a 
                // deletion of one of the objects is in progress
            }

            if (!tmp.isEmpty() && !ass.isDeleted()) {
                synchronized (jobs) {
                    jobs.addAll(tmp);
                }
            }
            done.countDown();
        }

    }

    private final static class FillSeqRun implements Runnable {

        private final SeqRunI run;
        private final Set<JobI> jobs;
        private final CountDownLatch done;

        public FillSeqRun(SeqRunI run, Set<JobI> jobs, CountDownLatch done) {
            this.run = run;
            this.jobs = jobs;
            this.done = done;
        }

        @Override
        public void run() {
            Set<JobI> tmp = new HashSet<>();
            try {
                for (JobI j : run.getMaster().Job().BySeqRun(run)) {
                    if (!tmp.contains(j)) {
                        if (j.getTool() == null) {
                            // trigger fetch of tool
                            run.getMaster().Tool().ByJob(j);
                        }
                        tmp.add(j);
                    }
                }

            } catch (MGXException ex) {
                tmp.clear();
                Exceptions.printStackTrace(ex);
                // silently ignore exception here, since it might
                // be cause by an intermediate refresh while a 
                // deletion of one of the objects is in progress
            }

            if (!tmp.isEmpty() && !run.isDeleted()) {
                synchronized (jobs) {
                    jobs.addAll(tmp);
                }
            }
            done.countDown();
        }

    }

}
