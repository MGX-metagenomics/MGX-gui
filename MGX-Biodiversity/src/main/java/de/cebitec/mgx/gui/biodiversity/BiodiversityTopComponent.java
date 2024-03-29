package de.cebitec.mgx.gui.biodiversity;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.ACE;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Chao1;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Shannon;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.ShannonEvenness;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Simpson;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import de.cebitec.mgx.gui.biodiversity.statistic.StatisticI;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Margalef;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Menhinick;
import java.awt.Image;
import java.io.Serial;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.biodiversity//Biodiversity//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "BiodiversityTopComponent",
        iconBase = "de/cebitec/mgx/gui/biodiversity/AlphaDiversity.svg",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "satellite", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.biodiversity.BiodiversityTopComponent")
//@ActionReference(path = "Menu/Window", position = 338)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BiodiversityAction",
        preferredID = "BiodiversityTopComponent"
)
@Messages({
    "CTL_BiodiversityAction=Biodiversity",
    "CTL_BiodiversityTopComponent=Biodiversity Indices",
    "HINT_BiodiversityTopComponent=Biodiversity Indices"
})
public final class BiodiversityTopComponent extends TopComponent implements LookupListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup.Result<GroupI> result;
    private GroupI<?> curGroup = null;
    private final TableModel model;
    private final StatisticI[] stats;
    //
    private final static String NOT_AVAILABLE = "N/A";

    @SuppressWarnings("unchecked")
    private BiodiversityTopComponent() {
        this.stats = new StatisticI[]{new ACE(), new Chao1(), 
            new Margalef(), new Menhinick(),
            new Shannon(), new ShannonEvenness(), new Simpson()};

        model = new DefaultTableModel(new String[]{"Index", "Value"}, stats.length) {

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    default:
                        return Double.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (int i = 0; i < stats.length; i++) {
            model.setValueAt(stats[i].getName(), i, 0);
        }
        setEmptyTable();
        initComponents();

        for (TableColumn tc : table.getColumns()) {
            if (0 != tc.getModelIndex()) {
                tc.setMinWidth(20);
                tc.setPreferredWidth(40);
                tc.setWidth(40);
            }
        }

        setName(Bundle.CTL_BiodiversityTopComponent());
        setToolTipText(Bundle.HINT_BiodiversityTopComponent());
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        result = Utilities.actionsGlobalContext().lookupResult(GroupI.class);
    }

    private static BiodiversityTopComponent instance = null;

    public static BiodiversityTopComponent getDefault() {
        if (instance == null) {
            instance = new BiodiversityTopComponent();
        }
        return instance;
    }

    @Override
    public Image getIcon() {
        Image image = super.getIcon();
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        groupName = new javax.swing.JLabel();
        attrType = new javax.swing.JLabel();
        panel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(BiodiversityTopComponent.class, "BiodiversityTopComponent.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(BiodiversityTopComponent.class, "BiodiversityTopComponent.jLabel7.text")); // NOI18N

        groupName.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        groupName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(groupName, org.openide.util.NbBundle.getMessage(BiodiversityTopComponent.class, "BiodiversityTopComponent.groupName.text")); // NOI18N

        attrType.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        attrType.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(attrType, org.openide.util.NbBundle.getMessage(BiodiversityTopComponent.class, "BiodiversityTopComponent.attrType.text")); // NOI18N

        panel.setLayout(new java.awt.GridLayout(1, 0));

        table.setModel(model);
        table.setEditable(false);
        table.setHighlighters(new Highlighter[] {HighlighterFactory.createAlternateStriping()});
        table.setSortable(false);
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(309, 309, 309)
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(groupName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(attrType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(groupName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(attrType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attrType;
    private javax.swing.JLabel groupName;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel panel;
    private org.jdesktop.swingx.JXTable table;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        result.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
    }

    private void update() {
        if (curGroup != null) {
            groupName.setText("<html><b>" + curGroup.getDisplayName() + "</b></html>");
            if (curGroup.getSelectedAttributeType() == null) {
                attrType.setText(NOT_AVAILABLE);
            } else {
                attrType.setText("<html><b>" + curGroup.getSelectedAttributeType() + "</b></html>");
                processDistribution();
            }
        } else {
            groupName.setText(NOT_AVAILABLE);
            attrType.setText(NOT_AVAILABLE);
            setEmptyTable();
        }
    }

    private void processDistribution() {

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<String[], Void> worker = new SwingWorker<String[], Void>() {

            @Override
            protected String[] doInBackground() throws Exception {

                DistributionI<Long> dist = curGroup.getDistribution();
                String[] ret = null;
                if (dist.getTotalClassifiedElements() > 0) {
                    ret = new String[stats.length];
                    for (int i = 0; i < stats.length; i++) {
                        ret[i] = stats[i].measure(dist);
                    }
                }
                return ret;
            }

            @Override
            protected void done() {
                super.done();
                String[] indices = null;
                try {
                    indices = get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                if (indices == null || indices.length != stats.length) {
                    setEmptyTable();
                } else {
                    for (int i = 0; i < indices.length; i++) {
                        model.setValueAt(indices[i], i, 1);
                    }
                }
                repaint();
            }

        };
        worker.execute();
    }

    private void setEmptyTable() {
        for (int i = 0; i < stats.length; i++) {
            model.setValueAt(NOT_AVAILABLE, i, 1);
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends GroupI> groups = result.allInstances();
        if (groups.isEmpty()) {
            return;
        }

        GroupI newGroup = null;
        for (GroupI vg : groups) {
            newGroup = vg;
        }

        if (newGroup != null && !newGroup.equals(curGroup)) {
            synchronized (this) {
                if (curGroup != null) {
                    curGroup.removePropertyChangeListener(this);
                }
                curGroup = newGroup;
                curGroup.addPropertyChangeListener(this);
            }
            update();
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case GroupI.VISGROUP_RENAMED:
                groupName.setText(curGroup.getDisplayName());
                break;
            case GroupI.VISGROUP_DEACTIVATED:
                // ignore
                break;
            case GroupI.VISGROUP_HAS_DIST:
            case GroupI.VISGROUP_ATTRTYPE_CHANGED:
            case GroupI.VISGROUP_CHANGED:
                update();
                break;
            case ModelBaseI.OBJECT_DELETED:
                if (evt.getSource() instanceof GroupI) {
                    GroupI vGrp = (GroupI) evt.getSource();
                    if (vGrp != null && vGrp.equals(curGroup)) {
                        synchronized (this) {
                            curGroup.removePropertyChangeListener(this);
                            curGroup = null;
                        }
                    }
                }
                update();
                break;
            default:
                System.err.println("BioDiversityTopComponent got unhandled event " + evt);
        }

    }
}
