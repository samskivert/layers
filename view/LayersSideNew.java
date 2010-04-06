package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import model.*;
import controller.*;

import net.java.dev.colorchooser.*;

public class LayersSideNew extends JPanel
{
    final JCheckBox checkbox;
    final JTextField textField;
    final ColorChooser chooser;

    public LayersSideNew()
    {
        super();

        final VibeEditor editor = VibeEditor.getInstance();

        checkbox = new JCheckBox();
        checkbox.setSelected(false);
        checkbox.setEnabled(false);

        add(checkbox);

        chooser = new ColorChooser();
        chooser.setColor(StyleConstants.getBackground(editor.peekBgAttr()));
        chooser.setEnabled(false);

        add(chooser);
        add(new JLabel(" "));

        textField = new JTextField("New Layer", 13);
        textField.setEnabled(false);

        // swing is such bullshit
        textField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        controller.Control.addLayer(textField.getText());
                        textField.setText("");
                        chooser.setColor(StyleConstants.getBackground(editor.peekBgAttr()));
                    }
                }
            });

        textField.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (! textField.hasFocus())
                        textField.grabFocus();
                }
            });

        textField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    textField.setText("");
                    textField.setEnabled(true);
                }

                public void focusLost(FocusEvent e) {
                    textField.setText("New Layer");
                    textField.setEnabled(false);
                }
            });

        add(textField);
    }

    public JTextField getTextField() {
        return textField;
    }
}
