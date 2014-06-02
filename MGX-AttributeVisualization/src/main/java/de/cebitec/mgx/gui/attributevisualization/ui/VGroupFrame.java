/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization.ui;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodefactory.VisualizationGroupNodeFactory;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;

/**
 *
 * @author sjaenick
 */
public class VGroupFrame extends javax.swing.JPanel implements ExplorerManager.Provider, ItemListener, ActionListener, DocumentListener, PropertyChangeListener {

    private final VisualizationGroupI vGroup;
    private ExplorerManager exmngr = new ExplorerManager();
    private VisualizationGroupNodeFactory vgnf;
    private MyListView listView;

    /**
     * Creates new form VGroupFrame
     */
    public VGroupFrame(VisualizationGroupI group) {
        initComponents();
        vGroup = group;
        vGroup.addPropertyChangeListener(this);
        //
        // set initial properties
        //
        setTitle(vGroup.getName() + " (" + vGroup.getNumSequences() + " sequences)");
        displayName.setText(vGroup.getName());
        color.setBackground(vGroup.getColor());
        //
        // add listeners _after_ setting initial values
        //
        displayName.getDocument().addDocumentListener(this);
        color.addActionListener(this);
        active.addItemListener(this);
        //
        vgnf = new VisualizationGroupNodeFactory(vGroup);

        final InvisibleRoot root = new InvisibleRoot(Children.create(vgnf, true));
        exmngr.setRootContext(root);

        listView = new MyListView();
        this.add(listView, BorderLayout.CENTER);

        setVisible(true);
    }

    public VisualizationGroupI getGroup() {
        return vGroup;
    }

    //@Override
    public final void setTitle(String title) {
        setToolTipText(title);
        //super.setTitle(title);
    }
//
//    @Override
//    public void dispose() {
//        vGroup.removePropertyChangeListener(this);
//        VGroupManager.getInstance().removeGroup(vGroup);
//        super.dispose();
//    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        active = new javax.swing.JCheckBox();
        displayName = new javax.swing.JTextField();
        color = new javax.swing.JButton();

        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        org.openide.awt.Mnemonics.setLocalizedText(active, org.openide.util.NbBundle.getMessage(VGroupFrame.class, "VGroupFrame.active.text")); // NOI18N
        active.setToolTipText(org.openide.util.NbBundle.getMessage(VGroupFrame.class, "VGroupFrame.active.toolTipText")); // NOI18N

