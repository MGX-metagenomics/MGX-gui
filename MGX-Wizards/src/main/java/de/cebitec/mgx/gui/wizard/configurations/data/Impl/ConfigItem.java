
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.data.Impl;

import java.util.Comparator;
import java.util.logging.Logger;

/**
 * Ein ConfigItem repräsentiert ein Feld, in dem ein Benutzer eine Auswahl
 * treffen kann.
 *
 *
 * @author belmann
 *
 */
public class ConfigItem implements Comparator<ConfigItem>, Comparable<ConfigItem> {

   /**
    * Setzt den String für die Antwort.
    *
    */
   private String answer;
   /**
    * Setzt die möglichen Auswahlwerte wenn eine Auswahl möglich ist.
    *
    */
   private Choices choice;
   /**
    * Speichert den Namen des ConfigItems.
    */
   private String configName;
   /**
    * Speichert den Typ des ConfigItems. Der dem Namen in der plugin.xml
    * gleicht.
    *
    */
   private String configType;
   /**
    * Speichert den default Wert des ConfigItems.
    *
    */
   private String defaultValue;
   /**
    *
    * Speichert die Beschreibung des ConfigItems.
    *
    */
   private String userDescription;
   /**
    * Speichert, ob das ConfigItem optional ist oder nicht.
    */
   private Boolean optional;
   /**
    * Speichert den vom User vergebenen Namen des ConfigItems.
    */
   private String userName;

   /**
    * Der Konstruktor speichert den vom User vergebenen Namen, die Beschreibung
    * sowie den Namen des ConfigItems.
    *
    *
    * @param lUserName Der vom User vergebene Name.
    * @param lUserDescription Die vom User vergebene Beschreibung.
    * @param lConfigName Der Name des ConfigItems.
    */
   public ConfigItem(String lUserName, String lUserDescription,
	 String lConfigName) {
	userName = lUserName;
	userDescription = lUserDescription;
	configName = lConfigName;
	optional = false;
	choice = new Choices();
	answer = null;
   }

   /**
    * Getter für den Namen des Configs. (Nicht der vom User vergebene Name.)
    *
    * @return Name des Configs.
    */
   public String getConfigName() {
	return configName;
   }

   /**
    * Der Typ des ConfigItems.
    *
    * @return Der Typ des ConfigItems.
    */
   public String getConfigType() {
	return configType;
   }

   /**
    * Die vom User vergebene Beschreibung des ConfigItems.
    *
    * @return Die User Beschreibung des ConfigItems.
    */
   public String getUserDescription() {
	return userDescription;
   }

   /**
    * Abfrage, ob das ConfigItem optional ist oder nicht.
    *
    * @return Optional
    */
   public Boolean isOptional() {
	return optional;
   }

   /**
    * Gibt den vom User vergebenen Namen für das ConfigItem wieder.
    *
    * @return den vom User vergebenen Namen.
    */
   public String getUserName() {
	return userName;
   }

   /**
    * Gibt bei Enumerations die Auflistung der einzelnen Objekte wieder, falls
    * diese für das ConfigItem überhaupt gesetzt sind.
    *
    * @return Auflistung von Objekten.
    */
   public Choices getChoice() {
	return choice;
   }

   /**
    * Überprüft ob die Antwort gesetzt ist oder nicht. Gibt auch false wieder,
    * wenn der String leer ist.
    *
    * @return Antwort gesetzt oder nicht.
    */
   public boolean isAnswerSet() {
	if (answer == null || answer.isEmpty()) {
	   return false;
	} else {
	   return true;
	}
   }

   /**
    * Setzt die Antwort.
    *
    *
    * @param lAnswer Die Antwort des Users.
    */
   public void setAnswer(String lAnswer) {
	if (lAnswer.trim().isEmpty() || lAnswer == null) {
	   answer = null;
	} else {
	   this.answer = lAnswer;
	}
   }

   /**
    * Gibt den Antwortstring wieder.
    *
    * @return Antwort.
    */
   public String getAnswer() {
	return answer;
   }

   /**
    * Setzt die mögliche Liste für eine Auswahl bei einer ComboBox.
    *
    *
    * @param choice Liste an Auswahlobjekten.
    */
   public void setChoice(Choices lChoice) {
	choice = lChoice;
   }

   /**
    * Gibt true wieder, falls eine Auswahl an Elementen für eine comboBox
    * vorhanden ist.
    *
    * @return
    */
   public boolean hasChoices() {
	if (choice.isChoiceEmpty()) {
	   return false;
	} else {
	   return true;
	}
   }

   /**
    * Setzt den Typen für ein ConfigItem.
    *
    * @param lConfigType Der Typ des ConfigItems.
    */
   public void setConfigType(String lConfigType) {
	this.configType = lConfigType;
   }

   /**
    * Setzt die Beschreibung des Benutzers für den ConfigItem.
    *
    * @param lUserDescription
    */
   public void setUserDescription(String lUserDescription) {
	this.userDescription = lUserDescription;
   }

   /**
    * Setzt den Wert, ob das ConfigItem optional ist oder nicht.
    *
    * @param lOptional optional
    */
   public void setOptional(Boolean lOptional) {
	this.optional = lOptional;
   }

   /**
    * Getter für den default wert. Gibt null wieder, wenn dieser nicht gesetzt
    * ist.
    *
    * @return defaultValue
    */
   public String getDefaultValue() {
	return defaultValue;
   }

   /**
    * Gibt an, ob ein default Wert für dieses ConfigItem existiert oder nicht.
    *
    * @return Default Wert gesetzt oder nicht.
    */
   public boolean hasDefaultValue() {
	if (defaultValue == null) {
	   return false;
	} else {
	   return true;
	}
   }

   /**
    * Setzt den default Wert für das ConfigItem.
    *
    * @param lDefaultValue the defaultValue to set
    */
   public void setDefaultValue(String lDefaultValue) {
	this.defaultValue = lDefaultValue;
   }

   /**
    * Vergleich von den optional Werten des ConfigItems. Dient zur Sortierung
    * der ConfigItems.
    *
    * @param o1 ConfigItem 1
    * @param o2 ConfigItem 2
    * @return ConfgItem 1 ist größer(1), kleiner(-1) oder gleich(0).
    */
   @Override
   public int compare(ConfigItem o1, ConfigItem o2) {

	if (o1.optional == o2.optional) {

	   return 0;
	} else if (o1.optional == true) {

	   return -1;
	} else {

	   return 1;

	}


   }

   /**
    * Vergleicht ein ConfigItem zu dem in dieser Klasse.
    *
    * @param o ConfigItem
    * @return ConfgItem 1 ist größer(1), kleiner(-1) oder gleich(0).
    */
   @Override
   public int compareTo(ConfigItem o) {

	return optional.compareTo(o.optional);

   }
}


//~ Formatted by Jindent --- http://www.jindent.com
