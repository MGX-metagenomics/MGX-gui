package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.SampleNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.habitat.HabitatWizardDescriptor;
import de.cebitec.mgx.gui.wizard.sample.SampleWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class HabitatNode extends MGXNodeBase<HabitatI> {

    private SampleNodeFactory snf = null;

    public HabitatNode(HabitatI h) {
        this(h, new SampleNodeFactory(h));
    }

    private HabitatNode(HabitatI h, SampleNodeFactory snf) {
        super(h.getMaster(), Children.create(snf, true), Lookups.fixed(h.getMaster(), h), h);
        setDisplayName(h.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(h));
        this.snf = snf;
    }

    private String getToolTipText(HabitatI h) {
        return new StringBuilder("<html>").append("<b>Habitat: </b>")
                .append(h.getName())
                .append("<br><hr><br>")
                .append("biome: ").append(h.getBiome()).append("<br>")
                .append("location: ").append(new Double(h.getLatitude()).toString())
                .append(" / ").append(new Double(h.getLongitude()).toString())
                .append("<br>")
                .append("altitude: ").append(Integer.valueOf(h.getAltitude()).toString())
                .append("</html>").toString();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditHabitat(), new DeleteHabitat(), new AddSample()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(getContent()));
    }

    private class EditHabitat extends AbstractAction {

        public EditHabitat() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            HabitatI habitat = getLookup().lookup(HabitatI.class);
            final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
            HabitatWizardDescriptor hwd = new HabitatWizardDescriptor(habitat);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(hwd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = hwd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                final HabitatI hab = hwd.getHabitat(habitat.getMaster());

                SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        m.Habitat().update(hab);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        super.done();
                    }
                };
                sw.execute();
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }

    private class DeleteHabitat extends AbstractAction {

        public DeleteHabitat() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final HabitatI habitat = getLookup().lookup(HabitatI.class);
            final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete habitat " + habitat.getName() + "?",
                    "Delete habitat",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                final MGXTask deleteTask = new MGXTask("Delete " + habitat.getName()) {
                    @Override
                    public boolean process() {
                        try {
                            setStatus("Deleting..");
                            TaskI<HabitatI> task = m.Habitat().delete(habitat);
                            while (task != null && !task.done()) {
                                setStatus(task.getStatusMessage());
                                m.<HabitatI>Task().refresh(task);
                                sleep();
                            }
                            if (task != null) {
                                task.finish();
                            }
                            return task != null && task.getState() == TaskI.State.FINISHED;
                        } catch (MGXException ex) {
                            setStatus(ex.getMessage());
                            failed();
                            return false;
                        }
                    }
                };

                NonEDT.invoke(new Runnable() {
                    @Override
                    public void run() {
                        TaskManager.getInstance().addTask(deleteTask);
                    }
                });

            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }

    private class AddSample extends AbstractAction {

        public AddSample() {
            putValue(NAME, "Add sample");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
            final SampleWizardDescriptor wd = new SampleWizardDescriptor(m);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                final HabitatI hab = getLookup().lookup(HabitatI.class);
                SwingWorker<SampleI, Void> worker = new SwingWorker<SampleI, Void>() {
                    @Override
                    protected SampleI doInBackground() throws Exception {
                        return m.Sample().create(hab, wd.getCollectionDate(), wd.getSampleMaterial(), wd.getTemperature(), wd.getVolume(), wd.getVolumeUnit());
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        snf.refreshChildren();
                        super.done();
                    }
                };
                worker.execute();
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }
}
