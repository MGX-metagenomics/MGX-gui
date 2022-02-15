/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.impl;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.cache.IntIterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface ViewControllerI extends PropertyChangeListener {

    public static final String BOUNDS_CHANGE = "boundsChange";
    //
    public static final String VIEWCONTROLLER_CLOSED = "viewControllerClosed";

    public static final String MAX_COV_CHANGE = "maxCoverageChange";
    public static final String PREVIEWBOUNDS_CHANGE = "previewBoundsChange";
    public static final String MIN_IDENTITY_CHANGE = "minIdentityChange";

    void addPropertyChangeListener(PropertyChangeListener listener);

    void close();

    int[] getBounds();
    
    public ToolI getTool() throws MGXException;
    
    public SeqRunI[] getSeqRuns() throws MGXException;
    
    public MappingI getMapping() throws MGXException;

    String getReferenceName();

    int getReferenceLength();

    int getIntervalLength();

    public String getSequence(int from, int to);

    public Set<RegionI> getRegions() throws MGXException;

    public void setMinIdentity(int ident);

    public int getMinIdentity();

    public long getMaxCoverage() throws MGXException;

    public IntIterator getCoverageIterator() throws MGXException;

    public void getCoverage(int from, int to, int[] dest) throws MGXException;

    public long getGenomicCoverage() throws MGXException;

    public List<MappedSequenceI> getMappings() throws MGXException;

    public List<MappedSequenceI> getMappings(int from, int to) throws MGXException;

    public List<MappedSequenceI> getMappings(int from, int to, int minIdent) throws MGXException;

    MGXMasterI getMaster();

    boolean isClosed();

    @Override
    void propertyChange(PropertyChangeEvent evt);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void setBounds(int i, int j);

}