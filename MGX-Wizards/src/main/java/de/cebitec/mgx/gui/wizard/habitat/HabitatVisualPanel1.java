package de.cebitec.mgx.gui.wizard.habitat;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serial;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapKit.DefaultProviders;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public final class HabitatVisualPanel1 extends JPanel implements DocumentListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public static final String PROP_NAME = "name";
    public static final String PROP_BIOME = "biome";
    public static final String PROP_LATITUDE = "latitude";
    public static final String PROP_LONGITUDE = "longitude";
    private static final int MAX_LOCATION_RESULTS = 10;
    private Location[] foundLocations;
    private Set<Waypoint> waypoints;
    private Double latitude = null;
    private Double longitude = null;
    private final static GeoPosition GP_BIELEFELD = new GeoPosition(52.03697, 8.49406);

    /**
     * Creates new form HabitatVisualPanel1
     */
    public HabitatVisualPanel1() {
        initComponents();
        initMapKit();
        createSearchListListener();
        habitatname.getDocument().addDocumentListener(this);
        biomename.getDocument().addDocumentListener(this);
        gpslocation.getDocument().addDocumentListener(this);
        //gpslocation.setEditable(false);
    }

    @Override
    public String getName() {
        return "Habitat location";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchLocation = new javax.swing.JTextField();
        jXMapKit1 = new org.jxmapviewer.JXMapKit();
        jScrollPane2 = new javax.swing.JScrollPane();
        foundLocationList = new javax.swing.JList<String>();
        jButton_searchLoc = new javax.swing.JButton();
        jLabel_foundLocations = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        habitatname = new javax.swing.JTextField();
        biomename = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        gpslocation = new javax.swing.JTextField();

        searchLocation.setText(org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "habitatVisualPanel1.jTextField_searchLoc.text")); // NOI18N
        searchLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchLocationActionPerformed(evt);
            }
        });

        jXMapKit1.setDefaultProvider(org.jxmapviewer.JXMapKit.DefaultProviders.OpenStreetMaps);
        jXMapKit1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jXMapKit1PropertyChange(evt);
            }
        });

        foundLocationList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(foundLocationList);

        org.openide.awt.Mnemonics.setLocalizedText(jButton_searchLoc, org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "habitatVisualPanel1.jButton_searchLoc.text")); // NOI18N
        jButton_searchLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_searchLocActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel_foundLocations, org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "habitatVisualPanel1.jLabel_foundLocations.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "HabitatVisualPanel1.jLabel1.text_1")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "HabitatVisualPanel1.jLabel2.text_1")); // NOI18N

        habitatname.setText(org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "HabitatVisualPanel1.habitatname.text")); // NOI18N

        biomename.setText(org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "HabitatVisualPanel1.biomename.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "HabitatVisualPanel1.jLabel3.text")); // NOI18N

        gpslocation.setEditable(false);
        gpslocation.setText(org.openide.util.NbBundle.getMessage(HabitatVisualPanel1.class, "HabitatVisualPanel1.gpslocation.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(biomename, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                            .addComponent(habitatname, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
                        .addGap(27, 27, 27))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jXMapKit1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(gpslocation)
                    .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_searchLoc))
                    .addComponent(jLabel_foundLocations)
                    .addComponent(jLabel3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(habitatname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(biomename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_searchLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel_foundLocations)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(gpslocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jXMapKit1, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField biomename;
    private javax.swing.JList<String> foundLocationList;
    private javax.swing.JTextField gpslocation;
    private javax.swing.JTextField habitatname;
    private javax.swing.JButton jButton_searchLoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_foundLocations;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jxmapviewer.JXMapKit jXMapKit1;
    private javax.swing.JTextField searchLocation;
    // End of variables declaration//GEN-END:variables

    public void setHabitatName(String name) {
        habitatname.setText(name);
    }

    public String getHabitatName() {
        return habitatname.getText();
    }

    public void setBiome(String biome) {
        biomename.setText(biome);
    }

    public String getBiome() {
        return biomename.getText();
    }

    public void setGPSLongitude(double longitude) {
        this.longitude = longitude;
        rebuildGPSLocation();
    }

    public Double getGPSLongitude() {
        return longitude;
    }

    public void setGPSLatitude(double latitude) {
        this.latitude = latitude;
        rebuildGPSLocation();
    }

    public Double getGPSLatitude() {
        return latitude;
    }

    private void initMapKit() {
        final JXMapKit kit = jXMapKit1;

        //
        // remove current set of mouse listeners from both main and mini map
        //
        for (MouseListener mouseListener : kit.getMainMap().getMouseListeners()) {
            kit.removeMouseListener(mouseListener);
        }
        for (MouseMotionListener mouseMotionListener : kit.getMainMap().getMouseMotionListeners()) {
            kit.removeMouseMotionListener(mouseMotionListener);
        }
        for (MouseListener mouseListener : kit.getMiniMap().getMouseListeners()) {
            kit.removeMouseListener(mouseListener);
        }
        for (MouseMotionListener mouseMotionListener : kit.getMiniMap().getMouseMotionListeners()) {
            kit.removeMouseMotionListener(mouseMotionListener);
        }

        kit.setDefaultProvider(DefaultProviders.OpenStreetMaps);
        kit.getMainMap().addPropertyChangeListener("centerPosition", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                latitude = ((GeoPosition) evt.getNewValue()).getLatitude();
                longitude = ((GeoPosition) evt.getNewValue()).getLongitude();
                rebuildGPSLocation();
            }
        });
        final int max = 17;
        TileFactoryInfo info = new TileFactoryInfo(1, max - 2, max,
                256, true, true, // tile size is 256 and x/y orientation is normal
                "http://tile.openstreetmap.org",//5/15/10.png",
                "x", "y", "z") {

            @Override
            public String getTileUrl(int x, int y, int zoom) {
                zoom = max - zoom;
                String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
                return url;
            }
        };
        TileFactory tf = new DefaultTileFactory(info);
        kit.setTileFactory(tf);
        kit.getMainMap().setZoom(15);
        kit.getMiniMap().setZoom(10);
        kit.setAddressLocation(new GeoPosition(51.5, 0));
        kit.getMainMap().setDrawTileBorders(false);
        kit.getMainMap().setRestrictOutsidePanning(true);
        //kit.getMainMap().setRecenterOnClickEnabled(true);
        kit.setAddressLocationShown(true);
        ((DefaultTileFactory) kit.getMainMap().getTileFactory()).setThreadPoolSize(8);
        waypoints = new HashSet<>();
        waypoints.add(new WaypointImpl(kit.getMainMap().getCenterPosition()));
        final WaypointPainter<Waypoint> painter = new WaypointPainter<>();
        painter.setWaypoints(waypoints);
        kit.getMainMap().setOverlayPainter(painter);
        waypoints.clear();
        kit.getMainMap().addPropertyChangeListener("centerPosition", new PropertyChangeListener() {
            //listener to redraw waypoint in middle and to listen for invalid positions (bug in mapviwer)

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //bugfix to get no invalid data
                double lon = kit.getMainMap().getCenterPosition().getLongitude();
                if (lon > 180) {
                    lon = lon - 360;
                } else if (lon < -180) {
                    lon = lon + 360;
                }
                kit.setCenterPosition(new GeoPosition(((GeoPosition) evt.getNewValue()).getLatitude(), lon));
                kit.getMainMap().setCenterPosition(kit.getCenterPosition());
                Point2D pt = kit.getMiniMap().getTileFactory().geoToPixel(kit.getMainMap().getCenterPosition(), kit.getMiniMap().getZoom());
                waypoints.clear();
                waypoints.add(new WaypointImpl(kit.getMainMap().getCenterPosition()));
                painter.setWaypoints(waypoints);
                kit.getMiniMap().setCenter(pt);
                kit.getMiniMap().repaint();
            }
        });
        GeoPosition gp;
        if (latitude == null || longitude == null) {
            gp = GP_BIELEFELD; // Bielefeld University
        } else {
            gp = new GeoPosition(latitude, longitude);
        }
        kit.getMainMap().setCenterPosition(gp);
        waypoints.add(new WaypointImpl(kit.getMainMap().getCenterPosition()));
    }

    //search by name for geolocation and put them into selectionList
    private void searchLocation(String loc) {
        try {
            // FIXME - registration required!
            URL url = new URL("http://api.geonames.org/search?q=" + loc + "&featureClass=P&maxRows=" + MAX_LOCATION_RESULTS + "&username=habitatwizard");
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList list = (NodeList) xpath.evaluate("//geoname",
                    new InputSource(url.openStream()),
                    XPathConstants.NODESET);
            final String[] strings = new String[list.getLength()];
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                String title = (String) xpath.evaluate("name/text()",
                        node, XPathConstants.STRING);
                String country = (String) xpath.evaluate("countryCode/text()",
                        node, XPathConstants.STRING);
                Double lat = (Double) xpath.evaluate("lat/text()",
                        node, XPathConstants.NUMBER);
                Double lon = (Double) xpath.evaluate("lng/text()",
                        node, XPathConstants.NUMBER);
                strings[i] = title + "/" + country;
                foundLocations[i] = new Location(new GeoPosition(lat, lon), title + "/" + country);
            }

            // update result list
            foundLocationList.setModel(new javax.swing.AbstractListModel<String>() {

                @Override
                public int getSize() {
                    return strings.length;
                }

                @Override
                public String getElementAt(int i) {
                    return strings[i];
                }
            });

        } catch (IOException | XPathExpressionException ex) {
        }
    }

    // listener on selectionList to change mapcenter and zoomlevel
    private void createSearchListListener() {
        foundLocations = new Location[MAX_LOCATION_RESULTS];
        foundLocationList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = ((JList) e.getSource()).getSelectedIndex();
                if (index >= 0 && index < foundLocations.length) {
                    jXMapKit1.setCenterPosition(foundLocations[index].getGeoLoc());
                    jXMapKit1.setZoom(8);
                }
            }
        });
    }

    private void jXMapKit1PropertyChange(java.beans.PropertyChangeEvent evt) {
    }

    private void jButton_searchLocActionPerformed(java.awt.event.ActionEvent evt) {
        searchLocation(searchLocation.getText());
    }

    private void searchLocationActionPerformed(java.awt.event.ActionEvent evt) {
        searchLocation(searchLocation.getText());
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
        if (habitatname.getDocument() == d) {
            firePropertyChange(PROP_NAME, 0, 1);
        } else if (biomename.getDocument() == d) {
            firePropertyChange(PROP_BIOME, 0, 1);
        } else if (gpslocation.getDocument() == d) {
            firePropertyChange(PROP_LATITUDE, 0, 1);
            firePropertyChange(PROP_LONGITUDE, 0, 1);
        }
    }

    private void rebuildGPSLocation() {
        if (latitude != null && longitude != null) {
            DecimalFormat f = new DecimalFormat("#0.00000");
            String gpsloc = new StringBuilder(f.format(latitude)).append(" / ").append(f.format(longitude)).toString();
            gpslocation.setText(gpsloc);
            GeoPosition geoPosition = new GeoPosition(latitude, longitude);
            jXMapKit1.setAddressLocation(geoPosition);
            jXMapKit1.setCenterPosition(geoPosition);
            if (waypoints != null) {
                waypoints.clear();
                Waypoint ww = new WaypointImpl(geoPosition);
                waypoints.add(ww);
            }
        }
    }

    private final class WaypointImpl implements Waypoint {

        private final GeoPosition pos;

        public WaypointImpl(GeoPosition pos) {
            this.pos = pos;
        }

        @Override
        public GeoPosition getPosition() {
            return pos;
        }

    }
}
