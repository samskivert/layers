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

public class LayersSideEntry extends JPanel
{
    final JCheckBox checkbox;
    final JLabel label;
    final GUILayer guiLayer;
    //    final JLabel colorLabel;
    final ColorChooser chooser;

    public LayersSideEntry(GUILayer layer)
    {
        VibeEditor editor = VibeEditor.getInstance();
        //attrSet = editor.nextBgAttr();

        this.guiLayer = layer;

        checkbox = new JCheckBox();
        if (layer.getLayer().isOn())
            checkbox.setSelected(true);
        if (layer.getLayer().isBase())
            checkbox.setEnabled(false);

        checkbox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    Control.toggleLayer(guiLayer);
                }
            });

        add(checkbox);

        chooser = new ColorChooser();
        chooser.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Control.changeColor(guiLayer, chooser.getColor());
                }
            });

        Color c = StyleConstants.getBackground(layer.getAttr());
        chooser.setColor(c);

        if (layer.getLayer().isBase())
            chooser.setEnabled(false);

        add(chooser);

        JLabel label2 = new JLabel("  ");
        add(label2);

        label = new JLabel(layer.getLayer().getName());

        label.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Control.setCurrentLayer(guiLayer);
                }
            });

        add(label);
    }

    public void toggle() {
        if (checkbox.isSelected())
            checkbox.setSelected(false);
        else
            checkbox.setSelected(true);
    }

    public JCheckBox getCheckbox() {
        return checkbox;
    }

    public GUILayer getLayer() {
        return guiLayer;
    }

    public void setCurrent() {
        Border border = BorderFactory.createLineBorder(Color.RED, 3);
        checkbox.setSelected(true);
        label.setBorder(border);
    }

    public void unsetCurrent() {
        label.setBorder(new EmptyBorder(0,0,0,0));
    }
}
