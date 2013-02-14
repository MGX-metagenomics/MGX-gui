package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.analysis.misc.NewToolPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.ToolPanel;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class AnalysisVisualPanel1 extends JPanel implements ListSelectionListener, ChangeListener {

    /**
     * Creates new form AnalysisVisualPanel1
     */
    public AnalysisVisualPanel1() {
        initComponents();

        projectTools = setupTab("Project");
        serverTools = setupTab("Repository");

        tabs.add("Custom tool", newTool);
        newTool.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(NewToolPanel.TOOL_DEFINED)) {
                    updated();
                }
            }
        });
        tabs.setSelectedIndex(0);
        tabs.addChangeListener(this);
    }

    public void setProjectTools(List<Tool> pTools) {
        projectTools.setListData(pTools.toArray(new Tool[]{}));
    }

    public void setServerTools(List<Tool> sTools) {
        serverTools.setListData(sTools.toArray(new Tool[]{}));
    }
    private JList<Tool> projectTools = null;
    private JList<Tool> serverTools = null;
    private NewToolPanel newTool = new NewToolPanel();

    private JList<Tool> setupTab(String title) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JList<Tool> list = new JList<>();
        list.setCellRenderer(new ToolListRenderer());
        list.addListSelectionListener(this);
        scrollPane.setViewportView(list);
        tabs.addTab(title, scrollPane);
        return list;
    }

    public Tool getTool() {
        switch (getToolType()) {
            case PROJECT:
                return projectTools.getSelectedValue();
            case GLOBAL:
                return serverTools.getSelectedValue();
            case USER_PROVIDED:
                return newTool.getTool();
            default:
                assert false;
                return null;
        }
    }

    public String getNewToolVersion() {
        switch (getToolType()) {
            case USER_PROVIDED:
                return newTool.getVersion();
            default:
                return null;
        }
    }

    public ToolType getToolType() {
        switch (tabs.getSelectedIndex()) {
            case 0:
                return ToolType.PROJECT;
            case 1:
                return ToolType.GLOBAL;
            case 2:
                return ToolType.USER_PROVIDED;
            default:
                assert false;
                return null;
        }
    }

    @Override
    public String getName() {
        return "Tool selection";
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        selectedTool = new javax.swing.JTextField();
        tabs = new javax.swing.JTabbedPane();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AnalysisVisualPanel1.class, "AnalysisVisualPanel1.jLabel1.text")); // NOI18N

        selectedTool.setText(org.openide.util.NbBundle.getMessage(AnalysisVisualPanel1.class, "AnalysisVisualPanel1.selectedTool.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectedTool, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(selectedTool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField selectedTool;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // forward list selection events
        updated();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // visible tab changed
        updated();
    }

    private void updated() {
        Tool tool = getTool();
        selectedTool.setText("");
        if (tool != null) {
            selectedTool.setText(tool.getName());
        }
        firePropertyChange("toolSelected", null, tool);
    }

    private final class ToolListRenderer implements ListCellRenderer<Tool> {

        private ToolPanel panel = new ToolPanel();
        protected Border noFocusBorder = LineBorder.createGrayLineBorder();
        protected Border focusBorder = LineBorder.createBlackLineBorder();

        @Override
        public Component getListCellRendererComponent(JList<? extends Tool> list, Tool tool, int index, boolean isSelected, boolean cellHasFocus) {
            panel.setToolName(tool.getName());
            panel.setAuthor(tool.getAuthor());
            panel.setDescription(tool.getDescription());
            panel.setVersion(tool.getVersion().toString());
            panel.setBorder(isSelected ? focusBorder : noFocusBorder);
            return panel;
        }
    }
}
