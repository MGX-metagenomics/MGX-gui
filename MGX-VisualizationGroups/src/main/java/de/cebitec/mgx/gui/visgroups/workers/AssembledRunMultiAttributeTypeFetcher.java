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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public final class AssembledRunMultiAttributeTypeFetcher extends Fetcher<Map<AssembledSeqRunI, Map<JobI, Set<AttributeTypeI>>>> {

    private final AssembledSeqRunI[] runs;

    public AssembledRunMultiAttributeTypeFetcher(final AssembledSeqRunI... runs) {
        this.runs = runs;
    }

    @Override
    protected Map<AssembledSeqRunI, Map<JobI, Set<AttributeTypeI>>> doInBackground() throws Exception {
        Map<AssembledSeqRunI, Map<JobI, Set<AttributeTypeI>>> ret = new HashMap<>();
        for (AssembledSeqRunI run : runs) {
            MGXMasterI master = run.getMaster();
            Map<JobI, Set<AttributeTypeI>> data = master.SeqRun().getJobsAndAttributeTypes(run);
            ret.put(run, data);
        }
        return ret;
    }

}
