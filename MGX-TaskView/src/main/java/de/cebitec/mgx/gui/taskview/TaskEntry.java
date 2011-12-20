package de.cebitec.mgx.gui.taskview;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author sjaenick
 */
public class TaskEntry extends JComponent {

    private JPanel left;
    private JPanel right;
    private JLabel main;
    private JLabel detail;

    public TaskEntry() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        left = new JPanel(new BorderLayout());
        panel.add(left, BorderLayout.WEST);

        right = new JPanel(new BorderLayout());
        panel.add(right, BorderLayout.EAST);
    }

    public void setMainText(JLabel t) {
        main = t;
    }

    public void setDetailText(JLabel t) {
        detail = t;
    }

    public void setProgressBar(JComponent jc) {
        right.removeAll();
        right.add(jc, BorderLayout.CENTER);
    }
    
    public void build() {
        left.removeAll();
        left.add(main, BorderLayout.NORTH);
        left.add(detail, BorderLayout.SOUTH);
    }
}
