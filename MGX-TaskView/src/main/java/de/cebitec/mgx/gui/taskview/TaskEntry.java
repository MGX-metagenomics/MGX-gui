package de.cebitec.mgx.gui.taskview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author sjaenick
 */
public class TaskEntry extends JComponent {

    private JPanel left;
    private JPanel middle;
    private JLabel main;
    private JLabel detail;
    private JProgressBar progress;

    public TaskEntry() {
        super();
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(150, 100));
        panel.setLayout(new BorderLayout());

        left = new JPanel();
        main = new JLabel();
        detail = new JLabel();
        left.setLayout(new BorderLayout());
        left.add(main, BorderLayout.NORTH);
        left.add(detail, BorderLayout.SOUTH);
        panel.add(left, BorderLayout.WEST);

        middle = new JPanel(new BorderLayout());
        progress = new JProgressBar();
        progress.setIndeterminate(true);
        middle.add(progress, BorderLayout.CENTER);
        panel.add(middle, BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    public void setMainText(String t) {
        main.setText(t);
    }

    public void setDetailText(String t) {
        detail.setText(t);
        progress.setString(t);
        this.repaint();
    }
//    public void setProgressBar(JComponent jc) {
//        right.removeAll();
//        right.add(jc, BorderLayout.CENTER);
//        JProgressBar p = new JProgressBar();
//        p.setIndeterminate(true);
//    }
//    public void build() {
//        left.removeAll();
//        left.setLayout(new BorderLayout());
//        left.add(main, BorderLayout.NORTH);
//        left.add(detail, BorderLayout.SOUTH);
//        this.invalidate();
//    }
}
