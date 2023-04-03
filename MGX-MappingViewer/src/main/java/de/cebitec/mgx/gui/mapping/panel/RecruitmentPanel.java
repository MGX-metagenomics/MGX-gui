/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.mapping.impl.ViewControllerI;
import de.cebitec.mgx.gui.mapping.viewer.SwitchModeBase;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import org.apache.commons.math3.util.FastMath;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class RecruitmentPanel extends PanelBase<ViewControllerI> {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final static int MIN_MAPPING_WIDTH = 1;
    private final int[] offsets = new int[101];
    private final int topBorder = 0;
    private final int topVSpace = 6;
    private final int bottomBorder = 10;
    private final List<Rectangle2D> shapes = new ArrayList<>();

    /**
     * Creates new form
     */
    public RecruitmentPanel(final ViewControllerI vc, SwitchModeBase sm) {
        super(vc, false);
        initComponents();
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        super.setBackground(Color.WHITE);
        super.setPreferredSize(new Dimension(5000, 800));
        super.setComponentPopupMenu(sm);

        identityFilter.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int minIdentity = identityFilter.getValue();
                identityFilter.setToolTipText("Showing >= " + minIdentity + "% identity");
                vc.setMinIdentity(minIdentity);
            }
        });
        identityFilter.setToolTipText("Showing >= " + identityFilter.getValue() + "% identity");
        BasicSliderUI sliderUI = new javax.swing.plaf.basic.BasicSliderUI(identityFilter) {
            @Override
            protected Dimension getThumbSize() {
                return new Dimension(5, 10);
            }
        };
        identityFilter.setUI(sliderUI);
    }

    @Override
    public void draw(Graphics2D g2) {

        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(0, topBorder, getWidth(), topBorder); // separation to top
        int textHeight = -1 + g2.getFontMetrics(g2.getFont()).getHeight() / 2;
        g2.drawString("100", 1, topBorder + textHeight + 5);
        g2.drawLine(20, topBorder + topVSpace, getWidth() - 5, topBorder + topVSpace); // top line for 100% identity

        g2.drawString(" " + String.valueOf(vc.getMinIdentity()), 1, getHeight() - bottomBorder + textHeight);
        g2.drawLine(20, getHeight() - bottomBorder, getWidth() - 5, getHeight() - bottomBorder); // bottom line

        // Get and install an AlphaComposite to do transparent drawing
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setColor(shapeColor);

        synchronized (shapes) {
            for (Rectangle2D rect : shapes) {
                g2.fill(rect);
            }
        }
    }

    private final Color shapeColor = new Color(0.1f, 0.1f, 0.6f, 0.2f);
    private final int rectHeight = 3;

    @Override
    public boolean update() {
        List<MappedSequenceI> mappings;
        try {
            mappings = vc.getMappings();
        } catch (MGXException ex) {
            if (vc.isClosed()) {
                return true;
            } else {
                Exceptions.printStackTrace(ex);
            }
            return true;
        }
        float height = getHeight() - topBorder - bottomBorder - topVSpace;
        float heightScale = height / (100 - vc.getMinIdentity());

        // precomputed vert. offsets
        for (int i = 0; i <= 100; i++) {
            offsets[i] = topVSpace + topBorder + (int) (1d * i * heightScale) - (rectHeight / 2);
        }

        List<Rectangle2D.Float> newShapes = new ArrayList<>();

        for (MappedSequenceI ms : mappings) {
            float pos0 = bp2px(ms.getMin());
            float pos1 = bp2px(ms.getMax());
            if (pos1 - pos0 < MIN_MAPPING_WIDTH) {
                pos1 = pos0 + MIN_MAPPING_WIDTH;
            }

            pos0 = FastMath.max(pos0, 0);
            pos1 = FastMath.min(pos1, getWidth());
            int yPos = offsets[100 - (int) ms.getIdentity()]; //(int) ((100 - ms.getIdentity()) * heightScale);
            Rectangle2D.Float rect = new Rectangle2D.Float(pos0, yPos, pos1 - pos0 + 1, rectHeight);
            newShapes.add(rect);
        }
        synchronized (shapes) {
            shapes.clear();
            shapes.addAll(newShapes);
        }
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ViewControllerI.MAX_COV_CHANGE:
                return;
        }
        super.propertyChange(evt);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        identityFilter = new javax.swing.JSlider();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setMinimumSize(new java.awt.Dimension(200, 200));

        identityFilter.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        identityFilter.setValue(0);
        identityFilter.setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(identityFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(822, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(345, Short.MAX_VALUE)
                .addComponent(identityFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider identityFilter;
    // End of variables declaration//GEN-END:variables
}
