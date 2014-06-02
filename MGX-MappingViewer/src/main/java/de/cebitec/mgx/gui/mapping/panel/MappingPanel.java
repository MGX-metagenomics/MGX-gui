/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.mapping.ViewController;
import de.cebitec.mgx.gui.mapping.shapes.MappedRead2D;
import de.cebitec.mgx.gui.mapping.tracks.Track;
import de.cebitec.mgx.gui.mapping.tracks.TrackFactory;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 *
 * @author sjaenick
 */
public class MappingPanel extends PanelBase implements ChangeListener, AdjustmentListener {

    private final SortedSet<MappedRead2D> coverage = new TreeSet<>();
    private final List<Track> tracks = new ArrayList<>();
    private int minIdentity = 0;
    private int scrollOffset = 0;

    /**
     * Creates new form MappingPanel
     */
    public MappingPanel(ViewController vc) {
        super(vc, false);
        initComponents();
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        identityFilter.addChangeListener(this);
        identityFilter.setToolTipText("Showing >= " + minIdentity + "% identity");
        BasicSliderUI sliderUI = new javax.swing.plaf.basic.BasicSliderUI(identityFilter) {
            @Override
            protected Dimension getThumbSize() {
                return new Dimension(5, 10);
            }
        };
        identityFilter.setUI(sliderUI);
        scrollBar.addAdjustmentListener(this);
    }

    @Override
    void draw(Graphics2D g2) {

        Color col = Color.BLACK;
        synchronized (coverage) {
            for (MappedRead2D mr2d : coverage) {
                if (!col.equals(mr2d.getColor())) {
                    g2.setColor(mr2d.getColor());
                }
                g2.fill(mr2d);
            }
            if (intervalLen < 8000) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(0.7f));
                for (MappedRead2D mr2d : coverage) {
                    g2.draw(mr2d);
                }
            }
        }
    }

    private final static int TRACKHEIGHT = 5;
    private final static int TRACK_VOFFSET = 1;
    private final static int MIN_MAPPING_WIDTH = 1;

    @Override
    public String getToolTipText(MouseEvent m) {
        Point loc = m.getPoint();
        if (!coverage.isEmpty()) {
            for (MappedRead2D a : coverage) {
                if (a.getBounds().contains(loc)) {
                    return a.getToolTipText();
                }
            }
        }
        int bpPos = px2bp(m.getX());
        int[] buf = new int[]{0};
        vc.getCoverage(bpPos, bpPos, buf);
        return "<html>Position: " + bpPos + "<br>Coverage: "
                + buf[0] + "</html>";
    }

    @Override
    synchronized boolean update() {

        SortedSet<MappedSequenceI> mappings = vc.getMappings(bounds[0], bounds[1]);
        if (mappings.isEmpty()) {
            return true;
        }

//        long duration = System.currentTimeMillis();
        TrackFactory.createTracks(minIdentity, mappings, tracks);
//        duration = System.currentTimeMillis() - duration;

        final double spaceing = TRACKHEIGHT * 0.1;
        final double mappingHeight = 1d * TRACKHEIGHT * 0.75;

        int height = getHeight();
        int maxVisibleTracks = getHeight() / TRACKHEIGHT;
        if (maxVisibleTracks < tracks.size()) {
            scrollBar.setEnabled(true);
            scrollBar.setMinimum(0);
            scrollBar.setMaximum(tracks.size() - maxVisibleTracks);
            scrollBar.setVisibleAmount(maxVisibleTracks);
        } else {
            scrollBar.setEnabled(false);
        }

        SortedSet<MappedRead2D> ret = new TreeSet<>();
        int vOffset = TRACK_VOFFSET;

        for (int i = scrollOffset; i < tracks.size(); i++) {
            Track t = tracks.get(i);
            Iterator<MappedSequenceI> iter = t.getSequences();
            vOffset += TRACKHEIGHT;

            while (iter.hasNext()) {
                MappedSequenceI ms = iter.next();
                double pos0 = bp2px(ms.getMin());
                double pos1 = bp2px(ms.getMax());
                if (pos1 - pos0 < MIN_MAPPING_WIDTH) {
                    pos1 = pos0 + MIN_MAPPING_WIDTH;
                }
                MappedRead2D rect = new MappedRead2D(ms, pos0, vOffset + spaceing, mappingHeight, pos1 - pos0 + 1);
                ret.add(rect);
            }

            if (vOffset + TRACKHEIGHT > height) {
                break;
            }
        }

        synchronized (coverage) {
            coverage.clear();
            coverage.addAll(ret);
        }
        return true;
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
        scrollBar = new javax.swing.JScrollBar();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setMinimumSize(new java.awt.Dimension(200, 200));

        identityFilter.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        identityFilter.setValue(0);
        identityFilter.setOpaque(false);

        scrollBar.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(identityFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 805, Short.MAX_VALUE)
                .addComponent(scrollBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(345, Short.MAX_VALUE)
                .addComponent(identityFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider identityFilter;
    private javax.swing.JScrollBar scrollBar;
    // End of variables declaration//GEN-END:variables

    @Override
    public void stateChanged(ChangeEvent e) {
        minIdentity = identityFilter.getValue();
        identityFilter.setToolTipText("Showing >= " + minIdentity + "% identity");
        update();
        repaint();
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (scrollBar.isEnabled()) {
            scrollOffset = scrollBar.getValue();
            update();
            repaint();
        }
    }
}
