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
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public final class MultiAttributeTypeFetcher extends Fetcher<Map<SeqRunI, Map<JobI, Set<AttributeTypeI>>>> {

    private final SeqRunI[] runs;

    public MultiAttributeTypeFetcher(final SeqRunI... runs) {
        this.runs = runs;
    }

    @Override
    protected Map<SeqRunI, Map<JobI, Set<AttributeTypeI>>> doInBackground() throws Exception {
        Map<SeqRunI, Map<JobI, Set<AttributeTypeI>>> ret = new HashMap<>();
        for (SeqRunI run : runs) {
            MGXMasterI master = run.getMaster();
            Map<JobI, Set<AttributeTypeI>> data = master.SeqRun().getJobsAndAttributeTypes(run);
            ret.put(run, data);
        }
        return ret;
    }

}
