/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.util;

import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.api.model.assembly.GeneObservationI;
import de.cebitec.mgx.gui.binexplorer.BinExplorerTopComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author sj
 */
public class ObservationCellRenderer extends DefaultTableCellRenderer {

    private final BinExplorerTopComponent tc;
    private final ObservationDisplay display = new ObservationDisplay();

    public ObservationCellRenderer(BinExplorerTopComponent tc) {
        super();
        this.tc = tc;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 4 && obj instanceof GeneObservationI) {
            GeneI gene = tc.getSelectedFeature();
            GeneObservationI obs = (GeneObservationI) obj;

            display.setGene(gene);
            display.setObservation(obs);
            display.setSize(table.getColumnModel().getColumn(column).getWidth(), getHeight());

            return display;
        }
        Component ret = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
        if (ret instanceof JLabel) {
            JLabel lbl = (JLabel) ret;
            lbl.setToolTipText(lbl.getText());
        }
        return ret;
    }

    private static class ObservationDisplay extends JPanel {

        private GeneI gene;
        private GeneObservationI observation;

        public ObservationDisplay() {
        }

        public void setGene(GeneI gene) {
            this.gene = gene;
        }

        public void setObservation(GeneObservationI observation) {
            this.observation = observation;
        }

        private static final int border = 3;

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            
            if (gene == null || observation == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            int mid = getHeight() / 2;
            g2.setColor(Color.BLACK);
            g2.drawLine(0, mid, getWidth(), mid);
            double plotWidth = getWidth() - border - border;
            double nuclLen;
            if (gene.getStart() < gene.getStop()) {
                nuclLen = gene.getStop() - gene.getStart() + 1;
            } else {
                nuclLen = gene.getStart() - gene.getStop() + 1;
            }
            double scaleFact = nuclLen / plotWidth;

            double obsLen;
            if (observation.getStart() < observation.getStop()) {
                obsLen = observation.getStop() - observation.getStart() + 1;
            } else {
                obsLen = observation.getStart() - observation.getStop() + 1;
            }

            g2.setColor(Color.BLUE);

            int minX = Math.min(observation.getStart(), observation.getStop());
            minX = (int) (border + minX / scaleFact);

            int width = (int) (obsLen / scaleFact);

            g2.fillRect(minX, border, width, getHeight() - border - border);
        }

    }

}
