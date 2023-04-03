package de.cebitec.mgx.gui.charts.basic.util;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.Serial;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 *
 * @author sjaenick
 */
public class ScrollableBarChart extends JPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    public ScrollableBarChart(SVGChartPanel cPanel, final SlidingCategoryDataset data) {
        super();
        setLayout(new BorderLayout());
        this.add(cPanel, BorderLayout.CENTER);

        int max = data.getTotalColumnCount() - data.getColumnCount();
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, max);
        this.add(scrollBar, BorderLayout.SOUTH);

        scrollBar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                data.setOffset(e.getValue());
            }
        });
    }
}
