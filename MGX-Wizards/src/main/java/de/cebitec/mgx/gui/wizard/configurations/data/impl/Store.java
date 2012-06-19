
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.data.impl;

//~--- non-JDK imports --------------------------------------------------------
import java.util.*;

/**
 * Verwaltet die Nodes einer vom User möglichen Auswahl an Tools.
 *
 *
 * @author belmann
 */
public class Store  {

   /**
    * Speichert die Nodes bzw. Tools in einer HashMap. Dabei stellen die id den
    * key dar.
    */
   private Map<String, Node> nodes;

   /**
    * Der Konstruktor initialisiert die HashMap, die die einzelnen Knoten
    * verwaltet.
    */
   public Store() {
	nodes = new TreeMap<String, Node>();
   }

    /**
    * Gibt einen Iterator wieder, um über alle Nodes iterieren zu können.
    * @return Iterator
    */
   public Iterator getIterator() {
	Set set = nodes.entrySet();
	return set.iterator();
   }

   /**
    * Gibt ein Node wieder.
    * 
    * @param lId Id des Nodes.
    * @return Node
    */
   public final Node getNode(String lId) {
	return nodes.get(lId);
   }

   /**
    * Fügt einen weiteren Knoten hinzu.
    *
    * @param node
    */
   public final void addNode(Node node) {
	nodes.put(node.getId(), node);
   }

   /**
    * Gibt die Anzahl der bisherigen Knoten im Store wieder.
    *
    * @return Anzahl Knoten.
    */
   public int storeSize() {
	return nodes.size();
   }

   /**
    * Entfernt alle Knoten, bei denen keine Antwort gesetzt wurde.
    */
   public void deleteEmptyNodes() {

	for (String id : new ArrayList<String>(nodes.keySet())) {
	   nodes.get(id).deleteEmptyConfigItems();
	   if (nodes.get(id).getNumberOfConfigItems() == 0) {
		nodes.remove(id);
	   }
	}
   }

   /**
    * Entfernt einen Knoten aus dem Store.
    *
    * @param id
    */
   public void removeNode(String id) {
	nodes.remove(id);
   }

   /**
    * Entfernt alle Nodes aus dem Store.
    */
   public void removeAllNodes() {

	nodes.clear();

   }

   /**
    * Gibt alle Antworten in einer HashMap wieder.
    * Als Key, dient der Name des ConfigItems.
    * @return ConfigItem
    */
   public HashMap<String, HashMap<String, String>> getAllAnswers() {
	deleteEmptyNodes();

	HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();

	for (String key : nodes.keySet()) {
	   map.put(key, nodes.get(key).getAnswers());
	}
	return map;
   }
}


//~ Formatted by Jindent --- http://www.jindent.com
