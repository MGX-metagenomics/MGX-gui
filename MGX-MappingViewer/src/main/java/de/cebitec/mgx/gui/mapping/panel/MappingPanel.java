/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXTimeoutException;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.mapping.impl.ViewControllerI;
import static de.cebitec.mgx.gui.mapping.panel.PanelBase.antiAlias;
import de.cebitec.mgx.gui.mapping.shapes.MappedRead2D;
import de.cebitec.mgx.gui.mapping.tracks.TrackFactory;
import de.cebitec.mgx.gui.mapping.tracks.TrackI;
import de.cebitec.mgx.gui.mapping.viewer.SwitchModeBase;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class MappingPanel extends PanelBase<ViewControllerI> {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final SortedSet<MappedRead2D> coverage = new TreeSet<>();
    private final List<TrackI> tracks = new ArrayList<>();
    private int scrollOffset = 0;
    private final String mappingName;

    /**
     * Creates new form MappingPanel
     */
    public MappingPanel(final ViewControllerI vc, SwitchModeBase sm) {
        super(vc, false);
        initComponents();
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        identityFilter.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int minIdentity = identityFilter.getValue();
                identityFilter.setToolTipText("Showing >= " + minIdentity + "% identity");
                vc.setMinIdentity(minIdentity);
            }
        });
        identityFilter.setToolTipText("Showing >= " + vc.getMinIdentity() + "% identity");
        BasicSliderUI sliderUI = new javax.swing.plaf.basic.BasicSliderUI(identityFilter) {
            @Override
            protected Dimension getThumbSize() {
                return new Dimension(5, 10);
            }
        };
        identityFilter.setUI(sliderUI);
        scrollBar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (scrollBar.isEnabled()) {
                    scrollOffset = scrollBar.getValue();
                    update();
                    repaint();
                } else {
                    scrollOffset = 0;
                }
            }
        });

        String name = null;
        try {
            String[] elems = new String[vc.getSeqRuns().length];
            for (int i = 0; i < elems.length; i++) {
                elems[i] = vc.getSeqRuns()[i].getName();
            }

            name = String.join(", ", elems) + " vs. " + vc.getReferenceName();
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        mappingName = name;

        super.setComponentPopupMenu(sm);
    }

    @Override
    public void draw(Graphics2D g2) {
        // clear image
//        g2.setColor(getBackground());
//        g2.fillRect(0, 0, getWidth(), getHeight());


        Color currentColor = Color.BLACK;
        synchronized (coverage) {
            for (MappedRead2D mr2d : coverage) {
                Color newColor = mr2d.getColor();
                if (!currentColor.equals(newColor)) {
                    g2.setColor(newColor);
                    currentColor = newColor;
                }
                g2.fill(mr2d);
            }
            if (vc.getIntervalLength() < 8000) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(0.7f));
                for (MappedRead2D mr2d : coverage) {
                    g2.draw(mr2d);
                }
            }
        }
    }

    @Override
    public void print(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, getWidth(), getHeight());
        super.paintBorder(g);

        if (getHeight() > 0) {
            if (useAntialiasing) {
                g2.setRenderingHints(antiAlias);
            }
            if (defaultFont == null) {
                defaultFont = new Font(g2.getFont().getFontName(), Font.PLAIN, 10);
            }
            g2.setFont(defaultFont);

            draw(g2);

        }

        g2.dispose();
        //super.paintComponent(g);
        //super.paintChildren(g);

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
        try {
            vc.getCoverage(bpPos, bpPos, buf);
        } catch (MGXException ex) {
            buf = null;
        }

        NumberFormat nf = NumberFormat.getInstance(Locale.US);

        String tmp = buf != null ? String.valueOf(buf[0]) : "unknown";
        try {
            long genomicCoverage = vc.getGenomicCoverage();
            double refCoveragePct = 100d * genomicCoverage / vc.getReferenceLength();

            return "<html>" + mappingName + "<br>Mapped with: " + vc.getTool().getName() + "<br>Position: " + nf.format(bpPos) + "<br>Coverage: "
                    + nf.format(Long.valueOf(tmp)) + "<br>Reference coverage: " + nf.format(vc.getGenomicCoverage()) + " bp ("
                    + String.format("%.2f", refCoveragePct)
                    + "%)</html>";
        } catch (MGXException ex) {
            return null;
        }
    }

    @Override
    public boolean update() {

        List<MappedSequenceI> sortedMappings = null;
        try {
            sortedMappings = vc.getMappings();
        } catch (MGXTimeoutException mte) {
            if (vc.isClosed()) {
                return false;
            } else {
                // FIXME - reopen?
            }
        } catch (MGXException ex) {
            if (vc.isClosed()) {
                coverage.clear();
                tracks.clear();
                return true;
            }
            Exceptions.printStackTrace(ex);
            return true;
        }

        boolean allPlaced = TrackFactory.createTracks(sortedMappings, tracks);

        final float spaceing = TRACKHEIGHT * 0.1f;
        final float mappingHeight = 0.75f * TRACKHEIGHT;

        int height = getHeight();
        int requiredHeight = TRACKHEIGHT * tracks.size();
        int maxVisibleTracks = height / TRACKHEIGHT;

        //System.err.println(tracks.size() + " tracks");

        if (requiredHeight > height) {
            scrollBar.setEnabled(true);
            scrollBar.setMinimum(0);
            scrollBar.setValue(scrollOffset);
            scrollBar.setMaximum(tracks.size() + 5);
            scrollBar.setVisibleAmount(maxVisibleTracks);
        } else {
            //System.err.println(tracks.size() + " tracks, no scrollbar needed");
            scrollBar.setEnabled(false);
        }

        SortedSet<MappedRead2D> ret = new TreeSet<>();
        int vOffset = TRACK_VOFFSET;

        for (int i = scrollOffset; i < tracks.size(); i++) {
            TrackI t = tracks.get(i);
            Iterator<MappedSequenceI> iter = t.getSequences();
            vOffset += TRACKHEIGHT;

            while (iter.hasNext()) {
                MappedSequenceI ms = iter.next();
                float pos0 = bp2px(ms.getMin());
                float pos1 = bp2px(ms.getMax());
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

}
