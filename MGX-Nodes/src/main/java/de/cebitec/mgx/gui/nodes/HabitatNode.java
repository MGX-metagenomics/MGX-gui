package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.nodefactory.SampleNodeFactory;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.habitat.HabitatWizardDescriptor;
import de.cebitec.mgx.gui.wizard.sample.SampleWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class HabitatNode extends MGXNodeBase<Habitat> {

    private SampleNodeFactory snf = null;

    public HabitatNode(MGXMaster m, Habitat h) {
        this(h, new SampleNodeFactory(m, h));
        master = m;
        setDisplayName(h.getName());
    }

    private HabitatNode(Habitat h, SampleNodeFactory snf) {
        super(Children.create(snf, true), Lookups.singleton(h), h);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(h));
        this.snf = snf;
    }

    private String getToolTipText(Habitat h) {
        return new StringBuilder("<html>").append("<b>Habitat: </b>").
                append(h.getName()).append("<br><hr><br>").append("biome: ").append(h.getBiome()).append("<br>").append("location: ").append(new Double(h.getLatitude()).toString()).append(" / ").append(new Double(h.getLongitude()).toString()).append("<br>").append("altitude: ").append(Integer.valueOf(h.getAltitude()).toString()).append("</html>").toString();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditHabitat(), new DeleteHabitat(), new AddSample()};
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
                String oldDisplayName = habitat.getName();
                habitat = hwd.getHabitat();
                getMaster().Habitat().update(habitat);
                fireDisplayNameChange(oldDisplayName, habitat.getName());
                setDisplayName(habitat.getName());
            }
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
                MGXTask deleteTask = new MGXTask() {

                    @Override
                    public void process() {
                        setStatus("Deleting..");
                        getMaster().Habitat().delete(habitat.getId());
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        fireNodeDestroyed();
                    }
                };

                TaskManager.getInstance().addTask("Delete " + habitat.getName(), deleteTask);

            }
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
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                    @Override
                    protected void done() {
                        snf.refreshChildren();
                        super.done();
                    }

                    @Override
                    protected Void doInBackground() throws Exception {
                        getMaster().Sample().create(s);
                        return null;
                    }
                };
                worker.execute();
            }
        }
    }
}
