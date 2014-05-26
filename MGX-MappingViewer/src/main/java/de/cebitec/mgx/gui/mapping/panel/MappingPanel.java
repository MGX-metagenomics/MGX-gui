/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.mapping.tracks.Track;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.mapping.ViewController;
import de.cebitec.mgx.gui.mapping.tracks.TrackFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.ToolTipManager;

/**
 *
 * @author sj
 */
public class MappingPanel extends PanelBase {

    private final SortedSet<MappedRead2D> coverage = new TreeSet<>();

    /**
     * Creates new form MappingPanel
     */
    public MappingPanel(ViewController vc) {
        super(vc);
        initComponents();
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        setMinimumSize(new Dimension(300, 300));
    }

    @Override
    void draw(Graphics2D g2) {
        if (!coverage.isEmpty()) {
            //System.err.println("mappings number " + coverage.size());
            Color col = Color.BLACK;
            synchronized (coverage) {
                for (MappedRead2D mr2d : coverage) {
                    if (!col.equals(mr2d.getColor())) {
                        g2.setColor(mr2d.getColor());
                    }
                    g2.fill(mr2d);
                }
                g2.setColor(Color.BLACK);
                for (MappedRead2D mr2d : coverage) {
                    Rectangle r = mr2d.getBounds();
                    g2.draw(r);
                }
            }
        }
    }

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
        return null;
    }

    @Override
    void update() {

        SortedSet<MappedSequence> mappings = vc.getMappings(bounds[0], bounds[1]);
        if (mappings.isEmpty()) {
            return;
        }

        List<Track> tracks = TrackFactory.createTracks(mappings);
        for (Track t : tracks) {
            assert t.size() > 0;
        }

        int vStart = 5; // padding in px from top
        int usableHeight = getHeight() - vStart;

        double trackHeight = 3; // Math.max(usableHeight / tracks.size(), 3d); // at least 3px track height
        double spaceing = trackHeight * 0.1;

        SortedSet<MappedRead2D> ret = new TreeSet<>();

        int trackNum = 0;
        for (Track t : tracks) {
            Iterator<MappedSequence> iter = t.getSequences();
            double vOffset = vStart + (trackNum * trackHeight);
            while (iter.hasNext()) {
                MappedSequence ms = iter.next();
                double pos0 = bp2px(ms.getMin());
                double pos1 = bp2px(ms.getMax());
                if (pos1 - pos0 < 1) {
                    pos1 = pos0 + 1;
                }
                MappedRead2D rect = new MappedRead2D(ms, pos0, vOffset + spaceing, trackHeight * 0.8, pos1 - pos0 + 1);
                ret.add(rect);
            }
            trackNum++;
        }

        synchronized (coverage) {
            coverage.clear();
            coverage.addAll(ret);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setMinimumSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 946, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 386, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
