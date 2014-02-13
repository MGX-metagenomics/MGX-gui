package de.cebitec.mgx.gui.charts3d;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jzy3d.chart.Chart;


/**
 *
 * @author sj
 */

public class PNGKeyboardSaver extends KeyAdapter {

    private final Chart chart;

    public PNGKeyboardSaver(Chart chart) {
        this.chart = chart;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
            JFileChooser jfc = new JFileChooser(".");
            jfc.setFileFilter(new FileNameExtensionFilter("PNG file", new String[]{".png"}));
            jfc.showSaveDialog(e.getComponent());
            
            if(jfc.getSelectedFile()!=null){
                if(!jfc.getSelectedFile().getParentFile().exists())
                        jfc.getSelectedFile().mkdirs();
                try {
                    chart.screenshot(new File(jfc.getSelectedFile().toString()+".png"));
                    //ImageIO.write(chart.screenshot(), "png", new File(jfc.getSelectedFile().toString()+".png"));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(e.getComponent(), "Error saving file.");
                    Logger.getLogger(PNGKeyboardSaver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
