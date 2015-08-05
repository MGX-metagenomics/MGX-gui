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
public class SwitchMode extends JPopupMenu implements ActionListener {

    private final TopComponentViewer tc;

    public SwitchMode(TopComponentViewer tc) {
        super();
        this.tc = tc;
        JMenuItem item = new JMenuItem("Switch view");
        item.addActionListener(this);
        add(item);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                tc.switchMode();
            }
        });
    }

}
