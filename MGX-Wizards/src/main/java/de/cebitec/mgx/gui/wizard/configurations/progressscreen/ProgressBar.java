package de.cebitec.mgx.gui.wizard.configurations.progressscreen;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *Fenster fuer die Anzeige des Fortschritts.
 * @author belmann
 */
public class ProgressBar extends JFrame {

    /**
     * Label fuer das Setzen des Texts
     */
    private JLabel label;
    
    /**
     * Breite des Fensters, wichtig fuer die moegliche Breite des Texts.
     */
    private int width = 0;

    /**
     * Panel fuer Text und Button.
     */
    private final JPanel progressButtonPanel;

    
    /**
     * Konstruktor baut das JFrame auf.
     * @param lMessage Anzuzeigende Nachricht.
     * @param title Titel der Nachricht.
     * @param lWidth Weite des Fensters
     * @param lHeight Breite des Fensters.
     */
    public ProgressBar(String lMessage, String title, int lWidth, int lHeight) {
        width = lWidth; 
        this.setTitle(title);
        setSize(lWidth, lHeight);
        setLayout(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        con.gridy = 0;
        con.weighty = 20;
        con.weightx = 20;
        con.anchor = GridBagConstraints.LINE_START;
        con.fill = GridBagConstraints.BOTH;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
       
        
        JPanel panel = new JPanel();
        add(panel, con);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridy = 0;
        c.weighty = 20;
        c.weightx = 20;
        label = new JLabel("<html><table><div align=center><td width=" + width + ">" + lMessage
                + "</td></div></table></html>");
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        setResizable(false);
        panel.add(label, c);
        c.gridy = 1;
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);

        progressButtonPanel = new JPanel(new CardLayout());
        JPanel borderProgressPanel = new JPanel();
        borderProgressPanel.add(bar, BorderLayout.CENTER);
        progressButtonPanel.add(borderProgressPanel, "progress");

        JPanel buttonBorderPanel = new JPanel();
        JButton b2 = new JButton("OK");
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        b2.setVerticalTextPosition(AbstractButton.CENTER);
        b2.setHorizontalTextPosition(AbstractButton.CENTER);
        buttonBorderPanel.add(b2);
        progressButtonPanel.add(buttonBorderPanel, "button");
        CardLayout cl = (CardLayout) (progressButtonPanel.getLayout());
        cl.show(progressButtonPanel, "progress");

        c.fill = GridBagConstraints.NONE;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(progressButtonPanel, c);
        setVisible(true);
    }

    
    /**
     * Updated den Text auf dem JFrame.
     * @param text Einzugebender Text.
     */
    public void setUpdateText(String text) {
        label.setText("<html><table><div align=center><td width=" + width + ">"
                + text + "" + "</td></div></table></html>");
    }

    /**
     * Setzt einen Ok Button auf dem Fenster.
     * @param text Anzuzeigender Text.
     */
    public void setButton(String text) {
        label.setText("<html><table><div align=center><td width=" + width + ">"
                + text + "</td></div></table></html>");
        CardLayout cl = (CardLayout) (progressButtonPanel.getLayout());
        cl.show(progressButtonPanel, "button");
    }
}
