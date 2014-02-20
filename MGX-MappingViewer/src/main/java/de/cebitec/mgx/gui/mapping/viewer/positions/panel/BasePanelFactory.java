package de.cebitec.mgx.gui.mapping.viewer.positions.panel;

import de.cebitec.mgx.gui.mapping.viewer.positions.AdjustmentPanel;
import de.cebitec.mgx.gui.mapping.sequences.ReferenceHolder;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfo;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfoManager;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import de.cebitec.mgx.gui.mapping.viewer.readsviewer.ReadsViewer;
import de.cebitec.mgx.gui.mapping.viewer.ReferenceViewer;
import de.cebitec.mgx.gui.mapping.loader.ReadsLoader;
import de.cebitec.mgx.gui.mapping.loader.RegionLoader;
import de.cebitec.mgx.gui.mapping.misc.ColorProperties;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.openide.util.Exceptions;

/**
 * Factory used to initialize all different kinds of base panels.
 *
 * @author ddoppmeier
 */
public class BasePanelFactory {

    private BoundsInfoManager boundsManager;
    private ReferenceHolder refHolder;
    private MGXMaster master;

    public BasePanelFactory(Reference reference, MGXMaster master) {
        this.master = master;
        this.refHolder = new ReferenceHolder(reference, this.loadReferenceSequence(master, reference));
        this.boundsManager = new BoundsInfoManager(refHolder);
    }

    public ReferenceBasePanel getGenomeViewerBasePanel() {

        ReferenceBasePanel b = new ReferenceBasePanel(boundsManager);
//        b.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // create viewer
        ReferenceViewer genomeViewer = new ReferenceViewer(boundsManager, b, refHolder, new RegionLoader(master, refHolder.getReference()));
        b.getBoundsManager().addBoundsListener(genomeViewer);

        // add panels to basepanel
        int maxSliderValue = 500;
        b.setViewer(genomeViewer);
        b.setHorizontalAdjustmentPanel(this.createAdjustmentPanel(true, true, maxSliderValue));
        b.repaint();
        return b;
    }

    public ReadsBasePanel getReadViewerBasePanel(final Mapping mapping) throws InterruptedException, ExecutionException {

        ReadsBasePanel readsBasePanel = new ReadsBasePanel(boundsManager);

        UUID uuid = loadMapping(mapping);

        ReadsViewer readsViewer = new ReadsViewer(boundsManager, readsBasePanel, refHolder, new ReadsLoader(master, refHolder.getReference(), uuid));

        JPanel genomePanelLegend = this.getGenomeViewerLegend(readsViewer);
        readsViewer.setupLegend(new MenuLabel(genomePanelLegend, MenuLabel.TITLE_LEGEND), genomePanelLegend);

        // add panels to basepanel
        int maxSliderValue = 500;
        readsBasePanel.setViewerInScrollpane(readsViewer);
        readsBasePanel.setHorizontalAdjustmentPanel(this.createAdjustmentPanel(true, true, maxSliderValue));
        readsBasePanel.repaint();
        return readsBasePanel;
    }

    private AdjustmentPanel createAdjustmentPanel(boolean hasScrollbar, boolean hasSlider, int sliderMax) {
        // create control panel
        BoundsInfo bounds = boundsManager.getUpdatedBoundsInfo(new Dimension(10, 10));
        AdjustmentPanel control = new AdjustmentPanel(1, refHolder.getRefLength(),
                bounds.getCurrentLogPos(), bounds.getZoomValue(), sliderMax, hasScrollbar, hasSlider);
        control.addAdjustmentListener(boundsManager);
        boundsManager.addSynchronousNavigator(control);
        return control;
    }

