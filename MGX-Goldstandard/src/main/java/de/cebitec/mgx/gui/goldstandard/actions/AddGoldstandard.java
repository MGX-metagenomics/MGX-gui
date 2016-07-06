package de.cebitec.mgx.gui.goldstandard.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.BulkObservationList;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.goldstandard.util.MGSAttribute;
import de.cebitec.mgx.gui.goldstandard.util.MGSEntry;
import de.cebitec.mgx.gui.goldstandard.util.MGSReader;
import de.cebitec.mgx.gui.goldstandard.util.WaitTimeMonitoringExecutorService;
import de.cebitec.mgx.gui.goldstandard.wizards.addgoldstandard.AddGoldstandardWizardDescriptor;
import java.awt.Dialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static javax.swing.Action.NAME;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.LookupEvent;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard")
@ActionRegistration(displayName = "Add gold standard", lazy = true)
public final class AddGoldstandard extends NodeAction implements LookupListener {

    private final Lookup context;
    private Lookup.Result<SeqRunI> lkpInfo;
    private final ExecutorService pool;

    public final static String TOOL_NAME = "Goldstandard";
    public final static String TOOL_AUTHOR = "Patrick Blumenkamp";
    public final static String TOOL_LONG_DESCRIPTION = "fake tool for gold standard results";
    public final static String TOOL_WEBSITE = "";
    public final static String TOOL_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><graph description=\"\" name=\"Goldstandard\" service=\"MGX\"><composites/><nodes><node id=\"1\" name=\"\" type=\"Conveyor.MGX.GetMGXJob\" x=\"407\" y=\"162\"><configuration_items/><typeParameters/></node><node id=\"2\" name=\"\" type=\"Conveyor.Core.Discard\" x=\"412\" y=\"310\"><configuration_items/><typeParameters/></node></nodes><links><link from_connector=\"output\" from_node=\"1\" to_connector=\"input\" to_node=\"2\"/></links></graph>";
    public final static float TOOL_VERSION = 1.0f;

    public final static int CHUNKSIZE = 1500;

    public AddGoldstandard() {
        this(Utilities.actionsGlobalContext());
    }

    private AddGoldstandard(Lookup context) {
        putValue(NAME, "AddGoldstandard");
        this.context = context;
        int threads = Math.min(20, Runtime.getRuntime().availableProcessors() + 3);
        RejectedExecutionHandler executionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
//        ThreadPoolExecutor temp = new ThreadPoolExecutor(threads, threads, 2, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100, true), executionHandler);
//        pool = new WaitTimeMonitoringExecutorService(temp);
        pool = new ThreadPoolExecutor(threads, threads, 2, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100, true), executionHandler);
        init();
    }

    private void init() {
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(SeqRunI.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return "AddGoldstandard";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        long start, stop;
        final MGXMasterI master = context.lookup(MGXMasterI.class);
        Collection<? extends SeqRunI> seqruns = lkpInfo.allInstances();
        if (seqruns.isEmpty()) {
            return;
        }

        final AddGoldstandardWizardDescriptor wd = new AddGoldstandardWizardDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            for (final SeqRunI seqrun : seqruns) {
                try {
                    start = System.currentTimeMillis();
                    ToolI tool = getTool(master, TOOL_NAME, TOOL_LONG_DESCRIPTION, TOOL_AUTHOR, TOOL_WEBSITE, TOOL_VERSION, TOOL_XML);
                    Collection<JobParameterI> params = new ArrayList<>(1);
                    JobI job = master.Job().create(tool, seqrun, params);
                    ProgressHandle p = ProgressHandle.createHandle("AddGoldstandard");
                    p.start((int) seqrun.getNumSequences());
                    MGSReader reader = new MGSReader(wd.getGoldstandardFile().getAbsolutePath(), master, job);
                    int i = 0;
                    int numChunks = 0;
                    BulkObservationList bol = new BulkObservationList();
                    ArrayList<String> list = new ArrayList<>();
                    while (reader.hasNext()) {
                        final MGSEntry entry = reader.next();
//                        SequenceI seq = master.Sequence().fetch(seqrun, entry.getHeader().split(" ")[0]);
                        String seqName = entry.getHeader();
                        seqName = seqName.substring(0, seqName.indexOf(" "));
                        for (MGSAttribute t : entry.getAttributes()) {
//                                        master.Observation().create(seq, t.getAttribute(), t.getStart(), t.getStop());                            
                            bol.addObservation(seqrun, seqName, t.getAttribute(), t.getStart(), t.getStop());
                        }
                        p.progress(i++);
                        numChunks++;
                        if (numChunks == CHUNKSIZE) {
                            final BulkObservationList submitBol = bol;
                            pool.submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
//                                        long start = System.currentTimeMillis();
                                        master.Observation().createBulk(submitBol);
//                                        long stop = System.currentTimeMillis();
//                                        System.out.println(String.format("%dms", stop - start));
                                    } catch (MGXException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                            });
                            bol = new BulkObservationList();
                            numChunks = 0;
                        }
                    }
                    if (numChunks > 0) {
                        final BulkObservationList submitBol = bol;
                        pool.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    System.out.println("Run last createBulk");
                                    master.Observation().createBulk(submitBol);
                                } catch (MGXException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                        });
                    }
//                        pool.submit(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    master.Observation().createBulk(bol);
//                                    p.progress(i++);
//                                } catch (MGXException ex) {
//                                    Exceptions.printStackTrace(ex);
//                                }
//                            }
//                        });
                    pool.shutdown();
                    pool.awaitTermination(1L, TimeUnit.HOURS);
                    p.finish();
                    job.setStatus(JobState.FINISHED);
                    master.Job().update(job);
                    stop = System.currentTimeMillis();
                    System.out.println(String.format("Runtime: %dms; ChunkSize: %d", stop-start, CHUNKSIZE));
                } catch (MGXException | IOException | InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        init();
        Collection<? extends SeqRunI> seqruns = lkpInfo.allInstances();
        long numSeqs = 0;
        for (SeqRunI sr : seqruns) {
            numSeqs += sr.getNumSequences();
        }
        // make sure we don't accidentally start analysis on datasets without sequences
        return super.isEnabled() && RBAC.isUser() && numSeqs > 0 && seqruns.size() == 1;
    }

    private ToolI getTool(MGXMasterI master, String name, String desc, String author, String web, float version, String xml) throws MGXException {
        Iterator<ToolI> tools = master.Tool().fetchall();
        while (tools.hasNext()) {
            ToolI tool = tools.next();
            if (tool.getName().equals(name)) {
                return tool;
            }
        }
        return master.Tool().create(name, desc, author, web, version, xml);
    }
}
