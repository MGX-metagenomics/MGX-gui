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
import de.cebitec.mgx.gui.mapping.viewer.SwitchModeBase;
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

    private int BIN_SIZE = 10_000;
    //
    private final static float MID_BIN_THRESHOLD = 75f;
    private final static float HI_BIN_THRESHOLD = 97f;
    private final List<ColoredRectangle> rects = new ArrayList<>();
    private final int refLength;
    private long maxBinCov = -1;
    private final int topBorderPx = 3;
    private final int bottomBorderPx = 5;

    public RecruitmentIdentityPanel(ViewController vc, SwitchModeBase sm) {
        super(vc, true);
        setComponentPopupMenu(sm);
        setPreferredSize(new Dimension(5000, 50));
        setMaximumSize(new Dimension(5000, 50));
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
        double availHeight = getHeight() - topBorderPx - bottomBorderPx; // 5px border top/bottom
        if (availHeight <= 0) {
            synchronized (rects) {
                rects.clear();
            }
            return true;
        }

        int[] bounds = vc.getBounds();

        // adaptive bin size
        BIN_SIZE = 10_000;
        int intervalLength = vc.getIntervalLength();
        while ((intervalLength / BIN_SIZE) < 85) {
            BIN_SIZE = (int) (0.9f * BIN_SIZE);
            if (BIN_SIZE == 0) {
                BIN_SIZE = 1;
                break;
            }
        }

        if (maxBinCov == -1) {
            try {
                maxBinCov = vc.getMaxCoverage();
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        // align lower bound to bin boundary
        int lowStart = bounds[0];
        while (lowStart > 0 && lowStart % BIN_SIZE != 0) {
            lowStart--;
        }

        List<ColoredRectangle> newRects = new ArrayList<>();

        SortedSet<MappedSequenceI> mappings = null;
        try {

            double scale = availHeight / FastMath.log(maxBinCov);
            int[] binnedIdentity = new int[]{0, 0, 0};

            double prevEndPosPx = bp2px(lowStart);

            for (int bpPos = lowStart; bpPos < bounds[1]; bpPos += BIN_SIZE) {
                int from = bpPos;
                int to = bpPos + BIN_SIZE - 1;
                double fromPx = prevEndPosPx;
                double toPx = bp2px(to);
                prevEndPosPx = toPx;

                // bin mappings by identity
                binnedIdentity[0] = 0;
                binnedIdentity[1] = 0;
                binnedIdentity[2] = 0;
                mappings = vc.getMappings(from, FastMath.min(to, refLength - 1));
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
                final long curBinCov = binnedIdentity[0] + binnedIdentity[1] + binnedIdentity[2];
                if (curBinCov > maxBinCov) {
                    maxBinCov = curBinCov;
                    return update();
                }
                
                final double width = toPx - fromPx; // + 1;

                if (curBinCov > 0) {
                    final double curBinHeight = scale * FastMath.log(curBinCov);
                    final double curScale = curBinHeight / curBinCov;

                    double yPos = availHeight + topBorderPx;

                    if (binnedIdentity[0] != 0) {
                        double scaledHeight = curScale * binnedIdentity[0];
                        yPos -= scaledHeight;
                        ColoredRectangle lo = new ColoredRectangle(Color.RED, fromPx, yPos, width, scaledHeight);
                        newRects.add(lo);
                    }

                    if (binnedIdentity[1] != 0) {
                        double scaledHeight = curScale * binnedIdentity[1];
                        yPos -= scaledHeight;
                        ColoredRectangle mid = new ColoredRectangle(Color.YELLOW, fromPx, yPos, width, scaledHeight);
                        newRects.add(mid);
                    }

                    if (binnedIdentity[2] != 0) {
                        double scaledHeight = curScale * binnedIdentity[2];
                        yPos -= scaledHeight;
                        ColoredRectangle hi = new ColoredRectangle(Color.GREEN, fromPx, yPos, width, scaledHeight);
                        newRects.add(hi);
                    }
                }
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
