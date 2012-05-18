/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.menu;


import java.beans.PropertyChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author belmann
 */
public class WizardDocumentListener implements DocumentListener {
MenuController listener;
   
   public WizardDocumentListener(MenuController propertyChangeListener){
   listener = propertyChangeListener;
   
   }
   
   @Override
   public void insertUpdate(DocumentEvent e) {
listener.getComponent().firePropertyChange(null, 0, 1);
   }

   @Override
   public void removeUpdate(DocumentEvent e) {
	throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void changedUpdate(DocumentEvent e) {
	throw new UnsupportedOperationException("Not supported yet.");
   }
   
}
