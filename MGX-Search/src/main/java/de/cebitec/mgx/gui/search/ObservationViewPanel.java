package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Sequence;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * Observation View Panel, fuer die Darstellung des Reads und der dazugehoerigen
 * Observations.
 *
 * @author pbelmann
 */
public class ObservationViewPanel extends javax.swing.JPanel {

   /**
    * Der Read, zu dem die einzelnen Observations dargestellt werden sollen.
    */
   private Sequence seq;
   /**
    * Observations die in Ebenen angeordnet sind.
    */
   private OrderedObservations orderedObservations;
   /**
    * Gibt die Groesse der Abschnitte an, in die der Read aufgeteilt wird.
    */
   private double cutValue;
   /**
    * Obsview fuer die Darstellung der Observations und des Reads.
    */
   private JPanel obsview;

   private MGXMaster master;


   /**
    * Konstruktor
    *
    * @param lOrderedObservations geordnete Observations
    * @param lSeq Sequenz des Reads.
    */
   public ObservationViewPanel(OrderedObservations lOrderedObservations, Sequence lSeq, MGXMaster lMaster) {
	super();

	this.master = lMaster;
	this.seq = lSeq;
	this.orderedObservations = lOrderedObservations;
	initComponents();
   }

   /**
    * Initialisiert alle Komponenten.
    */
   private void initComponents() {

	int height = ((orderedObservations.getLayers().size() + 1) * 10) + 15;
	JLabel readName = new javax.swing.JLabel();

	calculateSequenceCuts();

	obsview = new javax.swing.JPanel() {
	   @Override
	   protected void paintComponent(final Graphics g) {
		new PaintReadObservations(orderedObservations.getReadLength(),
		    orderedObservations.getLayers(), g, obsview, cutValue);
	   }
	};
	obsview.setSize(0, height);
	obsview.setPreferredSize(new Dimension(0, height));
	readName.setText(seq.getName() + " (" + seq.getLength() + "bp)");
	setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));


	javax.swing.GroupLayout obsviewLayout = new javax.swing.GroupLayout(obsview);
	obsview.setLayout(obsviewLayout);
	obsviewLayout.setHorizontalGroup(
	    obsviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    .addGap(0, 432, Short.MAX_VALUE));
	obsviewLayout.setVerticalGroup(
	    obsviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    .addGap(0, 120, Short.MAX_VALUE));

	javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	this.setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    .addGroup(layout.createSequentialGroup()
	    .addContainerGap()
	    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    .addComponent(obsview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	    .addGroup(layout.createSequentialGroup()
	    .addComponent(readName)
	    .addGap(0, 0, Short.MAX_VALUE)))
	    .addContainerGap()));
	layout.setVerticalGroup(
	    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    .addGroup(layout.createSequentialGroup()
	    .addContainerGap()
	    .addComponent(readName)
	    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	    .addComponent(obsview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
	    .addContainerGap()));
	this.setEnabled(true);
	obsview.setEnabled(true);
	obsview.repaint();
   }

   
   /*
    * Berechnet die Abschnitte, in die die Sequenzen eingeteilt werden.
    */
    private void calculateSequenceCuts() {
        cutValue = ((double) orderedObservations.getReadLength() / 4);
        int length = Integer.toString((int) orderedObservations.getReadLength()).length();
        double temp = 10;
        for (int counter = 0; counter < length - 2; counter++) {
           temp *= 10;
        }

        cutValue /= temp;
        double roundValue = Math.round(cutValue);

        while (roundValue == 0) {

           double newTemp = temp;
           newTemp /= 10;
           cutValue *= temp;
           cutValue /= newTemp;
           temp = newTemp;
           roundValue = Math.round(cutValue);
        }

        cutValue = roundValue * temp;
    }
}
