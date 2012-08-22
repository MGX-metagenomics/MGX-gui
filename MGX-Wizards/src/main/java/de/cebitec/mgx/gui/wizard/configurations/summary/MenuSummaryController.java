package de.cebitec.mgx.gui.wizard.configurations.summary;

import de.cebitec.mgx.gui.wizard.configurations.data.impl.ConfigItem;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Node;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.menu.MenuView;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import de.cebitec.mgx.gui.wizard.configurations.utilities.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * Diese Klasse übergibt bei anklicken des Panels alle vom Benutzer eingegebenen
 * Parameter an die Summary View.
 *
 *
 * @author belmann
 */
public class MenuSummaryController
        implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * Damit ein WizardDescriptor sich bei einem Wizard-Panel registrieren kann,
     * schreibt das Interface WizardDescriptor.Panel die beiden Methoden
     * addChangeListener und removeChangeListener vor. Diese Methoden fügen
     * dieser Liste die jeweiligen Listener hinzu.
     */
    private final EventListenerList listeners = new EventListenerList();
    /**
     * Gibt die View der Summary wieder.
     */
    private MenuSummaryView viewComponent;
    /**
     * Der Store, der die Nodes und ConfigItems enthält.
     */
    private Store store;
    /**
     * Anzeige für den Benutzer, wenn True gewählt wurde. Als Ersatz für True,
     * was in die Antwort geschrieben wird.
     */
    private final String RadioYes = "Yes";
    /**
     * Anzeige für den Benutzer, wenn False gewählt wurde. Als Ersatz für False,
     * was in die Antwort geschrieben wird.
     */
    private final String RadioNo = "No";
    /**
     * Wenn der Benutzer nichts gewählt hat, wird in das in der JTable
     * angezeigt.
     */
    private final String Nothing = "------";

    /**
     * Konstruktor für die Übergabe des Stores an die Klassenvariable.
     *
     * @param lStore Store.
     */
    public MenuSummaryController(Store lStore) {
        store = lStore;
        ArrayList<String> nodes = new ArrayList<String>();
        ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();


        Iterator nodeIterator = lStore.getIterator();
        Map.Entry nodeME;
        String nodeId;
        Iterator configItemIterator;
        ArrayList<String> localConfigs;

        while (nodeIterator.hasNext()) {

            nodeME = (Map.Entry) nodeIterator.next();
            nodes.add(((Node) nodeME.getValue()).getDisplayName());
            configItemIterator = ((Node) nodeME.getValue()).getIterator();
            Map.Entry configItemME;

            localConfigs = new ArrayList<String>();
            while (configItemIterator.hasNext()) {

                configItemME = (Map.Entry) configItemIterator.next();
                localConfigs.add(((ConfigItem) configItemME.getValue()).getUserName());
            }
            items.add(localConfigs);
        }
        viewComponent = new MenuSummaryView(nodes, items);
    }

    /**
     * Gibt die Summary View wieder.
     *
     * @return Summary View.
     */
    @Override
    public MenuSummaryView getComponent() {
        return viewComponent;
    }

    /**
     * Gibt die Einstellungen des Hilfe Buttons wieder.
     *
     * @return Hilfe Einstellungen.
     */
    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Liest die eingegebenen Parameter der vorherigen Panels aus. Wandelt die
     * Parameter (nur für die Anzeige) Boolean von True und False in Yes und No,
     * sowie leere Parameter in --------- ;
     *
     * @param lModel Das Model
     */
    @Override
    public void readSettings(WizardDescriptor lWiz) {

        getComponent().removeAll();

        ArrayList<ArrayList<String>> propertys =
                new ArrayList<ArrayList<String>>();

        Iterator nodeIterator = store.getIterator();
        Map.Entry nodeME;
        String nodeId;
        Iterator configItemIterator;

        ArrayList<String> property;
        while (nodeIterator.hasNext()) {


            property = new ArrayList<String>();


            nodeME = (Map.Entry) nodeIterator.next();
            nodeId = (String) nodeME.getKey();
            configItemIterator = ((Node) nodeME.getValue()).getIterator();
            Map.Entry configItemME;

            String localProperty;
            String configItemName;
            String nodeClassName;

            while (configItemIterator.hasNext()) {

                configItemME = (Map.Entry) configItemIterator.next();
                configItemName = (String) configItemME.getKey();
                nodeClassName = store.getNode(nodeId).getClassName();


                localProperty =
                        (String) lWiz.getProperty(
                        nodeId
                        + nodeClassName
                        + configItemName);

                if (localProperty.isEmpty() || lWiz.getProperty(
                        nodeId
                        + nodeClassName
                        + configItemName) == null) {

                    property.add(Nothing);

                } else if (store.getNode(nodeId).getConfigItem(configItemName).
                        getConfigType().equals(Types.Boolean)) {
                    if (localProperty.equals(ActionCommands.RadioFalse)) {
                        property.add(RadioNo);
                    } else if (localProperty.equals(ActionCommands.RadioTrue)) {
                        property.add(RadioYes);
                    }
                } else {
                    property.add(localProperty);
                }
            }
            propertys.add(property);
        }
        getComponent().initView(propertys);
    }

    /**
     * Übergibt die vom Benutzer eingegebenen Parameter an das Model.
     *
     *
     * @param lModel Das Model
     */
    @Override
    public void storeSettings(WizardDescriptor settings) {
    }

    /**
     * Gibt zurück, ob alle Parameter richtig eingegeben wurden und ob der
     * Benutzer dann auf "Finish" klicken darf.
     *
     * @return Korrekte Konfiguration oder nicht.
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Fügt einen Listener hinzu.
     *
     * @param l ChangeListener
     */
    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    /**
     * Entfernt einen Listener.
     *
     * @param l ChangeListener
     */
    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
