package view;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import model.*;

public class LayersSideList extends JPanel
{
    List<GUILayer> layers;

    public LayersSideList() {
        super();

        layers = new ArrayList<GUILayer>();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        LayersSideNew panel = new LayersSideNew();

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setMaximumSize(panel.getTextField().getPreferredSize());

        add(panel);
        refresh();
    }

    public void addEntry(GUILayer layer) {
        LayersSideEntry panel = new LayersSideEntry(layer);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);

        layers.add(layer);
        add(panel,1);

        refresh();
    }

    public void refresh() {
        invalidate();
        repaint();
        validate();
    }

    public List<GUILayer> getLayers() {
        return layers;
    }

    public void setCurrentLayer(GUILayer layer) {
        Component[] components = getComponents();

        for (int x = 0; x < components.length; x++) {
            if (components[x] instanceof LayersSideEntry) {
                LayersSideEntry entry = (LayersSideEntry)components[x];
                GUILayer l = entry.getLayer();
                if (l == layer)
                    entry.setCurrent();
                else
                    entry.unsetCurrent();
            }
        }
    }

    public GUILayer getGuiLayer(Layer layer) {
        for (GUILayer gl : layers) {
            if (gl.getLayer().equals(layer))
                return gl;
        }
        return null;
    }

    // XXX not sure this is the right place for this
    public AttributeSet getAttr(Layer l) {
        if (l == null)
            return VibeEditor.getInstance().defaultAttr();
        for (GUILayer gl : layers) {
            if (gl.getLayer().equals(l)) {
                System.out.println("returning attr for " + gl.getLayer().getName());
                return gl.getAttr();
            }
        }
        System.out.println("falling out");
        return VibeEditor.getInstance().defaultAttr();
    }

    public void setCheckbox(GUILayer layer, boolean flag) {
        Component[] components = getComponents();

        for (int x = 0; x < components.length; x++) {
            if (components[x] instanceof LayersSideEntry) {
                LayersSideEntry entry = (LayersSideEntry)components[x];
                GUILayer l = entry.getLayer();
                if (l.getLayer().equals(layer.getLayer())) {
                    entry.getCheckbox().setSelected(flag);
                    return;
                }
            }
        }
    }
}
