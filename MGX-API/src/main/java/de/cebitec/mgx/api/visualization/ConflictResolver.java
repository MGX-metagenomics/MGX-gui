/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.visualization;

import de.cebitec.mgx.api.groups.GroupI;
import java.util.List;

/**
 *
 * @author sj
 */
public interface ConflictResolver {

    public void resolve(String attributeType, List<GroupI> vg);
}
