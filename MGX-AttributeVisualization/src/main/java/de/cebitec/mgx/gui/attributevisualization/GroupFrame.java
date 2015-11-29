/*
 * GroupFrame.java
 *
 * Created on Dec 28, 2011, 2:52:00 PM
 */
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.gui.nodefactory.GroupedSeqRunNodeFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DecimalFormat;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class GroupFrame extends javax.swing.JInternalFrame implements ExplorerManager.Provider, ItemListener, ActionListener, DocumentListener, PropertyChangeListener {

    private final VisualizationGroupI vGroup;
    private final ExplorerManager exmngr = new ExplorerManager();
    private final GroupedSeqRunNodeFactory nodeFact;
    private final static DecimalFormat df = new DecimalFormat(",###");

    public GroupFrame(VisualizationGroupI group) {
        initComponents();
        vGroup = group;
        vGroup.addPropertyChangeListener(this);
        //
        // needed to receive selectionChange events
        VGroupManager.getInstance().addPropertyChangeListener(this);
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
        addInternalFrameListener(new SelectionHandler());
        //
        nodeFact = new GroupedSeqRunNodeFactory(vGroup);

        // invisible root node
        exmngr.setRootContext(new AbstractNode(Children.create(nodeFact, true)));

        VizGroupListView myListView = new VizGroupListView(vGroup);
        panel.add(myListView, BorderLayout.CENTER);

        setVisible(true);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().registerComponent(panel);
        ToolTipManager.sharedInstance().registerComponent(myListView);
    }

    public final VisualizationGroupI getGroup() {
        return vGroup;
    }

    @Override
    public String getToolTipText() {
        return "FOO " + vGroup.getName();
    }

    @Override
    public void dispose() {
        vGroup.removePropertyChangeListener(this);
        VGroupManager.getInstance().removePropertyChangeListener(this);
        VGroupManager.getInstance().removeVizGroup(vGroup);
        super.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        active = new javax.swing.JCheckBox();
        displayName = new javax.swing.JTextField();
        color = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);
        setMinimumSize(new java.awt.Dimension(180, 200));
        setPreferredSize(new java.awt.Dimension(180, 200));

        panel.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        active.setSelected(true);
        active.setToolTipText("Show group?");
        jPanel2.add(active, java.awt.BorderLayout.WEST);

        displayName.setToolTipText("Group name");
        jPanel2.add(displayName, java.awt.BorderLayout.CENTER);

        color.setToolTipText("Choose color");
        color.setMaximumSize(new java.awt.Dimension(16, 16));
        color.setMinimumSize(new java.awt.Dimension(16, 16));
        color.setPreferredSize(new java.awt.Dimension(16, 16));
        jPanel2.add(color, java.awt.BorderLayout.EAST);

        panel.add(jPanel2, java.awt.BorderLayout.NORTH);

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox active;
    private javax.swing.JButton color;
    private javax.swing.JTextField displayName;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel panel;
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
//            DecimalFormat df = new DecimalFormat(",###"); // FIXME
//            setTitle(vGroup.getName() + " (" + df.format(vGroup.getNumSequences()) + " sequences)");
//            displayName.setBackground(Color.WHITE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        final JColorChooser jcc = new JColorChooser(vGroup.getColor());
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case VisualizationGroupI.VISGROUP_RENAMED:
                setTitle(vGroup.getName() + " (" + df.format(vGroup.getNumSequences()) + " sequences)");
                displayName.setBackground(Color.WHITE);
                break;
            case VisualizationGroupI.VISGROUP_ACTIVATED:
                //ignore
                break;
            case VisualizationGroupI.VISGROUP_DEACTIVATED:
                //ignore
                break;
            case VisualizationGroupI.VISGROUP_ATTRTYPE_CHANGED:
                break; //ignore
            case VisualizationGroupI.VISGROUP_HAS_DIST:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_CHANGED:
                setTitle(vGroup.getName() + " (" + df.format(vGroup.getNumSequences()) + " sequences)");
                break;
            case VGroupManagerI.VISGROUP_SELECTION_CHANGED:
                try {
                    setSelected(vGroup == evt.getNewValue());
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
                break;
            case VisualizationGroupI.OBJECT_MODIFIED:
                repaint();
                break;
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_ADDED:
                // ignore
                break;
            default:
                System.err.println("GroupFrame for " + vGroup.getName() + " got event " + evt.getPropertyName());
        }
    }

}
