/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class SwitchToAlignment extends SwitchModeBase {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public SwitchToAlignment(MappingViewerTopComponent tc) {
        super(tc, "Alignment View");
    }
}