        displayName.setText(org.openide.util.NbBundle.getMessage(VGroupFrame.class, "VGroupFrame.displayName.text")); // NOI18N
        displayName.setToolTipText(org.openide.util.NbBundle.getMessage(VGroupFrame.class, "VGroupFrame.displayName.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(color, org.openide.util.NbBundle.getMessage(VGroupFrame.class, "VGroupFrame.color.text")); // NOI18N
        color.setToolTipText(org.openide.util.NbBundle.getMessage(VGroupFrame.class, "VGroupFrame.color.toolTipText")); // NOI18N
        color.setMaximumSize(new java.awt.Dimension(16, 16));
        color.setMinimumSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(active)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(displayName, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(color, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(active)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(color, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(displayName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 270, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox active;
    private javax.swing.JButton color;
    private javax.swing.JTextField displayName;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return exmngr;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        vGroup.setActive(active.isSelected());
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        handleUpdate(e);
    }

    private void handleUpdate(DocumentEvent e) {
        Document d = e.getDocument();
        if (displayName.getDocument() == d) {
            vGroup.setName(displayName.getText());
            DecimalFormat df = new DecimalFormat(",###"); // FIXME
            setTitle(vGroup.getName() + " (" + df.format(vGroup.getNumSequences()) + " sequences)");
            displayName.setBackground(Color.WHITE);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (VisualizationGroupI.VISGROUP_CHANGED.equals(evt.getPropertyName())) {
            setTitle(vGroup.getName() + " (" + vGroup.getNumSequences() + " sequences)");
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        final JColorChooser jcc = new JColorChooser(vGroup.getColor());
        //jcc.setChooserPanels(new AbstractColorChooserPanel[]{});
        JDialog dialog = JColorChooser.createDialog(new JFrame(), "Choose color", false, jcc, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Color newColor = jcc.getColor();
                if (newColor != null) {
                    color.setBackground(newColor);
                    vGroup.setColor(newColor);
                }
            }
        }, null);
        dialog.setVisible(true);
    }

    private class MyListView extends TreeTableView implements PropertyChangeListener {

        public MyListView() {
            super();
            setRootVisible(false);
            setDropTarget(true);
            //setShowParentNode(true);
            setAllowedDropActions(DnDConstants.ACTION_COPY + DnDConstants.ACTION_REFERENCE);
            setDropTarget();
            vGroup.addPropertyChangeListener(this);
        }

        private void setDropTarget() {
            DropTarget dt = new DropTarget(this, new DropTargetAdapter() {
                @Override
                public void dragEnter(DropTargetDragEvent dtde) {
                    Transferable t = dtde.getTransferable();
                    if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
                        try {
                            final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
                            if (mto.areDataFlavorsSupported(new DataFlavor[]{SeqRunI.DATA_FLAVOR})) {
                                int elems = mto.getCount();
                                for (int i = 0; i < elems; i++) {
                                    SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                                    if (vGroup.getSeqRuns().contains(run)) {
                                        dtde.rejectDrag();
                                        return;
                                    }
                                }
                                dtde.acceptDrag(DnDConstants.ACTION_COPY);
                                return;
                            }
                        } catch (UnsupportedFlavorException | IOException e) {
                        }
                    }

                    if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
                        try {
                            SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
                            if (run != null && !vGroup.getSeqRuns().contains(run)) {
                                dtde.acceptDrag(DnDConstants.ACTION_COPY);
                                return;
                            }
                        } catch (UnsupportedFlavorException | IOException ex) {
                        }
                    }

                    dtde.rejectDrag();
                }

                @Override
                public void drop(DropTargetDropEvent dtde) {
                    Transferable t = dtde.getTransferable();
                    if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
                        try {
                            final MultiTransferObject mto = (MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor);
                            if (mto.areDataFlavorsSupported(new DataFlavor[]{SeqRunI.DATA_FLAVOR})) {
                                int elems = mto.getCount();
                                for (int i = 0; i < elems; i++) {
                                    SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                                    if (vGroup.getSeqRuns().contains(run)) {
                                        dtde.rejectDrop();
                                        return;
                                    }
                                }
                                for (int i = 0; i < elems; i++) {
                                    SeqRunI run = (SeqRunI) mto.getTransferData(i, SeqRunI.DATA_FLAVOR);
                                    SeqRunNode srn = new SeqRunNode(run.getMaster(), run, Children.LEAF);
                                    vgnf.addNode(srn);
                                }
                                dtde.dropComplete(true);
                                return;
                            }
                        } catch (UnsupportedFlavorException | IOException e) {
                        }
                    }

                    if (dtde.isDataFlavorSupported(SeqRunI.DATA_FLAVOR)) {
                        try {
                            SeqRunI run = (SeqRunI) dtde.getTransferable().getTransferData(SeqRunI.DATA_FLAVOR);
                            if (run != null && !vGroup.getSeqRuns().contains(run)) {
                                SeqRunNode srn = new SeqRunNode(run.getMaster(), run, Children.LEAF);
                                vgnf.addNode(srn);
                                dtde.dropComplete(true);
                                return;
                            }
                        } catch (UnsupportedFlavorException | IOException ex) {
                        }
                    }

                    dtde.rejectDrop();
                }
            });
            setDropTarget(dt);

        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            //System.err.println("ListView got "+evt.getPropertyName());
        }
    }

    private class InvisibleRoot extends AbstractNode {

        public InvisibleRoot(Children children, Lookup lookup) {
            super(children, lookup);
        }

        public InvisibleRoot(Children children) {
            super(children);
        }
    }
}
