/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.impl;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.SequenceViewControllerI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.cache.IntIterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 *
 * @author sj
 */
public interface ViewControllerI extends SequenceViewControllerI<ReferenceRegionI>, PropertyChangeListener {

    public static final String MAX_COV_CHANGE = "maxCoverageChange";
    public static final String PREVIEWBOUNDS_CHANGE = "previewBoundsChange";
    public static final String MIN_IDENTITY_CHANGE = "minIdentityChange";

    void close();

    public ToolI getTool() throws MGXException;
    
    public SeqRunI[] getSeqRuns() throws MGXException;
    
    public MappingI getMapping() throws MGXException;

    //public Set<RegionI> getRegions() throws MGXException;

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

    @Override
    void propertyChange(PropertyChangeEvent evt);

}
