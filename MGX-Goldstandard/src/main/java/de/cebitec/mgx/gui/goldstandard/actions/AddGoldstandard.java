package de.cebitec.mgx.gui.goldstandard.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.goldstandard.MGSEntry;
import de.cebitec.mgx.goldstandard.MGSReader;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.goldstandard.wizard.AddGoldstandardWizardDescriptor;
import java.awt.Dialog;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import static javax.swing.Action.NAME;
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
    
    private final static String TOOL_NAME = "Goldstandard";
    private final static String TOOL_AUTHOR = "Patrick Blumenkamp";
    private final static String TOOL_LONG_DESCRIPTION = "fake tool for gold standard results";
    private final static String TOOL_WEBSITE = "";
    private final static String TOOL_XML = "";
    private final static float TOOL_VERSION = 1.0f;

    public AddGoldstandard() {
        this(Utilities.actionsGlobalContext());
    }

    private AddGoldstandard(Lookup context) {
        putValue(NAME, "AddGoldstandard");
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
        return "AddGoldstandard";
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

        final AddGoldstandardWizardDescriptor wd = new AddGoldstandardWizardDescriptor();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {            
            for (final SeqRunI seqrun : seqruns){
                try {
                    ToolI tool = master.Tool().create(TOOL_NAME, TOOL_LONG_DESCRIPTION, TOOL_AUTHOR, TOOL_WEBSITE, TOOL_VERSION, TOOL_XML);
                    Collection<JobParameterI> params = new ArrayList<>(1);
                    JobI job = master.Job().create(tool, seqrun, params);
                    MGSReader reader = new MGSReader(wd.getGoldstandardFile().getAbsolutePath(), master, job);
                    while (reader.hasNext()){
                        MGSEntry entry = reader.next();
                        SequenceI seq = master.Sequence().fetch(seqrun, entry.getHeader());
                        for (Triple<AttributeI, Integer, Integer> t : entry.getAttributes()){
                            master.Observation().create(seq, t.getFirst(), t.getSecond(), t.getThird());
                        }
                    }
//            ToolI tool = (ToolI) wiz.getProperty(AnalysisWizardIterator.PROP_TOOL);
//            ToolType tooltype = (ToolType) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLTYPE);
//            @SuppressWarnings(value = "unchecked")
//            List<JobParameterI> params = (List<JobParameterI>) wiz.getProperty(AnalysisWizardIterator.PROP_PARAMETERS);
//
//            for (final SeqRunI seqrun : seqruns) {
//                CountDownLatch toolIsCreated = new CountDownLatch(1);
//
//                // skip empty seqruns
//                if (seqrun.getNumSequences() == 0) {
//                    NotifyDescriptor d = new NotifyDescriptor.Message("Sequencing run has no sequences, skipping..");
//                    DialogDisplayer.getDefault().notify(d);
//                    continue;
//                }
//
//                final SubmitTask sTask = new SubmitTask(tool, tooltype, seqrun, params, toolIsCreated);
//                NonEDT.invoke(new Runnable() {
//                    @Override
//                    public void run() {
//                        TaskManager.getInstance().addTask(sTask);
//                    }
//                });
//
//                // first submission will create a tool instance within the project, so all
//                // subsequent invocations are project-based tools
//                //
//                // however, we will need to wait until the first task has finished processing
//                // before we can be sure the tool has been added to the project
//                //
//                try {
//                    toolIsCreated.await();
//                } catch (InterruptedException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//                tooltype = ToolType.PROJECT;
//            }                
                } catch (MGXException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
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
        return super.isEnabled() && RBAC.isUser() && numSeqs > 0;
    }

//    private final class SubmitTask extends MGXTask {
//
//        private final ToolI tool;
//        private final SeqRunI run;
//        private final ToolType tooltype;
//        private final Collection<JobParameterI> params;
//        private final CountDownLatch toolCreated;
//
//        public SubmitTask(ToolI tool, ToolType tooltype, SeqRunI run, Collection<JobParameterI> params, CountDownLatch toolCreated) {
//            super("Submit " + run.getName() + " / " + tool.getName());
//            this.tool = tool;
//            this.tooltype = tooltype;
//            this.params = params;
//            this.run = run;
//            this.toolCreated = toolCreated;
//        }
//
//        @Override
//        public boolean process() {
//            final MGXMasterI master = tool.getMaster();
//            try {
//                ToolI selectedTool = null;
//                switch (tooltype) {
//                    case GLOBAL:
//                        long projToolId = master.Tool().installTool(tool.getId());
//                        tool.setId(projToolId);
//                        selectedTool = master.Tool().fetch(projToolId);
//                        break;
//                    case PROJECT:
//                        selectedTool = tool;
//                        break;
//                    case USER_PROVIDED:
//                        master.Tool().create(tool);
//                        selectedTool = tool;
//                        break;
//                    default:
//                        assert false;
//                }
//                toolCreated.countDown();
//                setStatus("Creating job..");
//                JobI job = master.Job().create(selectedTool, run, params);
//                setStatus("Validating configuration..");
//                master.Job().verify(job);
//                setStatus("Submitting..");
//                return master.Job().execute(job);
//            } catch (MGXException ex) {
//                setStatus(ex.getMessage());
//                failed(ex.getMessage());
//                Exceptions.printStackTrace(ex);
//            }
//            return false;
//        }
//    }
}
