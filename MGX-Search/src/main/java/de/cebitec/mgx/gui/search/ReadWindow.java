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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author belmann
 */
public class ReadWindow extends JFrame {

   MGXMaster master;
   long sequenceId;
   JComponent component;

   public ReadWindow(MGXMaster lMaster, long seqId) {
	this.sequenceId = seqId;
	this.master = lMaster;
	JPanel panel = new JPanel(new BorderLayout());
	JPanel readNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	readNamePanel.add(new JLabel(this.getSequence()));
	panel.add(readNamePanel, BorderLayout.NORTH);


	JPanel sequencePanel = new JPanel(new BorderLayout());

	JTextArea sequenceArea = new JTextArea();
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
	buttonPanel.add(closeButton);
	panel.add(buttonPanel, BorderLayout.SOUTH);
	this.setLocationRelativeTo(null);
	this.setTitle("Read Sequence");
	this.setSize(500, 500);
	this.add(panel);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.pack();
	this.setVisible(true);

   }

   private String getSequence() {

	Sequence s = master.Sequence().fetch(sequenceId);

	StringBuilder sb = new StringBuilder(">")
	    .append(s.getName())
	    .append("\n")
	    .append(s.getSequence())
	    .append("\n");


	return "SequenceName";
   }

//   public static void main(String[] args) {

//	ReadWindow window = new ReadWindow();

//   }
}
