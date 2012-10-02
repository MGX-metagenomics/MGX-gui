/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.gui.search.ComputeObservations.Layer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author pbelmann
 */
/**
 *
 * @author sj
 */
public class ObservationViewPanel extends javax.swing.JPanel {

    private RequestProcessor.Task myTask;
    private Sequence seq;
    private static Map<Sequence, WeakReference<Observation[]>> cache = Collections.<Sequence, WeakReference<Observation[]>>synchronizedMap(new HashMap<Sequence, WeakReference<Observation[]>>());
    private ComputeObservations compute;
    private double value;

    /**
     * Creates new form ObservationViewPanel
     */
    public ObservationViewPanel(ComputeObservations compute, MGXMaster master, Sequence seq) {
        super();
//        this.observations = observations;
        this.master = master;
        this.seq = seq;
        this.compute = compute;
        initComponents();
    }
    ArrayList<Layer> layers;
//    @Override
//    public int getHeight() {
//        return 200;
//    }
//    Observation[] observations;
    MGXMaster master;
    Sequence sequence;
    RequestProcessor processor;
    JLabel label;

    public void setCurrentData(final MGXMaster m, final Sequence seq, final RequestProcessor proc) {
        this.seq = seq;
        readName.setText(seq.getName() + " (" + seq.getLength() + "bp)");
//        SwingWorker worker = new SwingWorker() {
//            @Override
//            protected Object doInBackground() throws Exception {


        try {

            BufferedImage image = new BufferedImage(seq.getLength(), 100, BufferedImage.TYPE_INT_RGB);
            int length = seq.getLength();
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.red);

            if (!cache.containsKey(seq)) {
                // submit observation fetcher task
                fetchFromServer(m, seq, proc);
            } else {
//                observations = getObservations(m, seq, proc);
            }

            master = m;
            sequence = seq;
            processor = proc;


//                         graphics.drawLine(10, 10, length, 10);
//                         getScaledInstance(432, 120, 2)
//                         ImageIcon icon = new ImageIcon(image);
//                         label = new JLabel(icon);
//                         label.setBorder(BorderFactory.createLineBorder(Color.RED));
            obsview.setLayout(new BorderLayout());
//            obsview.setBorder(BorderFactory.createLineBorder(Color.BLUE));
//                         obsview.add(label);
//            obsview.repaint();
//            repaint();
        } catch (AssertionError ex) {
        }
    }

    private Observation[] getObservations(MGXMaster m, Sequence seq, RequestProcessor proc) {
        // if task is still running, wait for it to finish
        if (myTask != null) {
            myTask.waitFinished();
            myTask = null;
        }

        if (cache.containsKey(seq)) {
            Observation[] obs = cache.get(seq).get(); // create strong reference
            if (obs == null) {
                // weak ref expired, start new fetcher and invoke self
                fetchFromServer(m, seq, proc);
                return getObservations(m, seq, proc);
            } else {
                // weak ref alive, return value
                return obs;
            }
        }

        assert false;
        return null;
    }
    int height = 0;
    String myHeight = "";


    @Override
    public Point getToolTipLocation(MouseEvent event) {
        return new Point(getWidth(), 0);
    }

    private void initComponents() {
        
        
      
        
        height = ((compute.getLayers().size() + 1) * 10) + 15;
        readName = new javax.swing.JLabel();
        
        
         value = ((double) compute.getReadLength() / 4);

        int length = Integer.toString(compute.getReadLength()).length();

        double temp = 10;


        for (int counter = 0; counter < length - 2; counter++) {
            temp *= 10;
        }



        value /= temp;

        double roundValue = Math.round(value);
        
        
        while (roundValue == 0) {
           
            double newTemp = temp;
            newTemp /= 10;
            value *= temp;
            value /= newTemp;
            temp = newTemp;
            roundValue = Math.round(value);
        }
        value = roundValue * temp;
        
        obsview = new javax.swing.JPanel() {
   

            @Override
            protected void paintComponent(final Graphics g) {
                final int obsviewWidth = obsview.getWidth();
       
                           new PaintObservations(compute.getReadLength(),
                             compute.getLayers(), g,
                             obsviewWidth, obsview, obsview.getHeight(), value); 
            }
//            @Override
//            public Point getToolTipLocation(MouseEvent event) {
//                return new Point(getWidth(), 0);
//            }
        };
//        this.setLayout(new BorderLayout());
//        obsview.setLayout(new BorderLayout());
        obsview.setSize(0, height);
        obsview.setPreferredSize(new Dimension(0, height));
//        obsview.setSize(50, height);
//        obsview.setPreferredSize(new Dimension(50, height));

     

        readName.setText(seq.getName() + " (" + seq.getLength() + "bp)" );
        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
//        obsview.setBorder(BorderFactory.createLineBorder(Color.BLUE));

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



//        this.transferFocus();


//        this.setToolTipText(names);

//        obsview.setFocusable(true);
//        obsview.setEnabled(true);
//        obsview.requestFocus();
//        obsview.setToolTipText("test");
//        obsview.setOpaque(true);
//        obsview.getLocation();
//      Logger.getAnonymousLogger().info(TOOL_TIP_TEXT_KEY);
//        obsview.addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                Logger.getAnonymousLogger().info("focus");
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//            }
//        });
//        obsview.setBorder(BorderFactory.createLineBorder(Color.BLUE));       
//        obsview.repaint();
        obsview.setEnabled(false);
//        this.repaint();
//          if (height != 0) {
//              JLabel label = new JLabel(Integer.toString(this.getHeight()));
//              label.setPreferredSize(new Dimension(50,height));
//              label.setSize(new Dimension(50,height));
//            obsview.add(label);
//        }
        obsview.repaint();

    }// </editor-fold>
    // Variables declaration - do not modify
    private javax.swing.JPanel obsview;
    private javax.swing.JLabel readName;

    private void fetchFromServer(MGXMaster m, Sequence seq, RequestProcessor proc) {
        myTask = proc.post(new ObservationViewPanel.ObsFetcher(m, seq));
    }

    
 
    
    
    
    private class ObsFetcher implements Runnable { //, Future<Collection<Observation>> {

        private final MGXMaster master;
        private final Sequence seq;

        public ObsFetcher(MGXMaster master, Sequence seq) {
            this.master = master;
            this.seq = seq;
        }

        @Override
        public void run() {
            Observation[] obs = master.Observation().ByRead(seq).toArray(new Observation[]{});
            cache.put(seq, new WeakReference(obs));
        }
    }
}
