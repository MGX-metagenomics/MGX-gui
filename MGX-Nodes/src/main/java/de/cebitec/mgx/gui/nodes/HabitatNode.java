package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.nodefactory.SampleNodeFactory;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.util.NonEDT;
import de.cebitec.mgx.gui.wizard.habitat.HabitatWizardDescriptor;
import de.cebitec.mgx.gui.wizard.sample.SampleWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.UUID;
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
public class HabitatNode extends MGXNodeBase<Habitat> {

    private SampleNodeFactory snf = null;

    public HabitatNode(MGXMaster m, Habitat h) {
        this(h, m, new SampleNodeFactory(m, h));
    }

    private HabitatNode(Habitat h, MGXMaster m, SampleNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(m, h), h);
        master = m;
        setDisplayName(h.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(h));
        this.snf = snf;
    }

    private String getToolTipText(Habitat h) {
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
            Habitat habitat = getLookup().lookup(Habitat.class);
            HabitatWizardDescriptor hwd = new HabitatWizardDescriptor(habitat);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(hwd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = hwd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                final Habitat hab = hwd.getHabitat();

                SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
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
            final Habitat habitat = getLookup().lookup(Habitat.class);
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
                        setStatus("Deleting..");
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        UUID delTask = m.Habitat().delete(habitat);
                        Task task = m.Task().get(delTask);
                        while (!task.done()) {
                            setStatus(task.getStatusMessage());
                            task = m.Task().get(delTask);
                            sleep();
                        }
                        return task.getState() == Task.State.FINISHED;
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
            SampleWizardDescriptor wd = new SampleWizardDescriptor();
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                Habitat hab = getLookup().lookup(Habitat.class);
                final Sample s = wd.getSample();
                s.setHabitatId(hab.getId());
                SwingWorker<Long, Void> worker = new SwingWorker<Long, Void>() {
                    @Override
                    protected Long doInBackground() throws Exception {
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        return m.Sample().create(s);
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