    private UUID loadMapping(final Mapping mapping) {

        SwingWorker<UUID, Void> worker = new SwingWorker<UUID, Void>() {
            @Override
            protected UUID doInBackground() throws Exception {
                final UUID uuid = master.Mapping().openMapping(mapping.getId());
                return uuid;
            }
        };
        worker.execute();
        try {
            return worker.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private String loadReferenceSequence(final MGXMaster master, final Reference reference) {
        final SwingWorker<String, Void> referenceSequenceWorker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                int length = reference.getLength();
                return master.Reference().getSequence(reference.getId(), 0, length - 1);
            }
        };
        referenceSequenceWorker.execute();
        try {
            return referenceSequenceWorker.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private JPanel getTitlePanel(String title) {
        JPanel p = new JPanel();
        p.add(new JLabel(title));
        p.setBackground(ColorProperties.TITLE_BACKGROUND);
        return p;
    }

    /**
     * Method has to be reworked.
     *
     * @param typeColor color of the feature type
     * @param type the feature type whose legend entry is created
     * @param viewer the viewer to which the legend entry belongs. If no
     * function is assigend to the legend entry, viewer can be set to null. In
     * this case a simple label is returned instead of the checkbox.
     * @return A legend entry for a feature type.
     */
    private JPanel getLegendEntry(Color typeColor, AbstractViewer viewer) {
        JPanel entry = new JPanel(new FlowLayout(FlowLayout.LEADING));
        entry.setBackground(ColorProperties.LEGEND_BACKGROUND);

        ColorPanel color = new ColorPanel();
        color.setSize(new Dimension(10, 10));
        color.setBackground(typeColor);

        entry.add(color);
        if (viewer != null) {
            entry.add(this.getCheckBox(viewer));
        } else {
            entry.add(new JLabel(""));
        }
        entry.setAlignmentX(Component.LEFT_ALIGNMENT);
        return entry;
    }

    /**
     *
     * Method has to be reworked.
     *
     * @param type the FeatureType for which the checkbox should be created
     * @param viewer the viewer to which the checkbox belongs
     * @return a check box for the given feature type, connected to the given
     * viewer.
     */
    private JCheckBox getCheckBox(AbstractViewer viewer) {
        JCheckBox checker = new JCheckBox("");

        checker.setBackground(ColorProperties.LEGEND_BACKGROUND);
        //strangely next line is needed to ensure correct size of whole legend panel
        checker.setBorder(BorderFactory.createLineBorder(ColorProperties.LEGEND_BACKGROUND));
        return checker;
    }

    private class ColorPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, this.getSize().width - 1, this.getSize().height - 1);
        }
    }

    /**
     * Method has to be reworked.
     *
     * @param description
     * @return
     */
    private JPanel getGradientEntry(String description) {
        JPanel entry = new JPanel(new FlowLayout(FlowLayout.LEADING));
        entry.setBackground(ColorProperties.LEGEND_BACKGROUND);

        JPanel color = new JPanel() {
            private static final long serialVersionUID = 1234537;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint whiteToBlack = new GradientPaint(0, 0, Color.WHITE, this.getSize().width - 1, 0, Color.BLACK);
                g2.setPaint(whiteToBlack);
                g2.fill(new Rectangle2D.Double(0, 0, this.getSize().width, this.getSize().height));
                g2.setPaint(null);
                g2.setColor(Color.black);
                g2.drawRect(0, 0, this.getSize().width - 1, this.getSize().height - 1);
            }
        };
        color.setSize(new Dimension(10, 10));
        entry.add(color);

        entry.add(new JLabel(description));
        entry.setAlignmentX(Component.LEFT_ALIGNMENT);
        return entry;
    }

    /**
     *
     * Method has to be reworked.
     *
     * @param viewer
     * @return
     */
    private JPanel getGenomeViewerLegend(AbstractViewer viewer) {
        JPanel legend = new JPanel();
        JPanel legend1 = new JPanel();
        JPanel legend2 = new JPanel();
        legend.setLayout(new BorderLayout());
        legend1.setLayout(new BoxLayout(legend1, BoxLayout.PAGE_AXIS));
        legend2.setLayout(new BoxLayout(legend2, BoxLayout.PAGE_AXIS));
        legend.setBackground(ColorProperties.LEGEND_BACKGROUND);

        legend1.add(this.getLegendEntry(Color.BLACK, viewer));
        legend1.add(this.getLegendEntry(Color.BLACK, viewer));
        legend1.add(this.getLegendEntry(Color.BLACK, viewer));
        legend1.add(this.getLegendEntry(Color.BLACK, viewer));
        legend1.add(this.getLegendEntry(Color.BLACK, viewer));
        legend2.add(this.getLegendEntry(Color.BLACK, viewer));
        legend2.add(this.getLegendEntry(Color.BLACK, viewer));
        legend2.add(this.getLegendEntry(Color.BLACK, viewer));
        legend2.add(this.getLegendEntry(Color.BLACK, viewer));
        legend2.add(this.getLegendEntry(Color.BLACK, viewer));

        legend.add(legend1, BorderLayout.WEST);
        legend.add(legend2, BorderLayout.EAST);

        return legend;
    }
}
