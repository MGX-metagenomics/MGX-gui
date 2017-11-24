/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.common;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Fetcher;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;

/**
 *
 * @author sjaenick
 */
public class DistributionFetcher extends Fetcher<Pair<SeqRunI, DistributionI<Long>>> {

    protected final SeqRunI run;
    protected final AttributeTypeI attrType;
    protected final JobI job;
//    protected final CountDownLatch latch;
//    protected final Map<SeqRunI, DistributionI> result;

    public DistributionFetcher(final SeqRunI run, final AttributeTypeI attrType, final JobI job) {
        this.run = run;
        this.attrType = attrType;
        this.job = job;
    }

    @Override
    protected Pair<SeqRunI, DistributionI<Long>> doInBackground() throws Exception {
        MGXMasterI master = run.getMaster();
//        if (attrType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL) {
//            TreeI<Long> tree = master.Attribute().getHierarchy(attrType, job);
//            return new Pair<>(run, DistributionFactory.fromTree(tree, attrType));
//        } else {
            return new Pair<>(run, master.Attribute().getDistribution(attrType, job));
//        }
    }

//    @Override
//    protected void done() {
//        DistributionI dist;
//        try {
//            dist = get();
//            result.put(run, dist);
//        } catch (InterruptedException | ExecutionException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        latch.countDown();
//    }

}
