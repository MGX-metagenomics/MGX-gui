/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache;

import com.google.common.cache.LoadingCache;
import de.cebitec.mgx.gui.cache.internal.Interval;
import de.cebitec.mgx.gui.datamodel.Reference;

/**
 *
 * @author sj
 */
public abstract class CoverageInfoCache<T> extends Cache<T> {

    public CoverageInfoCache(Reference ref, LoadingCache<Interval<T>, T> lcache) {
        super(ref, lcache);
    }

    public CoverageInfoCache(Reference ref, LoadingCache<Interval<T>, T> lcache, int segSize) {
        super(ref, lcache, segSize);
    }

    public abstract int[] getCoverage(int from, int to);

    public abstract int getMaxCoverage(int from, int to);
}
