/*
 * GroupFrame.java
 *
 * Created on Dec 28, 2011, 2:52:00 PM
 */
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.gui.nodes.ReplicateGroupNode;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.Serial;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class ReplicateGroupFrame extends GroupFrameBase<ReplicateGroupI, ReplicateI> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ReplicateGroupFrame(final ReplicateGroupI rgroup) {
        super(rgroup.getManager(), rgroup, new ReplicateGroupNode(rgroup));
        initComponents();

        //
        // set initial properties
        //
        setTitle(rgroup.getName() + " (" + rgroup.getNumSequences() + " sequences)");
        displayName.setText(rgroup.getName());
        color.setBackground(rgroup.getColor());
        //
        // add listeners _after_ setting initial values
        //
        displayName.getDocument().addDocumentListener(new DocumentListener() {

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
                if (displayName.getDocument() == e.getDocument()) {
                    rgroup.setName(displayName.getText());
//            DecimalFormat df = new DecimalFormat(",###"); // FIXME
//            setTitle(vGroup.getName() + " (" + df.format(vGroup.getNumSequences()) + " sequences)");
//            displayName.setBackground(Color.WHITE);
                }
            }

        });
        //
        color.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JColorChooser jcc = new JColorChooser(rgroup.getColor());
                JDialog dialog = JColorChooser.createDialog(new JFrame(), "Choose color", false, jcc, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        Color newColor = jcc.getColor();
                        if (newColor != null) {
                            color.setBackground(newColor);
                            rgroup.setColor(newColor);
                        }
                    }
                }, null);
                dialog.setVisible(true);
            }
        });
        addReplicate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                VGroupManager.getInstance().createReplicate(rgroup);
            }
        });
        // 
        active.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                rgroup.setActive(active.isSelected());
            }
        });
        //
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                VGroupManager.getInstance().setSelectedReplicateGroup(rgroup);
            }
        });

        ReplicateGroupTreeView view = new ReplicateGroupTreeView(rgroup);
        view.setRootVisible(false);
        panel.add(view, BorderLayout.CENTER);

        setVisible(true);
        ToolTipManager.sharedInstance().registerComponent(panel);
        ToolTipManager.sharedInstance().registerComponent(view);
    }

//    public final ReplicateGroupI getReplicateGroup() {
//        return replGroup;
//    }
    @Override
    public String getToolTipText() {
        return "Replicate group " + getContent().getName();
    }

//    @Override
//    public void dispose() {
//        getContent().removePropertyChangeListener(this);
//        VGroupManager.getInstance().removeReplicateGroup(getContent());
//        super.dispose();
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        active = new javax.swing.JCheckBox();
        displayName = new javax.swing.JTextField();
        topRightPanel = new javax.swing.JPanel();
        color = new javax.swing.JButton();
        addReplicate = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);
        setMaximumSize(new java.awt.Dimension(180, 200));
        setMinimumSize(new java.awt.Dimension(180, 200));
        setPreferredSize(new java.awt.Dimension(180, 200));

        panel.setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.BorderLayout());

        active.setSelected(true);
        active.setToolTipText("Show group?");
        topPanel.add(active, java.awt.BorderLayout.WEST);

        displayName.setToolTipText("Group name");
        topPanel.add(displayName, java.awt.BorderLayout.CENTER);

        topRightPanel.setLayout(new java.awt.BorderLayout());

        color.setToolTipText("Choose color");
        color.setMaximumSize(new java.awt.Dimension(16, 16));
        color.setMinimumSize(new java.awt.Dimension(16, 16));
        color.setPreferredSize(new java.awt.Dimension(16, 16));
        topRightPanel.add(color, java.awt.BorderLayout.WEST);

        addReplicate.setText("+");
        addReplicate.setToolTipText("Add replicate");
        addReplicate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addReplicate.setMargin(new java.awt.Insets(1, 1, 1, 1));
        addReplicate.setMaximumSize(new java.awt.Dimension(22, 22));
        addReplicate.setMinimumSize(new java.awt.Dimension(22, 22));
        addReplicate.setPreferredSize(new java.awt.Dimension(22, 22));
        topRightPanel.add(addReplicate, java.awt.BorderLayout.EAST);

        topPanel.add(topRightPanel, java.awt.BorderLayout.EAST);

        panel.add(topPanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox active;
    private javax.swing.JButton addReplicate;
    private javax.swing.JButton color;
    private javax.swing.JTextField displayName;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel topRightPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ReplicateGroupI.REPLICATEGROUP_RENAMED:
                setTitle(getContent().getName() + " (" + decFormat.format(getContent().getNumSequences()) + " sequences)");
                displayName.setBackground(Color.WHITE);
                break;
            case ReplicateGroupI.REPLICATEGROUP_ACTIVATED:
            case VisualizationGroupI.VISGROUP_ACTIVATED:
                //ignore
                break;
            case ReplicateGroupI.REPLICATEGROUP_DEACTIVATED:
            case VisualizationGroupI.VISGROUP_DEACTIVATED:
            case VisualizationGroupI.VISGROUP_RENAMED:
                //ignore
                break;
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_ADDED:
                ReplicateI newGrp = (ReplicateI) evt.getNewValue();
//                newGrp.addPropertyChangeListener(this);
                repaint();
                break;
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_REMOVED:
                repaint();
                break;
            case VisualizationGroupI.VISGROUP_ATTRTYPE_CHANGED:
                break; //ignore
            case VisualizationGroupI.VISGROUP_HAS_DIST:
                // ignore
                break;
            case ReplicateGroupI.REPLICATEGROUP_CHANGED:
                setTitle(getContent().getName() + " (" + decFormat.format(getContent().getNumSequences()) + " sequences)");
                break;
            case VGroupManagerI.REPLICATEGROUP_SELECTION_CHANGED:
                try {
                setSelected(getContent() == evt.getNewValue());
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
            break;
            case VisualizationGroupI.OBJECT_MODIFIED:
                repaint();
                break;
            default:
                super.propertyChange(evt);
        }
    }

}
