/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.api.MGXMasterI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sj
 */
public interface ViewControllerI extends PropertyChangeListener {

    String BOUNDS_CHANGE = "boundsChange";
    //
    String VIEWCONTROLLER_CLOSED = "viewControllerClosed";

    void addPropertyChangeListener(PropertyChangeListener listener);

    void close();

    int[] getBounds();
    
    String getReferenceName();
    
    int getReferenceLength();

    int getIntervalLength();
    
    public String getSequence(int from, int to);

    MGXMasterI getMaster();

    boolean isClosed();

    void propertyChange(PropertyChangeEvent evt);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void setBounds(int i, int j);
    
}
