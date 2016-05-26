/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public interface ModelBaseI<T extends ModelBaseI<T>> extends Transferable, Comparable<T> {

    public final static String OBJECT_MANAGED = "objectManaged";
    public final static String OBJECT_DELETED = "objectDeleted";
    public final static String OBJECT_MODIFIED = "objectModified";
    
    @Override
    public DataFlavor[] getTransferDataFlavors();

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor);

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException;

    public void modified();

    public void deleted();
    
    public boolean isDeleted();

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue);

    public void firePropertyChange(String propertyName, int oldValue, int newValue);

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue);

    public void firePropertyChange(PropertyChangeEvent event);

}
