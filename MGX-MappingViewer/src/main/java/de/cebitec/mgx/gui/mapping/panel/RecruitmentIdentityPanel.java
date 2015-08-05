/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.mapping.ViewController;
import de.cebitec.mgx.gui.mapping.shapes.ColoredRectangle;
import de.cebitec.mgx.gui.mapping.viewer.SwitchMode;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import org.apache.commons.math3.util.FastMath;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class RecruitmentIdentityPanel extends PanelBase {

    private final static int BIN_SIZE = 10_000;
    //
    //private final static float LO_BIN_THRESHOLD = 50f;
    private final static float MID_BIN_THRESHOLD = 75f;
    private final static float HI_BIN_THRESHOLD = 95f;
    private final List<ColoredRectangle> rects = new ArrayList<>();
    private final int refLength;
    private double maxBinCov = -1;

    public RecruitmentIdentityPanel(ViewController vc, SwitchMode sm) {
        super(vc, true);
        setComponentPopupMenu(sm);
        setPreferredSize(new Dimension(5000, 35));
        setMaximumSize(new Dimension(5000, 35));
        setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
        refLength = vc.getReference().getLength();
    }

    @Override
    void draw(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(5, getHeight() - 5, getWidth() - 5, getHeight() - 5); // bottom line

        synchronized (rects) {
            for (ColoredRectangle cr : rects) {
                if (!g2.getColor().equals(cr.getColor())) {
                    g2.setColor(cr.getColor());
                }
                g2.fill(cr);
            }

            // draw borders
            g2.setColor(Color.DARK_GRAY);
            for (ColoredRectangle cr : rects) {
                g2.draw(cr);
            }
        }
    }

    @Override
    public boolean update() {
        int[] bounds = vc.getBounds();

        if (maxBinCov == -1) {
            try {
                maxBinCov = vc.getMaxCoverage();
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        int lowStart = bounds[0];
        while (lowStart > 0 && lowStart % BIN_SIZE != 0) {
            lowStart--;
        }

        List<ColoredRectangle> newRects = new ArrayList<>();

        SortedSet<MappedSequenceI> mappings = null;
        double prevEndPx = -1d;
        try {

            double availHeight = getHeight() - 10; // 5px border top/bottom
            double scale = availHeight / maxBinCov;

            for (int bpPos = lowStart; bpPos < bounds[1]; bpPos += BIN_SIZE) {
                int from = bpPos;
                int to = bpPos + BIN_SIZE - 1;
                double fromPx = prevEndPx != -1d ? prevEndPx : bp2px(from);
                double toPx = bp2px(to);

                // bin mappings by identity
                mappings = vc.getMappings(from, FastMath.min(to, refLength - 1));
                int[] binnedIdentity = new int[]{0, 0, 0};
                for (MappedSequenceI mSeq : mappings) {
                    if (mSeq.getIdentity() >= HI_BIN_THRESHOLD) {
                        binnedIdentity[2]++;
                    } else if (mSeq.getIdentity() >= MID_BIN_THRESHOLD) {
                        binnedIdentity[1]++;
                    } else {
                        binnedIdentity[0]++;
                    }
                }

                // number of sequences within an interval might exceed maxCoverage; if so,
                // update maxCov and refetch data
                if (binnedIdentity[0] + binnedIdentity[1] + binnedIdentity[2] > maxBinCov) {
                    maxBinCov = binnedIdentity[0] + binnedIdentity[1] + binnedIdentity[2];
                    return update();
                }

                final double width = toPx - fromPx + 1;

                double yPos = getHeight() - 5; // bottom - border
                double scaledHeight = binnedIdentity[0] * scale;

                yPos -= scaledHeight;
                ColoredRectangle lo = new ColoredRectangle(Color.RED, fromPx, yPos, width, scaledHeight);

                scaledHeight = binnedIdentity[1] * scale;
                yPos -= scaledHeight;
                ColoredRectangle mid = new ColoredRectangle(Color.YELLOW, fromPx, yPos, width, scaledHeight);

                scaledHeight = binnedIdentity[2] * scale;
                yPos -= scaledHeight;
                ColoredRectangle hi = new ColoredRectangle(Color.GREEN, fromPx, yPos, width, scaledHeight);

                newRects.add(lo);
                newRects.add(mid);
                newRects.add(hi);

                prevEndPx = toPx;
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            synchronized (rects) {
                rects.clear();
            }
            return true;
        }

        // pre-sort by color, since color switching is expensive
        Collections.sort(newRects);

        synchronized (rects) {
            rects.clear();
            rects.addAll(newRects);
        }
        return true;
    }

}
