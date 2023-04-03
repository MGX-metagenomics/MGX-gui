package de.cebitec.mgx.gui.mapping.viewer;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.mapping.impl.ViewControllerI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.apache.commons.math3.util.FastMath;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.mapping.viewer//IdentityHistogram//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "IdentityHistogramTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "satellite", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.mapping.viewer.IdentityHistogramTopComponent")
//@ActionReference(path = "Menu/Window", position = 350)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_IdentityHistogramAction",
        preferredID = "IdentityHistogramTopComponent"
)
@Messages({
    "CTL_IdentityHistogramAction=Identity histogram",
    "CTL_IdentityHistogramTopComponent=Mapping identity histogram",
    "HINT_IdentityHistogramTopComponent=Mapping identity histogram"
})
public final class IdentityHistogramTopComponent extends TopComponent implements LookupListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final Lookup lkp;
    private final Lookup.Result<ViewControllerI> res;
    private ViewControllerI controller = null;
    //
    private final XYChart chart;
    private final XChartPanel<XYChart> panel;
    private final static String seriesName = "series_name";
    private final double[] xData;
    private final double[] yData;

    public IdentityHistogramTopComponent() {
        initComponents();
        setName(Bundle.CTL_IdentityHistogramTopComponent());
        setToolTipText(Bundle.HINT_IdentityHistogramTopComponent());

        yData = new double[101]; // 0-100
        for (int i = 0; i < 101; i++) {
            yData[i] = (double) i;
        }
        xData = new double[101];
        Arrays.fill(xData, 0d);

        chart = new XYChart(getWidth(), getHeight());

        chart.addSeries(seriesName, xData, yData);
        chart.getStyler().setLocale(Locale.ENGLISH);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setAxisTickLabelsFont(new Font(Font.DIALOG,
                Font.PLAIN,
                chart.getStyler().getAxisTickLabelsFont().getSize()));
        chart.getStyler().setSeriesColors(new Color[]{Color.BLACK});
        chart.getStyler().setSeriesMarkers(new Marker[]{SeriesMarkers.NONE});

        panel = new XChartPanel<>(chart);
        panel.setBackground(Color.WHITE);
        this.add(panel, BorderLayout.CENTER);

        lkp = Utilities.actionsGlobalContext();
        res = lkp.lookupResult(ViewControllerI.class);
        res.addLookupListener(this);

        resultChanged(null);
    }

    public void setController(ViewControllerI vc) {
        if (controller != null) {
            controller.removePropertyChangeListener(this);
        }
        controller = vc;
        controller.addPropertyChangeListener(this);
        try {
            updateChart(controller.getMappings());
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        switch (pce.getPropertyName()) {
            case ViewControllerI.VIEWCONTROLLER_CLOSED:
                controller.removePropertyChangeListener(this);
                controller = null;
                break;
            case ViewControllerI.BOUNDS_CHANGE:
            case ViewControllerI.MIN_IDENTITY_CHANGE:
                try {
                List<MappedSequenceI> mappings = controller.getMappings();
                updateChart(mappings);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            break;

            default:
                System.out.println("Unhandled event " + pce.getPropertyName() + " in " + getClass().getSimpleName());
                break;
        }
    }

    private void updateChart(List<MappedSequenceI> mappings) {

        Arrays.fill(xData, 0d);

        if (mappings != null) {
            for (MappedSequenceI mseq : mappings) {
                float identity = mseq.getIdentity();
                int idx = FastMath.round(identity);
                xData[idx]++;
            }
        }

        //
        // adjust lower bound to be displayed
        //
        
        // find minimum value where coverage is present
        int minValueIdx = 0;
        while (xData[minValueIdx] == 0d && minValueIdx < 100) {
            minValueIdx++;
        }

        // if possible, include some zeroes on the lower bound
        if (minValueIdx >= 3) {
            minValueIdx -= 3;
        }

        // if no coverage is available at all, display 0 to 100%
        if (minValueIdx == 100) {
            minValueIdx = 0;
        }

        double[] subsetX = Arrays.copyOfRange(xData, minValueIdx, xData.length);
        double[] subsetY = Arrays.copyOfRange(yData, minValueIdx, yData.length);
        
        chart.updateXYSeries(seriesName, subsetX, subsetY, null);
        panel.repaint();
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends ViewControllerI> allInstances = res.allInstances();
        if (!allInstances.isEmpty()) {
            if (controller != null) {
                controller.removePropertyChangeListener(this);
            }
            controller = allInstances.toArray(new ViewControllerI[]{})[0];
            controller.addPropertyChangeListener(this);
            try {
                updateChart(controller.getMappings());
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        Collection<? extends ViewControllerI> allInstances = res.allInstances();
        if (!allInstances.isEmpty()) {
            controller = allInstances.toArray(new ViewControllerI[]{})[0];
            controller.addPropertyChangeListener(this);

            try {
                final List<MappedSequenceI> mappings = controller.getMappings();
                updateChart(mappings);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }

    @Override
    public void componentClosed() {
        if (controller != null) {
            controller.removePropertyChangeListener(this);
            controller = null;
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
