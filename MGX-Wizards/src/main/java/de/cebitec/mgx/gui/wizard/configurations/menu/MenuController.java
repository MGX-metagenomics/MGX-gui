
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.menu;

//~--- non-JDK imports --------------------------------------------------------
import de.cebitec.mgx.gui.wizard.configurations.data.Impl.ConfigItem;
import de.cebitec.mgx.gui.wizard.configurations.data.Impl.Node;
import de.cebitec.mgx.gui.wizard.configurations.messages.Messages;
import de.cebitec.mgx.gui.wizard.configurations.utilities.ActionCommands;
import de.cebitec.mgx.gui.wizard.configurations.utilities.Types;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

//~--- JDK imports ------------------------------------------------------------

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.math.BigInteger;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Das Wizard Panel dienst als Controller, der zwischen den einzelnen
 * Models(WizardDescriptor) und den Views (WizardView) vermittelt.
 *
 * @author belmann
 */
public class MenuController
    implements WizardDescriptor.Panel<WizardDescriptor>,
    PropertyChangeListener, ActionListener {

   private final static Logger LOGGER =
	 Logger.getLogger(MenuController.class.getName());
   private WizardDescriptor model = null;
   /**
    * Damit ein WizardDescriptor sich bei einem Wizard-Panel registrieren kann,
    * schreibt das Interface WizardDescriptor.Panel die beiden Methoden
    * addChangeListener und removeChangeListener vor. Diese Methoden fügen
    * dieser Liste die jeweiligen Listener hinzu.
    */
   private final EventListenerList listeners = new EventListenerList();
   /**
    * Flag wird gesetzt um zu bestimmen, ob der Benutzer auf "Next" klicken
    * darf.
    */
   private boolean isValid = false;
   /**
    * Hier wird der Klassenname abgespeichert.
    */
   private String className;
   /**
    * viewComponent stellt die View dar.
    */
   private MenuView menuView;
   /**
    * DefaultValues enthalten die default Werte eines Nodes.
    */
   private ArrayList<String> defaultValues;
   /**
    * Die Id des jeweiligen Nodes wird hier zwischengespeichert.
    */
   private String id;
   /**
    * ConfigItems eines Nodes.
    */
   private ArrayList<ConfigItem> configItems;
   /**
    * Gibt an welche Nummer dieser Node beherbergt.
    */
   private int nodeCounter;
   /**
    * Gibt an, ob überhaupt welche Default Werte existieren.
    */
   private boolean hasDefault;

   /**
    * ActionListener für den DefaultButton, der alle möglichen Defaults Setzt.
    * 
    */
   private ActionListener listenerbutton;
   
   /**
    * JButton um alle möglichen Default Werte für einen Node zu setzen.
    */
   private JButton allDefaultButton;
   
   /**
    * Der Konstruktor bereitet die einzelnen Parameter der MenuView vor.
    * Außerdem findet hier die Initialisation der View statt.
    *
    * @param lNodeCounter Nodenummer.
    * @param lNode Node
    */
   public MenuController(int lNodeCounter, Node lNode) {
	id = lNode.getId();
	className = lNode.getClassName();
	configItems = new ArrayList<ConfigItem>();
	defaultValues = new ArrayList<String>();
	nodeCounter = lNodeCounter;

	String displayName = lNode.getDisplayName();
	ArrayList<String> userNames = new ArrayList<String>();
	ArrayList<String> configTypes = new ArrayList<String>();
	ArrayList<String> userDescriptions = new ArrayList<String>();
	int mandatoryComponents = 0;
	ArrayList<ArrayList<String>> userChoicesDescriptions = null;
	ArrayList<ArrayList<String>> userChoicesValues = null;

	Iterator iterator =
	    lNode.getIterator();
	Map.Entry me;
	while (iterator.hasNext()) {
	   me = (Map.Entry) iterator.next();
	   configItems.add((ConfigItem) me.getValue());
	}

	hasDefault = false;

	
	
	for (ConfigItem item : configItems) {
	   configTypes.add(item.getConfigType());
	   userNames.add(item.getUserName());
	   userDescriptions.add(item.getUserDescription());
	   defaultValues.add(item.getDefaultValue());
	   
	   
	   

	   if (!item.isOptional()) {
		mandatoryComponents++;
	   }

	   if (item.hasChoices()) {
		if (userChoicesValues == null) {
		   userChoicesValues = new ArrayList<ArrayList<String>>();
		}

		if (userChoicesDescriptions == null) {
		   userChoicesDescriptions =
			 new ArrayList<ArrayList<String>>();
		}

		userChoicesValues.add(new ArrayList<String>(item.getChoice().getChoices().keySet()));
		userChoicesDescriptions.add(new ArrayList<String>(item.getChoice().getChoices().values()));
	   }
	}

	for(int i = 0; i<defaultValues.size();i++){
	   
	   if (defaultValues.get(i)!=null) {
		hasDefault = true;
	   }
	   
	}
	
	menuView = new MenuView(nodeCounter, displayName, userNames,
	    userDescriptions, configTypes,
	    userChoicesValues,
	    userChoicesDescriptions,
	    defaultValues,
	    mandatoryComponents, this,
	    new WizardDocumentListener(this));
	
	
	listenerbutton = new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	   for (int i = 0; i < defaultValues.size(); i++) {
		if (defaultValues.get(i) != null) {
		   getComponent().setDefault(i);
		}
	   }

	};
   } ;

   }

   /**
    * Gibt die View wieder.
    *
    * @return MenuView
    */
   @Override
   public MenuView getComponent() {

	return menuView;
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
    * Gibt zurück, ob alle Parameter richtig eingegeben wurden und ob der
    * Benutzer dann auf "Next" klicken darf.
    *
    * @return Korrekte Konfiguration oder nicht.
    */
   @Override
   public boolean isValid() {
	return isValid;
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


   /**
    * Liest die Einstellungen der vorherigen Panels aus.
    *
    * @param lModel Das Model
    */
   @Override
   public void readSettings(WizardDescriptor lModel) {
	this.model = lModel;

	
	

	
	Object[] objects = lModel.getOptions();
	allDefaultButton = ((JButton) objects[0]);
	allDefaultButton.addActionListener(listenerbutton);
	
	if (hasDefault) {
	   allDefaultButton.setEnabled(true);
	} else {
	   allDefaultButton.setEnabled(false);
	}
	getComponent().addPropertyChangeListener(this);
   }
   
   
 
   
   /**
    * Übergibt die vom Benutzer eingegebenen Parameter an das Model.
    *
    *
    * @param lModel Das Model
    */
   @Override
   public void storeSettings(WizardDescriptor lModel) {

	allDefaultButton.removeActionListener(listenerbutton);


	for (int i = 0; i < configItems.size(); i++) {
	   if (configItems.get(i).getConfigType().equals(Types.Enumeration)
		 || configItems.get(i).getConfigType().equals(Types.Selection)) {
		lModel.putProperty(
		    id + className + configItems.get(i).getConfigName(),
		    getComponent().getInput(i));
	   } else if (configItems.get(i).getConfigType().equals(Types.Boolean)) {
		if (getComponent().getInput(i) == null) {
		   lModel.putProperty(id + className
			 + configItems.get(i).getConfigName(), "");
		} else {
		   lModel.putProperty(
			 id + className + configItems.get(i).getConfigName(),
			 getComponent().getInput(i));
		}
	   } else if (configItems.get(i).getConfigType().equals(Types.File)) {
		lModel.putProperty(
		    id + className + configItems.get(i).getConfigName(),
		    getComponent().getInput(i));
	   } else {
		lModel.putProperty(
		    id + className + configItems.get(i).getConfigName(),
		    getComponent().getInput(i));
	   }
	}
   }

   /**
    * Sobald sich derzeit eingegebenen Parameter ändern, wird in dieser Methode
    * überprüft, ob diese so akzeptiert erden.
    *
    * @param lEvt Änderung in der View.
    */
   @Override
   public void propertyChange(PropertyChangeEvent lEvt) {
	boolean oldState = isValid;

	isValid = checkValidity();
	fireChangeEvent(this, oldState, isValid);
   }

   /**
    * Überprüft, ob das eingegebene als Byte akzeptiert werden kann. (Eingabe
    * darf nicht mehr als 8 Bit benötigen.)
    *
    *
    * @param lInput mögliche Byte
    * @param lIndex Der Index des Configs in dem Node.
    * @return Korrekte Eingabe oder nicht.
    */
   private boolean checkConfigByte(String lInput, int lIndex) {
	BigInteger number;
	try {
	   if (!checkForLetters(lInput)) {
		NumberFormatException exception = new NumberFormatException();
		throw exception;
	   }
	   number = new BigInteger(lInput.trim());
	} catch (NumberFormatException e) {
	   setErrorMessage(Messages.getWarning(Messages.Byte, Integer.toString(lIndex), Integer.toString(nodeCounter)));
	   return false;
	}
	if ((number.bitLength() > 8)
	    || (number.abs() == number)) {
	   setErrorMessage(Messages.getWarning(Messages.Byte, Integer.toString(lIndex), Integer.toString(nodeCounter)));
	   return false;
	} else {
	   return true;
	}
   }

   /**
    * Überprüft, ob das eingegebene als Double akzeptiert werden kann.
    *
    *
    *
    * @param lInput mögliche Double
    * @param lIndex Der Index des Configs in dem Node.
    * @return Korrekte Eingabe oder nicht.
    */
   private boolean checkConfigDouble(String lInput, int lIndex) {
	try {
	   if (!checkForLetters(lInput)) {
		NumberFormatException exception = new NumberFormatException();
		throw exception;
	   }
	   Double.parseDouble(lInput.trim());
	} catch (NumberFormatException e) {
	   setErrorMessage(Messages.getWarning(Messages.Double, Integer.toString(lIndex), Integer.toString(nodeCounter)));
	   return false;
	}

	return true;
   }

   /**
    * Überprüft, ob das eingegebene als integer akzeptiert werden kann.
    *
    *
    * @param lInput möglicher Integer
    * @param lIndex Der Index des Configs in dem Node.
    * @return Korrekte Eingabe oder nicht.
    */
   private boolean checkConfigInteger(String lInput, int lIndex) {


	try {
	   if (!checkForLetters(lInput)) {
		NumberFormatException exception = new NumberFormatException();
		throw exception;
	   }
	   Integer.parseInt(lInput.trim());

	} catch (NumberFormatException e) {

	   setErrorMessage(Messages.getWarning(Messages.Integer, Integer.toString(lIndex), Integer.toString(nodeCounter)));
	   return false;
	}
	return true;
   }

   /**
    * Überprüft, ob das eingegebene als Long akzeptiert werden kann.
    *
    *
    * @param lInput möglicher Long
    * @param lIndex Der Index des Configs in dem Node.
    * @return Korrekte Eingabe oder nicht.
    */
   private boolean checkConfigLong(String lInput, int lIndex) {
	try {
	   if (!checkForLetters(lInput)) {
		NumberFormatException exception = new NumberFormatException();
		throw exception;
	   }
	   Long.parseLong(lInput.trim());
	} catch (NumberFormatException e) {
	   setErrorMessage(Messages.getWarning(Messages.Long, Integer.toString(lIndex), Integer.toString(nodeCounter)));
	   return false;
	}

	return true;
   }

   /**
    * Überprüft, ob das eingegebene als signed Byte akzeptiert werden kann.
    *
    *
    * @param lInput möglicher Integer
    * @param index Der Index des Configs in dem Node.
    * @return Korrekte Eingabe oder nicht.
    */
   private boolean checkConfigSByte(String lInput, int lIndex) {
	try {
	   if (!checkForLetters(lInput)) {
		NumberFormatException exception = new NumberFormatException();
		throw exception;
	   }
	   Byte.parseByte(lInput.trim());
	} catch (Exception e) {
	   setErrorMessage(Messages.getWarning(Messages.SByte, Integer.toString(lIndex), Integer.toString(nodeCounter)));
	   return false;
	}
	return true;
   }

   /**
    * Überprüft, ob das eingegebene als unsigned Long akzeptiert werden kann.
    * (Eingabe darf nicht mehr als 8 Bit benötigen.)
    *
    *
    * @param lInput mögliche Byte
    * @param lIndex Der Index des Configs in dem Node.
    * @return Korrekte Eingabe oder nicht.
    */
   private boolean checkConfigULong(String lInput, int lIndex) {
	BigInteger number;
	try {
	   if (!checkForLetters(lInput)) {
		NumberFormatException exception = new NumberFormatException();
		throw exception;
	   }
	   number = new BigInteger(lInput.trim());
	   if (number == (number.abs()) && (number.bitLength() <= 64)) {
		return true;
	   } else {
		setErrorMessage(Messages.getWarning(Messages.ULong, Integer.toString(lIndex), Integer.toString(nodeCounter)));
		return false;
	   }
	} catch (NumberFormatException e) {
	   setErrorMessage(Messages.getWarning(Messages.ULong, Integer.toString(lIndex), Integer.toString(nodeCounter)));
	   return false;
	}
   }

   /**
    * Hier wird überprüft, ob ein Default Button in der View betätigt wurde,
    * falls ja muss der Default im jeweilgen Feld gesetzt werden.
    *
    * @param lEvent ActionEvent des Default Button.
    */
   private void checkDefaultButtonEvent(ActionEvent lEvent) {
	BigInteger number = new BigInteger(lEvent.getActionCommand().split(":")[1]);
	for (int i = 0; i < defaultValues.size(); i++) {

	   if (number.intValue() == i
		 && (defaultValues.get(i) != null)) {
		getComponent().setDefault(i);
	   }
	}
   }

   /**
    * In dieser Methode wird überpüft, ob bei dem Node, alle Parameter bisher
    * richtig eingegeben wurden.
    *
    * @return richtig oder falsch
    */
   private boolean checkValidity() {
	for (int i = 0; i < configItems.size(); i++) {
	   boolean optional = configItems.get(i).isOptional();

	   if (optional == true) {
		if (checkOptionalInput((i))) {
		   return false;
		}
	   } else {
		if (checkMandatoryInput(i)) {
		   return false;
		}
	   }
	}
	setInformationMessage(null);
	return true;
   }

  
   
   /**
    * Hier wird überprüft, ob die bisherige Eingabe in den optionalen Feldern
    * soweit korrekt ist oder nicht.
    *
    * @param lConfigItemIndex Index des ConfigItems
    * @return Korrekte Eingabe oder nicht.
    */
   private boolean checkOptionalInput(int lConfigItemIndex) {
	String configType = configItems.get(lConfigItemIndex).getConfigType();

	if (configType.equals(Types.Enumeration)
	    || configType.equals(
	    Types.Selection)) {
	} else if (configType.equals(Types.Boolean)) {
	} else if (configType.equals(Types.File)) {
	} else {
	   if (!getComponent().getInput(lConfigItemIndex).equals("")) {
		if (correctParameter(configType,
		    getComponent().getInput(lConfigItemIndex),
		    (lConfigItemIndex + 1))) {
		} else {
		   return true;
		}
	   }
	}
	return false;
   }

   /**
    * Hier wird überprüft, ob die bisherige Eingabe in den nicht optionalen
    * Feldern soweit korrekt ist oder nicht.
    *
    * @param lIndex Index des ConfigItems
    * @return Korrekte Eingabe oder nicht.
    */
   private boolean checkMandatoryInput(int lIndex) {
	String configType = configItems.get(lIndex).getConfigType();
	String input = getComponent().getInput(lIndex);

	if (configType.equals(Types.Enumeration)
	    || configType.equals(
	    Types.Selection)) {
	   if ((input == null)
		 || input.trim().isEmpty()) {
		setWarningMessage(
		    Messages.getWarning(Messages.Enumeration, "" + (lIndex + 1), Integer.toString(nodeCounter)));
		return true;
	   } else {
	   }
	} else if (configType.equals(Types.Boolean)) {
	   if ((input == null)
		 || input.trim().isEmpty()) {
		setWarningMessage(Messages.getWarning(Messages.Boolean, "" + (lIndex + 1), Integer.toString(nodeCounter)));
		return true;
	   } else {
	   }
	} else if (configType.equals(Types.File)) {
	   if (input.equals("")) {
		setWarningMessage(Messages.getWarning(Messages.File, (lIndex + 1) + "", Integer.toString(nodeCounter)));
		return true;
	   } else {
	   }
	} else {
	   if (!getComponent().getInput(lIndex).trim().equals("")) {
		if (correctParameter(configType,
		    input,
		    (lIndex + 1))) {
		} else {
		   return true;
		}
	   } else {
		setWarningMessage(Messages.getWarning(Messages.Fill, "" + (lIndex + 1), Integer.toString(nodeCounter)));
		return true;
	   }
	}
	return false;
   }

   /**
    * Überprüft, ob der Eingegebene String nur Ziffern enthält.
    *
    * @param lInput Input
    * @return boolean.
    */
   private boolean checkForLetters(String lInput) {
	String regex = "(^\\d+$)|(^\\d+\\.\\d+$)|(^\\d+\\.$)";
	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(lInput.trim());
	return matcher.find();
   }

   /**
    * Hier werden alle Listener informiert, sobald sich der zustand des Panels
    * ändert.
    *
    * @param lSource Quelle
    * @param lOldState alter Status
    * @param newState neuer Status
    */
   protected void fireChangeEvent(Object lSource, boolean lOldState,
	 boolean newState) {

	if (lOldState != newState) {
	   ChangeEvent ev = new ChangeEvent(lSource);
	   for (ChangeListener listener :
		 listeners.getListeners(ChangeListener.class)) {
		listener.stateChanged(ev);
	   }
	}
   }

   /**
    * Setzt eine Information an den Benutzer im unteren Abschnitt des Panels.
    *
    * @param lMessage Nachricht.
    */
   private void setInformationMessage(String lMessage) {
	model.getNotificationLineSupport().setInformationMessage(lMessage);
   }

   /**
    * Setzt eine Warnung an den Benutzer im unteren Abschnitt des Panels.
    *
    * @param lMessage Warnung.
    */
   private void setWarningMessage(String lMessage) {
	model.getNotificationLineSupport().setWarningMessage(lMessage);
   }

   /**
    * Setzt eine Error Nachricht an den Benutzer im unteren Abschnitt des
    * Panels.
    *
    * @param lMessage Error.
    */
   private void setErrorMessage(String lMessage) {
	model.getNotificationLineSupport().setErrorMessage(lMessage);
   }

   /**
    * Ruft, ganz nachdem welcher Typ von Parameter überprüft werden soll, die
    * jeweilige Methode des Types auf.
    *
    * @param lType Typ des Parameters.
    * @param lInput Eingabe, die überprüft werden soll.
    * @param lIndex Der Index des ConfigItems.
    * @return Richtiger oder Falscher Parameter.
    */
   private boolean correctParameter(String lType, String lInput, int lIndex) {

	if (lType.equals(Types.Integer)) {
	   return checkConfigInteger(lInput, lIndex);
	} else if (lType.equals(Types.Double.toString())) {
	   return checkConfigDouble(lInput, lIndex);
	} else if (lType.equals(Types.Long.toString())) {
	   return checkConfigLong(lInput, lIndex);
	} else if (lType.equals(Types.Byte.toString())) {
	   return checkConfigByte(lInput, lIndex);
	} else if (lType.equals(Types.ULong.toString())) {
	   return checkConfigULong(lInput, lIndex);
	} else if (lType.equals(Types.SByte.toString())) {
	   return checkConfigSByte(lInput, lIndex);
	}
	return true;
   }

   /**
    * ActionPerformed Methode für die Buttons, ComboBoxen und RadioButtons.
    *
    * @param lE ActionEvent
    */
   @Override
   public void actionPerformed(ActionEvent lE) {
	if (lE.getActionCommand().startsWith(ActionCommands.FileOpen)) {

	   BigInteger number = new BigInteger(lE.getActionCommand().split(":")[1]);
	   getComponent().chooseFile(number.intValue());

	} else if (lE.getActionCommand().equals(ActionCommands.Box)) {
	   this.propertyChange(null);
	} else if (lE.getActionCommand().equals(ActionCommands.RadioTrue)
	    || lE.getActionCommand().equals(ActionCommands.RadioFalse)) {
	   this.propertyChange(null);
	} else if (lE.getActionCommand().startsWith(ActionCommands.FileClear)) {

	   BigInteger number = new BigInteger(lE.getActionCommand().split(":")[1]);
	   getComponent().resetFileFieldText(number.intValue());
	   this.propertyChange(null);
	} else if (lE.getActionCommand().startsWith(ActionCommands.Default)) {
	   checkDefaultButtonEvent(lE);
	}
   }
}


//~ Formatted by Jindent --- http://www.jindent.com
