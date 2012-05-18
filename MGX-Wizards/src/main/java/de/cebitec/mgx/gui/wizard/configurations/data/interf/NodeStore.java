/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.data.interf;

import de.cebitec.mgx.gui.wizard.configurations.data.Impl.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Interface für den NodeStore, der zunächst im Parser gefüllt wird, in der View
 * bearbeitet wird und danach durch das Loolup weiter an andere gegeben werden
 * kann.
 *
 * @author belmann
 */
public interface NodeStore {

   /**
    * Fügt einen weiteren Knoten hinzu.
    *
    * @param lNode
    */
   public void addNode(Node lNode);


   
   /**
    * Gibt ein Node wieder.
    * 
    * @param lId Id des Nodes.
    * @return Node
    */
   public Node getNode(String lId);
   
   
   /**
    * Gibt einen Iterator wieder, um über alle Nodes iterieren zu können.
    * @return Iterator
    */
   public Iterator getIterator();
   
   /**
    * Entfernt alle Knoten, bei denen keine Antwort gesetzt wurde.
    */
   public void deleteEmptyNodes();
   
   /**
    * Gibt alle Antworten wieder.
    * @return Antworten
    */
   public HashMap<String,HashMap<String,String>> getAllAnswers();
   
   /**
    * Gibt die Anzahl der bisherigen Knoten im Store wieder.
    *
    * @return Anzahl Knoten
    */
   public int storeSize();

   /**
    * Entfernt einen Knoten aus dem Store anhand der Id.
    *
    * @param lNode
    */
   public void removeNode(String lId);

   /**
    * Entfernt alle Knoten aus dem Store.
    */
   public void removeAllNodes();

}
