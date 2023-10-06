package de.cebitec.mgx.gui.goldstandard.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.BulkObservation;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.common.ToolScope;
import de.cebitec.mgx.gui.goldstandard.util.MGSAttribute;
import de.cebitec.mgx.gui.goldstandard.util.MGSEntry;
import de.cebitec.mgx.gui.goldstandard.util.MGSReader;
import de.cebitec.mgx.gui.goldstandard.wizards.addgoldstandard.AddGoldstandardWizardDescriptor;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.rbac.RBAC;
import java.awt.Dialog;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
@ActionRegistration(displayName = "Add reference annotation", lazy = false)
public final class AddGoldstandard extends NodeAction implements LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup context;
    private Lookup.Result<SeqRunI> lkpInfo;

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
        putValue(NAME, "Add reference annotation");
        this.context = context;
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
        setEnabled(RBAC.isUser() && !lkpInfo.allInstances().isEmpty());
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return RBAC.isUser();
    }

    @Override
    public String getName() {
        return "Add reference annotation";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        final MGXMasterI master = context.lookup(MGXMasterI.class);
        Collection<? extends SeqRunI> seqruns = lkpInfo.allInstances();
        final AtomicBoolean hasError = new AtomicBoolean(false);
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
                JobI job;
                ProgressHandle p = ProgressHandle.createHandle("Importing .mgs file");
                try {
                    ToolI tool = getToolByName(master, TOOL_NAME, TOOL_LONG_DESCRIPTION, TOOL_AUTHOR, TOOL_WEBSITE, TOOL_VERSION, TOOL_XML);
                    List<JobParameterI> params = new ArrayList<>(1);
                    job = master.Job().create(tool, params, seqrun);
                    job.setStatus(JobState.RUNNING);
                    p.start((int) seqrun.getNumSequences() + 1);
                    master.Job().update(job);

                    MGSReader reader = new MGSReader(wd.getGoldstandardFile().getAbsolutePath(), master, job);
                    int i = 0;
                    int numChunks = 0;
                    List<BulkObservation> bol = new ArrayList<>();
                    final AtomicInteger numRunnables = new AtomicInteger(0);

                    while (reader.hasNext()) {
                        if (hasError.get()) {
                            break;
                        }
                        final MGSEntry entry = reader.next();
                        String seqName = entry.getHeader();
                        if (seqName.contains(" ")) {
                            seqName = seqName.substring(0, seqName.indexOf(" "));
                        }
                        for (MGSAttribute t : entry.getAttributes()) {
                            bol.add(new BulkObservation(seqrun.getId(), seqName, t.getAttribute().getId(), t.getStart(), t.getStop()));
                        }
                        p.progress(i++);
                        numChunks++;
                        if (numChunks == CHUNKSIZE) {
                            final List<BulkObservation> submitBol = bol;
                            numRunnables.incrementAndGet();
                            MGXPool.getInstance().submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!hasError.get()) {
                                            master.Observation().createBulk(submitBol);
                                        }
                                    } catch (MGXException ex) {
                                        Exceptions.printStackTrace(ex);
                                        hasError.set(true);
                                    }
                                    numRunnables.decrementAndGet();
                                }
                            });
                            bol = new ArrayList<>();
                            numChunks = 0;
                        }
                    }
                    if (!hasError.get() && numChunks > 0) {
                        final List<BulkObservation> submitBol = bol;
                        numRunnables.incrementAndGet();
                        MGXPool.getInstance().submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!hasError.get()) {
                                        master.Observation().createBulk(submitBol);
                                    }
                                } catch (MGXException ex) {
                                    Exceptions.printStackTrace(ex);
                                    hasError.set(true);
                                }
                                numRunnables.decrementAndGet();
                            }
                        }
                        );
                    }

                    while (numRunnables.get() != 0) {
                        Thread.yield();
                        // wait
                    }

                    if (hasError.get()) {
                        master.Job().delete(job);
                    } else {
                        job.setStatus(JobState.FINISHED);
                        master.Job().update(job);
                    }
                    p.progress(i++);
                    System.err.println("job set to finished");
                } catch (MGXException | IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    p.finish();
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

    private static ToolI getToolByName(MGXMasterI master, String name, String desc, String author, String web, float version, String xml) throws MGXException {
        Iterator<ToolI> iter = master.Tool().fetchall();
        while (iter.hasNext()) {
            ToolI tool = iter.next();
            if (name.equals(tool.getName())) {
                return tool;
            }
        }
        return master.Tool().create(ToolScope.READ, name, desc, author, web, version, xml);
    }
}
