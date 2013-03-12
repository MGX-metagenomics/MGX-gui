/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Sequence;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

/**
 *
 * Fenster zum anzeigen der Read Sequenzen.
 *
 * @author pbelmann
 */
public class ReadWindow extends JFrame {

    /*
     * JTextArea fuer die Darstellung
     */
    private JTextArea sequenceArea;
    /*
     * Master zum Laden der Sequenzen.
     */
    private MGXMaster master;
    /*
     * Sequenzen die geladen werden muessen.
     */
    private ArrayList<Sequence> sequences;
    /*
     * ProgressBar fuer das Anzeigen des Lade Fortschritts.
     * 
     */
    private JProgressBar bar;
    /*
     * Panel fuer die JProgressBar.
     */
    private final JPanel processPanel;

    /*
     * Konstruktor
     * 
     * @param lMaster Master zum Laden der Sequenzen.
     * 
     * @param sequences Sequenzen die geladen werden sollen.
     */
    public ReadWindow(MGXMaster lMaster, ArrayList<Sequence> sequences) {
        this.sequences = sequences;
        this.master = lMaster;
        JPanel panel = new JPanel(new BorderLayout());
        processPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        bar = new JProgressBar();
        panel.add(processPanel, BorderLayout.NORTH);
        JPanel sequencePanel = new JPanel(new BorderLayout());
        sequenceArea = new JTextArea();
        sequenceArea.setWrapStyleWord(true);
        sequenceArea.setLineWrap(true);
        sequenceArea.setSize(500, 400);
        sequenceArea.setPreferredSize(new Dimension(500, 400));
        sequencePanel.add(sequenceArea);
        panel.add(sequencePanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JButton clipboardButton = new JButton("Copy to Clipboard");
        clipboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyAllSequencesToClipBoard();
            }
        });
        buttonPanel.add(clipboardButton);
        buttonPanel.add(closeButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        this.setLocationRelativeTo(null);
        this.setTitle("Read Sequences");
        this.setSize(500, 500);
        this.setResizable(false);
        this.add(panel);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.pack();
        loadSequences();
        this.setVisible(true);

    }

    /*
     * Laedt die Sequenzen.
     * 
     */
    private void loadSequences() {

        SwingWorker worker = new SwingWorker() {
            StringBuilder sb = new StringBuilder();

            @Override
            protected Object doInBackground() throws Exception {

                Sequence s;
                for (Sequence seq : sequences) {
                    s = master.Sequence().fetch(seq.getId());
                    sb.append(">")
                            .append(s.getName())
                            .append("\n")
                            .append(s.getSequence())
                            .append("\n");
                }

                return null;
            }

            @Override
            protected void done() {
                sequenceArea.setText(sb.toString());
                processPanel.remove(bar);
                super.done();
            }
        };
        bar.setIndeterminate(true);
        bar.setString("Loading sequences...");
        bar.setStringPainted(true);
        processPanel.add(bar);
        worker.execute();
    }

    /*
     * Kopiert alle Sequenzen in den Arbeitsspeicher.
     */
    private void copyAllSequencesToClipBoard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(sequenceArea.getText()), null);
    }
}
