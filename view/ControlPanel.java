package view;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;


public class ControlPanel extends JPanel implements ActionListener {
    JButton map;
    JButton port;
    ControlListener listener;

    public ControlPanel() {
        map = new JButton("Map");
        port = new JButton("Port");

        listener = new ControlListener() {
                public void map() {
                    LayerFrame f = new LayerFrame();
                    f.pack();
                    f.setVisible(true);
                    System.out.println("Clicked map");
                }
                public void port() {
                    System.out.println("Clicked port");
                }
            };

        add(map);
        add(port);

        map.addActionListener(this);
        port.addActionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();

        if (src == map) 
            listener.map();
        else if (src == port)
            listener.port();
    }

    public void setListener(ControlListener l) {
        this.listener = l;
    }
}
