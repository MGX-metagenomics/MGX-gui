/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.common.VGroupManager;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author sjaenick
 */
final class SelectionHandler implements InternalFrameListener {

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        JInternalFrame srcFrame = e.getInternalFrame();
        if (srcFrame instanceof GroupFrame) {
            GroupFrame gFrame = (GroupFrame)srcFrame;
            VGroupManager.getInstance().setSelectedVizGroup(gFrame.getGroup());
        } else if (srcFrame instanceof ReplicateGroupFrame) {
            ReplicateGroupFrame rFrame = (ReplicateGroupFrame) srcFrame;
            VGroupManager.getInstance().setSelectedReplicateGroup(rFrame.getReplicateGroup());
        }
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

}
