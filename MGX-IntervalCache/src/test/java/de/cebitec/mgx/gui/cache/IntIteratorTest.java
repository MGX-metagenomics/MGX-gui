/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class IntIteratorTest {

    public IntIteratorTest() {
    }

//    @Test
//    public void testCoveragePriv() {
//        System.err.println("testCoveragePriv");
//        MGXMasterI master = TestMaster.getPrivate();
//        Iterator<MappingI> iter = master.Mapping().fetchall();
//        MappingI mapping = null;
//        while (iter.hasNext()) {
//            mapping = iter.next();
//        }
//        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
//        UUID uuid = master.Mapping().openMapping(1);
//        CoverageInfoCache<SortedSet<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
//        //
//        //
//        int max = -1;
//        IntIterator covIter = cache.getCoverageIterator(0, ref.getLength() - 1);
//        while (covIter.hasNext()) {
//            int c = covIter.next();
//            if (c > max) {
//                max = c;
//            }
//        }
//        assertEquals(8901, max);
//    }
    
    
    @Test
    public void testIteratorLength() throws MGXException {
        System.err.println("testIteratorLength");
        MGXMasterI master = TestMaster.getRO();
        Iterator<MappingI> iter = master.Mapping().fetchall();
        int cnt = 0;
        MappingI mapping = null;
        while (iter.hasNext()) {
            mapping = iter.next();
            cnt++;
        }
        assertEquals(1, cnt);
        assertNotNull(mapping);
        assertEquals(1, mapping.getId());
        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
        UUID uuid = master.Mapping().openMapping(mapping.getId());
        CoverageInfoCache<Set<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
        assertNotNull(cache);
        //
        //
        int numPos = 0;
        IntIterator covIter = cache.getCoverageIterator(0, ref.getLength() - 1);
        while (covIter.hasNext()) {
            int c = covIter.next();
            numPos++;
        }
        assertEquals(numPos, ref.getLength());
    }
}
