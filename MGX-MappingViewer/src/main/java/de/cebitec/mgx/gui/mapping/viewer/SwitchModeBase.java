/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author sjaenick
 */
public abstract class SwitchModeBase extends JPopupMenu implements ActionListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final MappingViewerTopComponent tc;

    protected SwitchModeBase(MappingViewerTopComponent tc, String target) {
        super();
        this.tc = tc;
        JMenuItem item = new JMenuItem("Switch to " + target);
        item.addActionListener(this);
        item.setActionCommand("switchMode");
        super.add(item);
        //
        JMenuItem item2 = new JMenuItem("Download as BAM");
        item2.addActionListener(this);
        item2.setActionCommand("downloadBAM");
        super.add(item2);
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "switchMode":
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        tc.switchMode();
                    }
                });
                break;
            case "downloadBAM":
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        tc.downloadBAM();
                    }
                });
                break;
            default:
                assert false;
        }
    }

}
