/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.common;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.Fetcher;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public final class AttributeTypeFetcher extends Fetcher<Map<JobI, Set<AttributeTypeI>>> {

    private final SeqRunI run;

    public AttributeTypeFetcher(final SeqRunI run) {
        this.run = run;
    }

    @Override
    protected Map<JobI, Set<AttributeTypeI>> doInBackground() throws Exception {
        MGXMasterI master = run.getMaster();
        return master.SeqRun().getJobsAndAttributeTypes(run);
    }

}
