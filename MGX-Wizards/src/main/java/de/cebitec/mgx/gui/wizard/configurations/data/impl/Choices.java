
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.data.impl;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *Bei Choices, kann der User die einzelnen Elemente über eine ComboBox auswählen.
 * 
 * 
 * @author belmann
 */
public class Choices  {
    
   
   /**
    * Hier sind die einzelnen Elemente gespeichert,
    * dabei dient der choice Name als key und die Beschreibung als value.
    */
   private LinkedHashMap<String,String> sample;
   

   /**
    * Initialisiert die Datenstruktur für die Namen und Beschreibungen.
    */
    public Choices() {
	 sample = new LinkedHashMap<String, String>();
    }
   /**
    * Uebergibt dem konstruktor die Choices.
    */
    public Choices(Map<String, String> choices) {
	 sample = (LinkedHashMap<String, String>) choices;
    }

 
    /**
     * Fügt ein Element hinzu.
     * @param value Name
     * @param description Beschreibung 
     */
    public void addItem(String value, String description) {
    	sample.put(value, description);
    }

    /**
     * Überprüft, ob eine Auswahl vorhanden ist oder nicht.
     * @return Auswahl vorhanden oder nicht
     */
    public boolean isChoiceEmpty() {
        return sample.isEmpty();
    }

   /**
    * Gibt die Auswahl für das ConfigItem wieder.
    * @return LinkedHashMap, wobei der Schlüssel, das Value darstellt.
    */
   public LinkedHashMap<String,String> getChoices() {
	return sample;
   }
}


//~ Formatted by Jindent --- http://www.jindent.com
