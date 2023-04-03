/*
 * GroupFrame.java
 *
 * Created on Dec 28, 2011, 2:52:00 PM
 */
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.AssemblyGroupI;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.nodes.AssemblyGroupNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.io.Serial;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.explorer.view.BeanTreeView;

/**
 *
 * @author sj
 */
public class AssemblyGroupFrame extends GroupFrameBase<AssemblyGroupI, AssembledSeqRunI> {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public AssemblyGroupFrame(final AssemblyGroupI vGroup) {
        super(vGroup.getManager(), vGroup, new AssemblyGroupNode(vGroup));
        initComponents();
        // set initial properties
        //
        super.setTitle(vGroup.getDisplayName() + " (" + decFormat.format(vGroup.getNumSequences()) + " sequences)");
        displayName.setText(vGroup.getDisplayName());
        color.setBackground(vGroup.getColor());
        color.setForeground(vGroup.getColor());
        color.validate();

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
                    vGroup.setName(displayName.getText());
                }
            }

        });

        //
        color.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JColorChooser jcc = new JColorChooser(vGroup.getColor());
                JDialog dialog = JColorChooser.createDialog(new JFrame(), "Choose color", false, jcc, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        Color newColor = jcc.getColor();
                        if (newColor != null) {
                            color.setBackground(newColor);
                            color.setForeground(newColor);
                            color.validate();
                            vGroup.setColor(newColor);
                        }
                    }
                }, null);
                dialog.setVisible(true);
            }
        });

        // 
        active.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                vGroup.setActive(active.isSelected());
            }
        });

        BeanTreeView btv = new BeanTreeView();
        btv.setRootVisible(false);
        panel.add(btv, BorderLayout.CENTER);

        super.setVisible(true);
        ToolTipManager.sharedInstance().registerComponent(panel);
    }

    @Override
    public String getToolTipText() {
        return getContent().getDisplayName();
    }

//    @Override
//    public void dispose() {
//        VGroupManager.getInstance().removeVisualizationGroup(getContent());
//        super.dispose();
//    }
//    @Override
//    public void dispose() {
//        getContent().getManager().removeVisualizationGroup(getContent());
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
        color = new javax.swing.JButton();

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

        color.setToolTipText("Choose color");
        color.setMaximumSize(new java.awt.Dimension(16, 16));
        color.setMinimumSize(new java.awt.Dimension(16, 16));
        color.setPreferredSize(new java.awt.Dimension(16, 16));
        topPanel.add(color, java.awt.BorderLayout.EAST);

        panel.add(topPanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox active;
    private javax.swing.JButton color;
    private javax.swing.JTextField displayName;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case VisualizationGroupI.VISGROUP_RENAMED:
            case AssemblyGroupI.ASMGROUP_RENAMED:
                setTitle(getContent().getDisplayName() + " (" + decFormat.format(getContent().getNumSequences()) + " sequences)");
                displayName.setBackground(Color.WHITE);
                break;
            case VisualizationGroupI.VISGROUP_ACTIVATED:
            case VisualizationGroupI.VISGROUP_DEACTIVATED:
            case AssemblyGroupI.ASMGROUP_ACTIVATED:
            case AssemblyGroupI.ASMGROUP_DEACTIVATED:
                //ignore
                break;
            case VisualizationGroupI.VISGROUP_ATTRTYPE_CHANGED:
                break; //ignore
            case VisualizationGroupI.VISGROUP_HAS_DIST:
            case AssemblyGroupI.ASMGROUP_HAS_DIST:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_CHANGED:
            case AssemblyGroupI.ASMGROUP_CHANGED:
                setTitle(getContent().getDisplayName() + " (" + decFormat.format(getContent().getNumSequences()) + " sequences)");
                break;
            case ModelBaseI.OBJECT_MODIFIED:
                repaint();
                break;
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_ADDED:
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_REMOVED:
            case ReplicateGroupI.REPLICATEGROUP_ACTIVATED:
            case ReplicateGroupI.REPLICATEGROUP_DEACTIVATED:
            case VGroupManagerI.REPLICATEGROUP_SELECTION_CHANGED:
            case VGroupManagerI.ASMGROUP_SELECTION_CHANGED:
                // ignore
                break;
            default:
                super.propertyChange(evt);
        }
    }

}
