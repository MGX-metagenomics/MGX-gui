package de.cebitec.mgx.gui.wizard.sample;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public final class SampleVisualPanel2 extends JPanel implements DocumentListener {

    public static final String PROP_MATERIAL = "material";
    public static final String PROP_TEMPERATURE = "temperature";
    public static final String PROP_VOLUME = "volume";
    public static final String PROP_VOLUME_UNIT = "volume_unit";
    //
    private final static String[] temps = { "°F", "°C", "K" };
    private final static String[] vols = { "ml", "l", "g", "kg" };

    /** Creates new form SampleVisualPanel2 */
    public SampleVisualPanel2() {
        initComponents();
        material.getDocument().addDocumentListener(this);
        temperature.getDocument().addDocumentListener(this);
        volume.getDocument().addDocumentListener(this);
        tempUnit.setModel(new javax.swing.DefaultComboBoxModel<>(temps));
        tempUnit.setEditable(false);
        tempUnit.setSelectedItem("K");
        volUnit.setModel(new javax.swing.DefaultComboBoxModel<>(vols));
        volUnit.setEditable(false);
        volUnit.setSelectedItem("ml");
    }

    public String getMaterial() {
        return material.getText();
    }

    public void setMaterial(String m) {
        material.setText(m);
    }

    public String getTemperature() {
        return temperature.getText();
    }
    
    public String getTemperatureUnit() {
        return (String) tempUnit.getSelectedItem();
    }
    
    public void setTemperature(Double t) {
        // input is always in Kelvin
        temperature.setText(t.toString());
        tempUnit.setSelectedItem("K");
    }

    public String getVolume() {
        return volume.getText();
    }
    
    public void setVolume(Integer vol) {
        volume.setText(vol.toString());
    }

    public String getVolumeUnit() {
        return (String) volUnit.getSelectedItem();
    }
    
    public void setVolumeUnit(String vu) {
        volUnit.setSelectedItem(vu);
    }

    @Override
    public String getName() {
        return "Sample description";
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        material = new javax.swing.JTextField();
        temperature = new javax.swing.JTextField();
        volume = new javax.swing.JTextField();
        tempUnit = new javax.swing.JComboBox<String>();
        volUnit = new javax.swing.JComboBox<String>();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SampleVisualPanel2.class, "SampleVisualPanel2.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SampleVisualPanel2.class, "SampleVisualPanel2.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SampleVisualPanel2.class, "SampleVisualPanel2.jLabel3.text")); // NOI18N

        material.setText(org.openide.util.NbBundle.getMessage(SampleVisualPanel2.class, "SampleVisualPanel2.material.text")); // NOI18N

        temperature.setText(org.openide.util.NbBundle.getMessage(SampleVisualPanel2.class, "SampleVisualPanel2.temperature.text")); // NOI18N

        volume.setText(org.openide.util.NbBundle.getMessage(SampleVisualPanel2.class, "SampleVisualPanel2.volume.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(material, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(volume)
                            .addComponent(temperature, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tempUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(volUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(material, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(temperature, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tempUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(volume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(volUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField material;
    private javax.swing.JComboBox<String> tempUnit;
    private javax.swing.JTextField temperature;
    private javax.swing.JComboBox<String> volUnit;
    private javax.swing.JTextField volume;
    // End of variables declaration//GEN-END:variables

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
        if (material.getDocument() == d) {
            firePropertyChange(PROP_MATERIAL, 0, 1);
        } else if (temperature.getDocument() == d) {
            firePropertyChange(PROP_TEMPERATURE, 0, 1);
        } else if (volume.getDocument() == d) {
            firePropertyChange(PROP_VOLUME, 0, 1);
        }
    }
}
