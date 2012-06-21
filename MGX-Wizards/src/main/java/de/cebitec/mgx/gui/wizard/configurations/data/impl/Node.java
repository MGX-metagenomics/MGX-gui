package de.cebitec.mgx.gui.wizard.configurations.data.impl;

//~--- JDK imports ------------------------------------------------------------
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 * Ein Node repräsentiert eine sachliche Einheit, die ein oder mehrere Felder
 * (ConfigItems) beinhaltet, in denen der User eine Auswahl bzw Antwort geben
 * kann.
 *
 *
 * @author belmann
 */
public class Node {

    /**
     * Der Klassenname eines Knotens.
     *
     */
    private String className;
    /**
     * Da die ConfigItems alle eindeutig sind für einen Node, werden diese in
     * einer Map abgespeichert. Der Schlüssel ist hierbei der ConfigName.
     *
     */
    private TreeMap<String, ConfigItem> configItems;
    /**
     * Speichert den Namen des Nodes, der Angezeigt werden soll.
     */
    private String displayName;
    /**
     * Speichert die Id des Knotens.
     *
     */
    private String id;

    /**
     * Der Konstruktor teilt den Klassennamen als auch die Id des Knotens den
     * Klassenvariablen zu und initialisiert die Map mit den ConfigItems.
     *
     *
     * @param lClassName Der Klassenname des Nodes.
     * @param lId Die Id des Nodes.
     */
    public Node(String lClassName, String lId) {
        id = lId;
        className = lClassName;
        configItems = new TreeMap<>();
    }

    /**
     * Gibt die Anzahl der ConfigItems des Nodes an.
     *
     * @return Anzahl an ConfigItems.
     */
    public int getNumberOfConfigItems() {
        return configItems.size();
    }

    /**
     * Gibt den Klassen des Nodes wieder.
     *
     * @return Klassenname des Nodes.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Überprüft, ob ein ConfigItem enthalten ist in der Map.
     *
     * @param lConfigName
     * @return Enthalten oder nicht.
     */
    public boolean containsConfigItem(String lConfigName) {
        return configItems.containsKey(lConfigName);

    }

    /**
     * Gibt das ConfigItem wieder.
     *
     * @param lConfigName Der Name des ConfigItems.
     * @return ConfigItem.
     */
    public ConfigItem getConfigItem(String lConfigName) {
        return configItems.get(lConfigName);
    }

    /**
     * Gibt die Antworten in einer Map zurück. Der Schlüssel, ist dabei der
     * eindeutige Name des ConfigItems. ConfigItems, dessen Antwort leer ist,
     * oder nicht gesetzt wurde, werden gelöscht.
     *
     * @return HashMap<String,String> mit allen Antworten.
     *
     *
     */
    public HashMap<String, String> getAnswers() {

        deleteEmptyConfigItems();

        HashMap<String, String> map = new HashMap<>();
        for (String key : configItems.keySet()) {
            map.put(key, configItems.get(key).getAnswer());
        }

        return map;
    }

    /**
     * Gibt einen Iterator wieder, um über alle ConfigItems iterieren zu können.
     *
     * @return Iterator
     */
    public Iterator<Entry<String, ConfigItem>> getIterator() {
        return configItems.entrySet().iterator();
    }

    /**
     * Entfernt alle ConfigItems, die noch keine Antwort gesetzt bekommen haben.
     */
    protected void deleteEmptyConfigItems() {
        ArrayList<String> list = new ArrayList<String>(configItems.keySet());
        for (String configName : list) {
            if (!configItems.get(configName).isAnswerSet()) {
                configItems.remove(configName);

            }
        }
    }

    /**
     * Fügt ein ConfigItem zu dem Node hinzu.
     *
     *
     * @param lConfigItem
     */
    public void addConfigItem(ConfigItem lConfigItem) {
        configItems.put(lConfigItem.getConfigName(), lConfigItem);
    }

    /**
     * Gibt den Namen des Nodes, der angezeigt werden soll wieder.
     *
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Setzt den Namen des Nodes, der angezeigt werden soll.
     *
     * @param lDisplayName displayName
     */
    public void setDisplayName(String lDisplayName) {
        this.displayName = lDisplayName;
    }

    /**
     * Entfernt ein ConfigItem vom Node.
     *
     * @param lConfigName
     */
    public void removeConfigItem(String lConfigName) {
        configItems.remove(lConfigName);
    }

    /**
     * Gibt die eindeutige ID des Nodes wieder.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }
}