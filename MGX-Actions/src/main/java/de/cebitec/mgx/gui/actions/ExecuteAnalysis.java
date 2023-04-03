/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.ToolType;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.common.ToolScope;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.analysis.AnalysisWizardIterator;
import java.io.Serial;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author sjaenick
 */
@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.actions.ExecuteAnalysis")
@ActionRegistration(displayName = "Analyze", lazy = false)
public class ExecuteAnalysis extends NodeAction implements LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup context;
    private Lookup.Result<SeqRunI> lkpInfo;

    public ExecuteAnalysis() {
        this(Utilities.actionsGlobalContext());
    }

    private ExecuteAnalysis(Lookup context) {
        putValue(NAME, "Analyze");
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
    public Action createContextAwareInstance(Lookup lkp) {
        return this;
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
        return "Analyze";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        final MGXMasterI master = context.lookup(MGXMasterI.class);
        Collection<? extends SeqRunI> seqruns = lkpInfo.allInstances();
        if (seqruns.isEmpty()) {
            return;
        }

        // TODO: check if invoked on EDT
        // fetch required data
        final List<MGXReferenceI> references = new ArrayList<>();
        final List<ToolI> projectTools = new ArrayList<>();
        final List<ToolI> repositoryTools = new ArrayList<>();
        try {
            Iterator<MGXReferenceI> refiter = master.Reference().fetchall();
            while (refiter.hasNext()) {
                references.add(refiter.next());
            }
            Collections.sort(references);

            Iterator<ToolI> pTools = master.Tool().fetchall();
            while (pTools.hasNext()) {
                ToolI t = pTools.next();
                if (t.getScope() == ToolScope.READ) {
                    projectTools.add(t);
                }
            }
            Collections.sort(projectTools);

            Iterator<ToolI> repoTools = master.Tool().listGlobalTools();
            while (repoTools.hasNext()) {
                ToolI t = repoTools.next();
                if (t.getScope() == ToolScope.READ) {
                    repositoryTools.add(t);
                }
            }
            Collections.sort(repositoryTools);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        AnalysisWizardIterator iter = new AnalysisWizardIterator(master, references, projectTools, repositoryTools);
        WizardDescriptor wiz = new WizardDescriptor(iter);
        iter.setWizardDescriptor(wiz);
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Tool selection");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            Long toolId = (Long) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLID);
            ToolType tooltype = (ToolType) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLTYPE);

            String toolName = (String) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLNAME);

            @SuppressWarnings(value = "unchecked")
            List<JobParameterI> params = (List<JobParameterI>) wiz.getProperty(AnalysisWizardIterator.PROP_PARAMETERS);

            CountDownLatch toolIsCreated = new CountDownLatch(1);

            // throttle parallel creation/submission of jobs
            Semaphore submissionRateLimit = new Semaphore(2);

            for (final SeqRunI seqrun : seqruns) {

                // skip empty seqruns
                if (seqrun.getNumSequences() == 0) {
                    NotifyDescriptor d = new NotifyDescriptor.Message("Sequencing run " + seqrun.getName() + " has no sequences, skipping..");
                    DialogDisplayer.getDefault().notify(d);
                    continue;
                }

                SubmitTask submitTask;
                if (tooltype == ToolType.USER_PROVIDED) {
                    String toolDesc = (String) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLDESC);
                    String toolAuthor = (String) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLAUTHOR);
                    String toolWebsite = (String) wiz.getProperty(AnalysisWizardIterator.PROP_TOOL_URL);
                    String toolXML = (String) wiz.getProperty(AnalysisWizardIterator.PROP_TOOL_DEFINITION);
                    Float toolVersion = Float.parseFloat((String) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLVERSION));
                    submitTask = new SubmitTask(toolName, toolDesc, toolAuthor, toolWebsite, toolVersion, toolXML, seqrun, params, toolIsCreated, submissionRateLimit);
                } else {
                    submitTask = new SubmitTask(toolId, toolName, tooltype, seqrun, params, toolIsCreated, submissionRateLimit);
                }
                final SubmitTask sTask = submitTask;

                NonEDT.invoke(new Runnable() {
                    @Override
                    public void run() {
                        TaskManager.getInstance().addTask(sTask);
                    }
                });

                // first submission will create a tool instance within the project, so all
                // subsequent invocations are project-based tools
                //
                // however, we will need to wait until the first task has finished processing
                // before we can be sure the tool has been added to the project
                //
                try {
                    toolIsCreated.await();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
                tooltype = ToolType.PROJECT;
                toolId = submitTask.getToolId();
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
        return super.isEnabled() && RBAC.isUser() && numSeqs > 0;
    }

    private final static class SubmitTask extends MGXTask {

        private long toolId;
        private final SeqRunI run;
        private final ToolType tooltype;
        private final List<JobParameterI> params;
        private final CountDownLatch toolCreated;
        private final Semaphore rateLimit;

        public SubmitTask(long toolId, String toolName, ToolType tooltype, SeqRunI run, List<JobParameterI> params, CountDownLatch toolCreated, Semaphore rateLimit) {
            super("Submit " + run.getName() + " / " + toolName);
            if (toolName == null) {
                throw new RuntimeException("No tool name supplied.");
            }
            if (tooltype != ToolType.GLOBAL && tooltype != ToolType.PROJECT) {
                throw new RuntimeException("Wrong ctor used");
            }
            this.toolId = toolId;
            this.tooltype = tooltype;
            this.params = params;
            this.run = run;
            this.toolCreated = toolCreated;
            this.rateLimit = rateLimit;
            //
            this.toolName = null;
            this.toolDesc = null;
            this.toolAuthor = null;
            this.toolUri = null;
            this.toolVersion = -1;
            this.toolXML = null;
            setStatus("Waiting..");
            //
        }

        private final String toolName;
        private final String toolDesc;
        private final String toolAuthor;
        private final String toolUri;
        private final String toolXML;
        private final float toolVersion;

        public SubmitTask(String toolName, String toolDesc, String toolAuthor, String toolUri, float toolVersion, String toolXML, SeqRunI run, List<JobParameterI> params, CountDownLatch toolCreated, Semaphore rateLimit) {
            super("Submit " + run.getName() + " / " + toolName);
            this.toolId = Identifiable.INVALID_IDENTIFIER;
            this.tooltype = ToolType.USER_PROVIDED;
            //
            this.toolName = toolName;
            this.toolDesc = toolDesc;
            this.toolAuthor = toolAuthor;
            this.toolUri = toolUri;
            this.toolVersion = toolVersion;
            this.toolXML = toolXML;
            //
            this.params = params;
            this.run = run;
            this.toolCreated = toolCreated;
            this.rateLimit = rateLimit;
            setStatus("Waiting..");
        }

        public final long getToolId() {
            return toolId;
        }

        @Override
        public boolean process() {
            final MGXMasterI master = run.getMaster();
            rateLimit.acquireUninterruptibly();
            try {
                ToolI selectedTool = null;
                switch (tooltype) {
                    case GLOBAL:
                        long projToolId = master.Tool().installTool(toolId);
                        selectedTool = master.Tool().fetch(projToolId);
                        break;
                    case PROJECT:
                        selectedTool = master.Tool().fetch(toolId);
                        break;
                    case USER_PROVIDED:
                        selectedTool = master.Tool().create(ToolScope.READ, toolName, toolDesc, toolAuthor, toolUri, toolVersion, toolXML);
                        break;
                    default:
                        assert false;
                }
                // update tool id to refer to the project-specific 
                // tool id _before_ releasing the latch 
                toolId = selectedTool.getId();
                toolCreated.countDown();

                setStatus("Creating job..");
                JobI job = master.Job().create(selectedTool, params, run);
                setStatus("Validating configuration..");
                boolean verified = master.Job().verify(job);
                if (!verified) {
                    failed("Job verification failed.");
                    return false;
                }
                setStatus("Submitting..");
                boolean success = master.Job().execute(job);
                return success;
            } catch (MGXException ex) {
                setStatus(ex.getMessage());
                failed(ex.getMessage());
                Exceptions.printStackTrace(ex);
            } finally {
                rateLimit.release();
            }
            return false;
        }
    }

}
