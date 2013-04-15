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
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import org.openide.util.Exceptions;

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
    private final JTextArea sequenceArea;
    /*
     * Master zum Laden der Sequenzen.
     */
    private final MGXMaster master;
    /*
     * Sequenzen die geladen werden muessen.
     */
    private final List<Sequence> sequences;
    /*
     * ProgressBar fuer das Anzeigen des Lade Fortschritts.
     * 
     */
    private final JProgressBar bar;
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
    public ReadWindow(MGXMaster lMaster, List<Sequence> sequences) {
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

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // fetch updated sequence objects with actual sequence data
                master.Sequence().fetchSeqData(sequences);

                StringBuilder sb = new StringBuilder();
                for (Sequence seq : sequences) {
                    sb.append(">").append(seq.getName())
                            .append("\n")
                            .append(seq.getSequence())
                            .append("\n");
                }

                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    sequenceArea.setText(get());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                processPanel.remove(bar);
                super.done();
            }
        };
        bar.setIndeterminate(true);
        bar.setString("Loading sequences...");
        bar.setStringPainted(true);
        processPanel.add(bar);
        worker.execute();

    } /*
     * Kopiert alle Sequenzen in den Arbeitsspeicher.
     */


    private void copyAllSequencesToClipBoard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(sequenceArea.getText()), null);
    }
}
