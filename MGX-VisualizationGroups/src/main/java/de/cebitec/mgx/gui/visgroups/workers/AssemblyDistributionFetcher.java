/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.visgroups.workers;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Fetcher;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;

/**
 *
 * @author sjaenick
 */
public class AssemblyDistributionFetcher extends Fetcher<Pair<AssembledSeqRunI, DistributionI<Long>>> {

    protected final AssembledSeqRunI run;
    protected final AttributeTypeI attrType;
    protected final JobI job;

    public AssemblyDistributionFetcher(final AssembledSeqRunI run, final AttributeTypeI attrType, final JobI job) {
        this.run = run;
        this.attrType = attrType;
        this.job = job;
    }

    @Override
    protected Pair<AssembledSeqRunI, DistributionI<Long>> doInBackground() throws Exception {
        MGXMasterI master = run.getMaster();
//        if (attrType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL) {
//            TreeI<Long> tree = master.Attribute().getHierarchy(attrType, job);
//            return new Pair<>(run, DistributionFactory.fromTree(tree, attrType));
//        } else {
            return new Pair<>(run, master.Attribute().getDistribution(attrType, job, run.getSeqRun()));
//        }
    }

}
