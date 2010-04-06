package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class NewLayerWindow extends JFrame
{
    public NewLayerWindow() {
        super("New Layer");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final JTextField textField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(textField);

        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    controller.Control.addLayer(textField.getText());
                }
            });

        panel.add(button);
        getContentPane().add(panel);
        pack();
        setVisible(true);
    }
}
