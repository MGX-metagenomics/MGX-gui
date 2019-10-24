/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.visgroups.workers;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.Fetcher;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public final class AssembledRunAttributeTypeFetcher extends Fetcher<Map<JobI, Set<AttributeTypeI>>> {

    private final AssembledSeqRunI run;

    public AssembledRunAttributeTypeFetcher(final AssembledSeqRunI run) {
        this.run = run;
    }

    @Override
    protected Map<JobI, Set<AttributeTypeI>> doInBackground() throws Exception {
        MGXMasterI master = run.getMaster();
        return master.SeqRun().getJobsAndAttributeTypes(run);
    }

}
