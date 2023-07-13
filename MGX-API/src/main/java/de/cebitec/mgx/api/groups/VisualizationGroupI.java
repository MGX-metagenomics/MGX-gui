/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.model.SeqRunI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public interface VisualizationGroupI extends GroupI<SeqRunI> {

    public static final DataFlavor VISGROUP_DATA_FLAVOR = new DataFlavor(VisualizationGroupI.class, "VisualizationGroupI");
    //private String managedState = OBJECT_MANAGED;
    //

}
