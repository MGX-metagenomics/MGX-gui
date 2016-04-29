package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.ToolType;
import static de.cebitec.mgx.api.misc.ToolType.PROJECT;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.wizard.analysis.misc.NewToolPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.ToolPanel;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class AnalysisVisualPanel1 extends JPanel implements ListSelectionListener, ChangeListener {

    //private final MGXMasterI master;
    /**
     * Creates new form AnalysisVisualPanel1
     */
    public AnalysisVisualPanel1(MGXMasterI master) {
        //this.master = m;
        initComponents();
        newTool = new NewToolPanel();
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

    public void setProjectTools(Collection<ToolI> pTools) {
        projectTools.setListData(pTools.toArray(new ToolI[]{}));
        // if no tools are available, switch to repository tab
        if (pTools.isEmpty()) {
            tabs.setSelectedIndex(1);
        }
    }

    public void setServerTools(Collection<ToolI> sTools) {
        serverTools.setListData(sTools.toArray(new ToolI[]{}));
    }

    private JList<ToolI> projectTools = null;
    private JList<ToolI> serverTools = null;
    private final NewToolPanel newTool;

    private JList<ToolI> setupTab(String title) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JList<ToolI> list = new JList<>();
        list.setCellRenderer(new ToolListRenderer());
        list.addListSelectionListener(this);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(list);
        tabs.addTab(title, scrollPane);
        return list;
    }

    public long getToolId() {
        switch (getToolType()) {
            case PROJECT:
                return projectTools.getSelectedValue() != null ? projectTools.getSelectedValue().getId() : Identifiable.INVALID_IDENTIFIER;
            case GLOBAL:
                return serverTools.getSelectedValue() != null ? serverTools.getSelectedValue().getId() : Identifiable.INVALID_IDENTIFIER;
            case USER_PROVIDED:
                return Identifiable.INVALID_IDENTIFIER;
        }
        return -1;
    }

    public String getToolVersion() {
        switch (getToolType()) {
            case PROJECT:
                return projectTools.getSelectedValue() != null ? String.valueOf(projectTools.getSelectedValue().getVersion()) : null;
            case GLOBAL:
                return serverTools.getSelectedValue() != null ? String.valueOf(serverTools.getSelectedValue().getVersion()) : null;
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

    public String getToolName() {
        switch (getToolType()) {
            case PROJECT:
                return projectTools.getSelectedValue() != null ? projectTools.getSelectedValue().getName() : null;
            case GLOBAL:
                return serverTools.getSelectedValue() != null ? serverTools.getSelectedValue().getName() : null;
            case USER_PROVIDED:
                return newTool.getToolName();
            default:
                return null;
        }
    }

    public String getToolDescription() {
        switch (getToolType()) {
            case PROJECT:
                return projectTools.getSelectedValue() != null ? projectTools.getSelectedValue().getDescription() : null;
            case GLOBAL:
                return serverTools.getSelectedValue() != null ? serverTools.getSelectedValue().getDescription() : null;
            case USER_PROVIDED:
                return newTool.getToolDescription();
            default:
                return null;
        }

    }

    public String getToolAuthor() {
        switch (getToolType()) {
            case PROJECT:
                return projectTools.getSelectedValue() != null ? projectTools.getSelectedValue().getAuthor() : null;
            case GLOBAL:
                return serverTools.getSelectedValue() != null ? serverTools.getSelectedValue().getAuthor() : null;
            case USER_PROVIDED:
                return newTool.getToolAuthor();
            default:
                return null;
        }
    }

    public String getToolWebsite() {
        switch (getToolType()) {
            case PROJECT:
                return projectTools.getSelectedValue() != null ? projectTools.getSelectedValue().getUrl() : null;
            case GLOBAL:
                return serverTools.getSelectedValue() != null ? serverTools.getSelectedValue().getUrl() : null;
            case USER_PROVIDED:
                return newTool.getToolWebsite();
            default:
                return null;
        }
    }

    public String getToolXML() {
        switch (getToolType()) {
            case USER_PROVIDED:
                return newTool.getToolXML();
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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        selectedTool = new javax.swing.JTextField();
        tabs = new javax.swing.JTabbedPane();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AnalysisVisualPanel1.class, "AnalysisVisualPanel1.jLabel1.text")); // NOI18N

        selectedTool.setEditable(false);
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
        selectedTool.setText("");
        ToolI tool = null;
        switch (getToolType()) {
            case PROJECT:
                tool = projectTools.getSelectedValue();
                if (tool != null) {
                    selectedTool.setText(tool.getName());
                    firePropertyChange("toolSelected", null, tool);
                }
                break;
            case GLOBAL:
                tool = serverTools.getSelectedValue();
                if (tool != null) {
                    selectedTool.setText(tool.getName());
                    firePropertyChange("toolSelected", null, tool);
                }
                break;
            case USER_PROVIDED:
                String toolName = newTool.getToolName();
                if (toolName != null) {
                    selectedTool.setText(toolName);
                    firePropertyChange("toolSelected", null, tool);
                }
                break;
            default:
                assert false;
        }
    }

    private final class ToolListRenderer implements ListCellRenderer<ToolI> {

        private final ToolPanel panel = new ToolPanel();
        protected Border noFocusBorder = LineBorder.createGrayLineBorder();
        protected Border focusBorder = LineBorder.createBlackLineBorder();

        @Override
        public Component getListCellRendererComponent(JList<? extends ToolI> list, ToolI tool, int index, boolean isSelected, boolean cellHasFocus) {
            panel.setToolName(tool.getName());
            panel.setAuthor(tool.getAuthor());
            panel.setDescription(tool.getDescription());
            panel.setVersion(tool.getVersion().toString());
            panel.setBorder(isSelected ? focusBorder : noFocusBorder);
            return panel;
        }
    }
}
