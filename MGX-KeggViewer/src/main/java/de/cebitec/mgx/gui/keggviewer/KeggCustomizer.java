package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import java.awt.Cursor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author sj
 */
public class KeggCustomizer extends javax.swing.JPanel {

    /**
     * Creates new form KeggCustomizer
     */
    public KeggCustomizer() {
        initComponents();
    }

    synchronized void restrictPathways(Set<PathwayI> pathways) {
        pathwayList.setEnabled(false);
        pathwayList.removeAllItems();
        if (pathways.size() > 0) {
            for (PathwayI p : pathways) {
                pathwayList.addItem(p);
            }
            pathwayList.setEnabled(true);
        } else {
            pathwayList.addItem(new PathwayI() {

                @Override
                public String getMapNum() {
                    return "";
                }

                @Override
                public String getName() {
                    return "No pathways found";
                }

                @Override
                public int compareTo(PathwayI o) {
                    return -1;
                }
            });
        }
    }

    PathwayI getSelectedPathway() {
        return (PathwayI) pathwayList.getSelectedItem();
    }

    private final static Pattern ecNumber = Pattern.compile("\\d+[.](-|\\d+)[.](-|\\d+)[.](-|\\d+)");

    public Set<PathwayI> selectPathways(final KEGGMaster master, RequestProcessor RP) throws ConflictingJobsException, KEGGException {
        final Set<ECNumberI> ecNumbers = new HashSet<>();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        for (Pair<VisualizationGroupI, DistributionI> p : VGroupManager.getInstance().getDistributions()) {
            DistributionI dist = p.getSecond();
            for (Map.Entry<AttributeI, Number> e : dist.entrySet()) {
                Matcher matcher = ecNumber.matcher(e.getKey().getValue());
                if (matcher.find()) {
                    try {
                        ECNumberI ec = ECNumberFactory.fromString(e.getKey().getValue().substring(matcher.start(), matcher.end()));
                        ecNumbers.add(ec);
                    } catch (KEGGException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        final Set<PathwayI> ret = Collections.synchronizedSet(new HashSet<PathwayI>());
        RequestProcessor.Task task = RP.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ret.addAll(master.Pathways().getMatchingPathways(ecNumbers));
                } catch (KEGGException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        task.waitFinished();
        
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return ret;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        pathwayList = new javax.swing.JComboBox<PathwayI>();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(KeggCustomizer.class, "KeggCustomizer.jLabel1.text")); // NOI18N

        pathwayList.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pathwayList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pathwayList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(250, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox<PathwayI> pathwayList;
    // End of variables declaration//GEN-END:variables
}
