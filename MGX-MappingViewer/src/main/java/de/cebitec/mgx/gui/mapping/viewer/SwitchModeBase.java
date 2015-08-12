/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author sjaenick
 */
public abstract class SwitchModeBase extends JPopupMenu implements ActionListener {

    private final TopComponentViewer tc;

    protected SwitchModeBase(TopComponentViewer tc, String target) {
        super();
        this.tc = tc;
        JMenuItem item = new JMenuItem("Switch to "+ target);
        item.addActionListener(this);
        add(item);
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                tc.switchMode();
            }
        });
    }

}
