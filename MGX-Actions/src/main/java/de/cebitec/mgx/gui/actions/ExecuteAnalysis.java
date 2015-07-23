/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.ToolType;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.analysis.AnalysisWizardIterator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
@ActionRegistration(displayName = "Analyze", lazy = true)
public class ExecuteAnalysis extends NodeAction implements LookupListener {

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
        return null;
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
                projectTools.add(pTools.next());
            }
            Collections.sort(projectTools);

            Iterator<ToolI> repoTools = master.Tool().listGlobalTools();
            while (repoTools.hasNext()) {
                repositoryTools.add(repoTools.next());
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
            ToolI tool = (ToolI) wiz.getProperty(AnalysisWizardIterator.PROP_TOOL);
            ToolType tooltype = (ToolType) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLTYPE);
            @SuppressWarnings(value = "unchecked")
            List<JobParameterI> params = (List<JobParameterI>) wiz.getProperty(AnalysisWizardIterator.PROP_PARAMETERS);

            for (final SeqRunI seqrun : seqruns) {
                CountDownLatch toolIsCreated = new CountDownLatch(1);

                // skip empty seqruns
                if (seqrun.getNumSequences() == 0) {
                    NotifyDescriptor d = new NotifyDescriptor.Message("Sequencing run has no sequences, skipping..");
                    DialogDisplayer.getDefault().notify(d);
                    continue;
                }

                final SubmitTask sTask = new SubmitTask(tool, tooltype, seqrun, params, toolIsCreated);
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

    private final class SubmitTask extends MGXTask {

        private final ToolI tool;
        private final SeqRunI run;
        private final ToolType tooltype;
        private final Collection<JobParameterI> params;
        private final CountDownLatch toolCreated;

        public SubmitTask(ToolI tool, ToolType tooltype, SeqRunI run, Collection<JobParameterI> params, CountDownLatch toolCreated) {
            super("Submit " + run.getName() + " / " + tool.getName());
            this.tool = tool;
            this.tooltype = tooltype;
            this.params = params;
            this.run = run;
            this.toolCreated = toolCreated;
        }

        @Override
        public boolean process() {
            final MGXMasterI master = tool.getMaster();
            try {
                ToolI selectedTool = null;
                switch (tooltype) {
                    case GLOBAL:
                        long projToolId = master.Tool().installTool(tool.getId());
                        tool.setId(projToolId);
                        selectedTool = master.Tool().fetch(projToolId);
                        break;
                    case PROJECT:
                        selectedTool = tool;
                        break;
                    case USER_PROVIDED:
                        master.Tool().create(tool);
                        selectedTool = tool;
                        break;
                    default:
                        assert false;
                }
                toolCreated.countDown();
                setStatus("Creating job..");
                JobI job = master.Job().create(selectedTool, run, params);
                setStatus("Validating configuration..");
                master.Job().verify(job);
                setStatus("Submitting..");
                return master.Job().execute(job);
            } catch (MGXException ex) {
                setStatus(ex.getMessage());
                failed();
                Exceptions.printStackTrace(ex);
            }
            return false;
        }
    }

}
