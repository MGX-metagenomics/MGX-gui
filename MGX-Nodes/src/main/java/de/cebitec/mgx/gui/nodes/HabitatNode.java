package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.DeleteHabitat;
import de.cebitec.mgx.gui.actions.EditHabitat;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.SampleNodeFactory;
import de.cebitec.mgx.gui.wizard.sample.SampleWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
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
        super(Children.create(snf, true), Lookups.fixed(h.getMaster(), h), h);
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
                final HabitatI hab = Utilities.actionsGlobalContext().lookup(HabitatI.class);
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
