package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.JobState;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.configurations.action.MenuAction;
import de.cebitec.mgx.gui.wizard.configurations.data.util.Transform;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
import java.awt.Dialog;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SeqRunNode extends MGXNodeBase<SeqRun> implements Transferable {

    private Action[] actions = new Action[]{new ExecuteAnalysis(), new EditSeqRun(), new DeleteSeqRun()};
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(SeqRunNode.class, "SeqRunNode");

    public SeqRunNode(MGXMaster m, SeqRun s) {
        this(s);
        master = m;
        setDisplayName(s.getSequencingMethod() + " run");
    }

    private SeqRunNode(SeqRun s) {
        super(Children.LEAF, Lookups.singleton(s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/SeqRun.png");
        setShortDescription(getToolTipText(s));
    }

    @Override
    public Transferable drag() throws IOException {
        return this;
    }

    private String getToolTipText(SeqRun run) {
        return new StringBuilder("<html><b>").append(run.getSequencingTechnology()).append(" sequencing run </b>(").append(run.getNumSequences()).append(" reads)").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == DATA_FLAVOR;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor == DATA_FLAVOR) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
  private final static Logger LOGGER =
            Logger.getLogger(SeqRunNode.class.getName());
    private class ExecuteAnalysis extends AbstractAction {

        public ExecuteAnalysis() {
            putValue(NAME, "Analyze");
        }

        @Override
        public void actionPerformed(final ActionEvent e) {

            



            SwingWorker worker = new SwingWorker() {
                private MenuAction action;
                private long jobid;
                private Job job;
                private Tool tool;
                private SeqRun seqrun;
                private List<JobParameter>  toolList;


                @Override
                protected Object doInBackground() throws Exception {
                  action = new MenuAction();
                   tool = null;
                    seqrun = getLookup().lookup(SeqRun.class);
                    
                    try {

                     

                        List<Tool> tools = new ArrayList<Tool>();

//                Tool testTool = new Tool();
//                testTool.setAuthor("author");
//                testTool.setDescription("Bla Bla bla");
//                testTool.setName("yeaaah");
//                float f = 11;
//                testTool.setVersion(f);

//                tools.add(testTool);

                        //erzeugung der moeglichen tools
                        action.setTools( master.Tool().listGlobalTools(), master.Tool().fetchall());
                        //anzeige der moeglichen tools
                        action.actionPerformed(e);
                                LOGGER.info("tools set");
                        //rueckgabe welches tool gewaehlt wurde
                        tool = action.startWizardTools();
                    } catch (MGXServerException ex) {
                        Exceptions.printStackTrace(ex);


                    }
                    
                    
                    
                   LOGGER.info(tool.getId()+"");
            job = new Job();
     
            job.setTool(tool);
            job.setSeqrun(seqrun);      
            job.setCreator(master.getLogin());
            job.setStatus(JobState.PENDING);
 LOGGER.info("Before create");
 
 
             jobid = master.Job().create(job);
          
             LOGGER.info("After create "+jobid);
 
                      toolList = null;
              //parsen
            try {
                List<JobParameter> list = new ArrayList<JobParameter>();
                      
                Iterator iterator = master.Job().getParameters(jobid).iterator();
                                LOGGER.info("After Get Parameters");

                while (iterator.hasNext()) {
                    list.add((JobParameter) iterator.next());
                }

                action.setStore(Transform.getFromJobParameterNodeStore(list));
                //eigentlicher wizard start
                action.actionPerformed(e);
                toolList = new ArrayList<JobParameter>();

                toolList = Transform.getFromNodeStoreJobParameter(action.startWizardConfigurations());

            } catch (MGXServerException ex) {
                Exceptions.printStackTrace(ex);
            }
            
            return null;
                }
                
                
                @Override
                protected void done() {
                    super.done();
                    
             //                 .setId(j.getId())
//                .setSeqrunId(j.getSeqrun().getId())
//                .setToolId(j.getTool().getId())
//                .setCreator(j.getCreator())
//                .setState(dto.JobDTO.JobState.valueOf(j.getStatus().getValue()));

             


            String parameter = "";
            // muster: nodeid.configname "wert"
            for (JobParameter jobParameter : toolList) {
                String answer = jobParameter.getConfigItemValue().replaceAll("\n", " ");
                answer = answer.replaceAll("\r\n", " ");

                parameter = parameter + jobParameter.getNodeId() + "."
                        + jobParameter.getConfigItemName() + "\""
                        + answer + "\"";

            }
            //parameter liste an job weitergeben  
            job.setParameters(parameter);

            boolean job_is_ok = false;
            try {
                job_is_ok = master.Job().verify(jobid);
                if (job_is_ok) {
                    master.Job().execute(jobid);
                }
            } catch (MGXServerException ex) {
                Exceptions.printStackTrace(ex);
            }       
                    
                    
                }
                
                
            };



worker.execute();


           

        }
    }

    private class EditSeqRun extends AbstractAction {

        public EditSeqRun() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SeqRun seqrun = getLookup().lookup(SeqRun.class);
            SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor(seqrun);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                String oldDisplayName = seqrun.getSequencingMethod() + " run";
                seqrun = wd.getSeqRun();
                getMaster().SeqRun().update(seqrun);
                fireDisplayNameChange(oldDisplayName, seqrun.getSequencingMethod() + " run");
                setDisplayName(seqrun.getSequencingMethod() + " run");
            }
        }
    }

    private class DeleteSeqRun extends AbstractAction {

        public DeleteSeqRun() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final SeqRun sr = getLookup().lookup(SeqRun.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete sequencing run " + sr.getSequencingMethod() + "?",
                    "Delete sequencing run",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                // update display name to indicate this object is currently being modified
//                String oldDisplayName = sr.getSequencingMethod() + " run";
//                String newName = new StringBuilder("<html><s>")
//                        .append(sr.getSequencingMethod())
//                        .append(" run</s></html>")
//                        .toString();
//                fireDisplayNameChange(oldDisplayName, newName);
                // FIXME disable actions?

                MGXTask deleteTask = new MGXTask() {

                    @Override
                    public void process() {
                        setStatus("Deleting..");
                        getMaster().SeqRun().delete(sr.getId());
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        fireNodeDestroyed();
                    }
                };

                TaskManager.getInstance().addTask("Delete " + sr.getSequencingMethod(), deleteTask);
            }
        }
    }
}
