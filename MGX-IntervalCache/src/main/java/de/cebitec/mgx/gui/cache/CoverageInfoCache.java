/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;

/**
 *
 * @author sj
 */
public abstract class CoverageInfoCache<T> extends Cache<T> {

    public CoverageInfoCache(MGXReferenceI ref, LoadingCache<Interval, T> lcache) {
        super(ref, lcache);
    }

    public CoverageInfoCache(MGXReferenceI ref, LoadingCache<Interval, T> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    public abstract void getCoverage(int from, int to, int[] dest) throws MGXException;

    public abstract IntIterator getCoverageIterator(int from, int to) throws MGXException;

    public abstract int getMaxCoverage(int from, int to) throws MGXException;
}
