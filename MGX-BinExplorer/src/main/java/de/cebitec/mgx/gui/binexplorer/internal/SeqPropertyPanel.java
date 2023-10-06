/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.internal;

import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.gui.binexplorer.util.GC;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.io.Serial;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ToolTipManager;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class SeqPropertyPanel extends PanelBase<ContigViewController> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates new form FeaturePanel
     */
    public SeqPropertyPanel(ContigViewController vc) {
        super(vc, true);
        super.setMinimumSize(new Dimension(500, 175));
        super.setPreferredSize(new Dimension(500, 175));
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
    }

    public void clear() {
        super.repaint();
    }

    @Override
    public void draw(Graphics2D g2) {
//        // clear image
//        g2.setColor(getBackground());
//        g2.clearRect(0, 0, getWidth(), getHeight());

        int midY = getHeight() / 2;

        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(0, midY, getWidth(), midY); // midline

        /*
         * add tick marks with sequence positions
         */
        int separate = 500;
        while (vc.getIntervalLength() / separate > 10) {
            separate += 500;
        }

        int firstpos = bounds[0];
        while (firstpos % separate != 0) {
            firstpos++;
        }
        for (int i = firstpos; i < bounds[1]; i += separate) {
            float pos = bp2px(i);
            g2.drawLine((int) pos, midY - 3, (int) pos, midY + 3);
            String text1 = String.valueOf(i);
            g2.drawString(text1, (int) pos - textWidth(g2, text1) / 2, midY + 13);
        }

        int winSize = FastMath.max(vc.getIntervalLength() / 300, 10);
        int winShift = FastMath.max(winSize / 6, 10);

        GeneralPath gcContent = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        gcContent.moveTo(0, midY);
        float minGC = 100;
        float maxGC = 0;

        GeneralPath gcSkew = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        gcSkew.moveTo(0, midY);
        float minSkew = 1000;
        float maxSkew = -1000;
        // TODO: convert to TFloatArrayList ?
        List<Float> gcData = new LinkedList<>();
        List<Float> skewData = new LinkedList<>();

        ContigI contig = vc.getContig();
        if (contig == null) {
            return;
        }
        String dnaSequence = vc.getSequence();
        
        if (dnaSequence == null) {
            // e.g. because the assembly is currently being deleted
            return;
        }

        int seqLen = dnaSequence.length();
        for (int i = bounds[0]; i < bounds[1]; i += winShift) {
            String subseq = dnaSequence.substring(i, FastMath.min(i + winSize, seqLen - 1));
            float gc = GC.gc(subseq);
            float skew = GC.gcSkew(subseq);
            gcData.add(gc);
            skewData.add(skew);

            minGC = FastMath.min(minGC, gc);
            maxGC = FastMath.max(maxGC, gc);
            minSkew = FastMath.min(minSkew, skew);
            maxSkew = FastMath.max(maxSkew, skew);
        }

        int vertPadding = 5;
        float gcRange = maxGC - minGC;
        float gcScale = (getHeight() - vertPadding - vertPadding) / gcRange;

        float skewRange = maxSkew - minSkew;
        float skewScale = (getHeight() - vertPadding - vertPadding) / skewRange;

        Iterator<Float> gcIter = gcData.iterator();
        Iterator<Float> skewIter = skewData.iterator();

        for (int i = bounds[0]; i < bounds[1]; i += winShift) {
            float posX = bp2px(i + winSize / 2); // center of sliding window

            float gc = gcIter.hasNext() ? gcIter.next() : 0;
            float gcPosY = (gc - minGC) * gcScale;
            gcContent.lineTo(posX, getHeight() - vertPadding - gcPosY);

            float cumSkew = skewIter.hasNext() ? skewIter.next() : 0;
            float skewPosY = (cumSkew - minSkew) * skewScale;
            gcSkew.lineTo(posX, getHeight() - vertPadding - skewPosY);
        }

        g2.setColor(new Color(196, 31, 20)); // red, but a little darker
        g2.draw(gcContent);

        g2.setColor(new Color(61, 13, 184)); // dark blue
        g2.draw(gcSkew);
    }

    @Override
    public String getToolTipText(MouseEvent m) {
        return "<html>Red: GC content<br>Blue: GC skew</html>";
    }

    @Override
    public boolean update() {
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ContigViewController.BIN_SELECTED:
                // ignore
                break;
            default:
                super.propertyChange(evt);
        }
    }
}
