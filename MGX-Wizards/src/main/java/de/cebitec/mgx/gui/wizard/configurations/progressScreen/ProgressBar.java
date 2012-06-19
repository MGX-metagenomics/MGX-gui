package de.cebitec.mgx.gui.wizard.configurations.progressScreen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;

/**
 *
 * @author belmann
 */
public class ProgressBar extends JFrame {

    private String message;
    private JPanel panel;
    private JLabel label;
    private int width = 0;
    private int height = 0;
    private JPanel progressButtonPanel;
    private ActionListener listener = null;
    boolean disposeOnClose;

    public ProgressBar(String lMessage, int lWidth, int lHeight, boolean disposeOnExit) {
        disposeOnClose = disposeOnExit;
        progressButtonPanel = new JPanel(new CardLayout());
        initializePanel(lWidth, lHeight, lMessage);
    }

    public ProgressBar(String lMessage, int lWidth, int lHeight, ActionListener lListener, WindowListener lWlistener, boolean disposeOnExit) {
        disposeOnClose = disposeOnExit;
        listener = lListener;
        this.addWindowListener(lWlistener);
        progressButtonPanel = new JPanel(new CardLayout());
        initializePanel(lWidth, lHeight, lMessage);
    }

    private void initializePanel(int lWidth, int lHeight, String lMessage) throws SecurityException {
        width = lWidth;
        height = lHeight;
        this.setAlwaysOnTop(true);
        message = lMessage;
        setSize(lWidth, lHeight);
        setLayout(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        con.gridy = 0;
        con.weighty = 20;
        con.weightx = 20;
        con.anchor = GridBagConstraints.LINE_START;
        con.fill = GridBagConstraints.BOTH;

        if (disposeOnClose) {
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        } else {

            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        setLocationRelativeTo(null);
        panel = new JPanel();
        add(panel, con);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridy = 0;
        c.weighty = 20;
        c.weightx = 20;
        label = new JLabel("<html><table><td width=" + width + "><font align=center>" + message
                + " Waiting for Server... " + "</font></td></table></html>");
        label.setHorizontalTextPosition(SwingConstants.CENTER);
//	label.setHorizontalAlignment(SwingConstants.CENTER);
//	label = new JLabel(message+ "\nWaiting for Server... ");
//	label.setBorder(BorderFactory.createLineBorder(Color.yellow));
        panel.add(label, c);
//	panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        c.gridy = 1;
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);

        JPanel borderProgressPanel = new JPanel();
//	borderProgressPanel.setLayout(new BorderLayout());
        borderProgressPanel.add(bar, BorderLayout.CENTER);
        progressButtonPanel.add(borderProgressPanel, "progress");

        JPanel buttonBorderPanel = new JPanel();
        JButton b2 = new JButton("OK");
        b2.addActionListener(listener);
        b2.setVerticalTextPosition(AbstractButton.CENTER);
        b2.setHorizontalTextPosition(AbstractButton.CENTER);
//	buttonBorderPanel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//	JPanel panel2 = new JPanel(new FlowLayout());
//	panel2.add(b2);
        buttonBorderPanel.add(b2);
//	button.setMaximumSize(new Dimension(20,30));
//	button.setSize(new Dimension(20,30));
//	button.setMinimumSize(new Dimension(20,30));
//	button.setPreferredSize(new Dimension(20,30));
//	buttonBorderPanel.add(button,BorderLayout.CENTER);
        progressButtonPanel.add(buttonBorderPanel, "button");
        CardLayout cl = (CardLayout) (progressButtonPanel.getLayout());
        cl.show(progressButtonPanel, "progress");

        c.fill = GridBagConstraints.NONE;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(progressButtonPanel, c);
        setVisible(true);
    }

    public void setUpdateText(String text) {
        this.toFront();
        this.requestFocus();
        label.setText("<html><table><td width=" + width + "><font align=center>"
                + text + " Waiting for Server... " + "</font></td></table></html>");
    }

    public void setButton(String text) {
        this.toFront();
        this.requestFocus();
        label.setText("<html><table><td width=" + width + "><font align=center>"
                + text + "</font></td></table></html>");
        CardLayout cl = (CardLayout) (progressButtonPanel.getLayout());
        cl.show(progressButtonPanel, "button");
    }
//   public static void main(String[] args) throws InterruptedException {
//	ProgressBar bar = new ProgressBar("Verifying toolparameters.", 200, 140);
//	Thread.sleep(4000);
//	bar.setUpdateText("Execute Parameters");
//	Thread.sleep(4000);
//	bar.setButton("The installed tool you can find in your Project View.");
//   }

    private void initializeForm() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}