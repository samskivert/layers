package controller;

import java.awt.Color;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import view.*;
import model.*;

public class Control 
{
    public static boolean EDITING_INTERNALLY = false;

    public static void addLayer(String layerName)
    {
        System.out.println("Control.addLayer: " + layerName);

        LayerDB db = LayerDB.getInstance();
        VibeEditor editor = VibeEditor.getInstance();

        Layer layer = new Layer(layerName);
        layer.turnOn();

        db.addLayer(layer);
        db.setCurrentLayer(layer);

        GUILayer guiLayer = new GUILayer(layer, editor.nextBgAttr());
        editor.getEditor().setAttributes(guiLayer.getAttr());

        editor.getLayersSideList().addEntry(guiLayer);

        setCurrentLayer(guiLayer);
    }

    // XXX implement circular edit check
    public static void textRemoved(final String text, final int pos)
    {
        if (EDITING_INTERNALLY)
            return;

        PositionManager pm = PositionManager.getInstance();
        LayerDB db = LayerDB.getInstance();
        final VibeEditor editor = VibeEditor.getInstance();
        final Document doc = editor.getEditor().getDocument();

        final Layer l = db.getCurrentLayer();
        final Layer l1 = db.editOverlaps(pos);

        if (l != l1 && l1 != null) {
            if (db.addDependency(l1, l) == false) {
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                AttributeSet attr = 
                                    editor.getLayersSideList().getAttr(l1);
                                AttributeSet oldAttr = editor.getEditor().getAttributes();
                                editor.getEditor().setAttributes(attr);

                                Control.EDITING_INTERNALLY = true;
                                doc.insertString(pos, text, attr);
                                Control.EDITING_INTERNALLY = false;
                                
                                editor.getEditor().setAttributes(oldAttr);
                            }
                            catch (BadLocationException ble) {
                                Control.failedException(ble);
                            }
                        }
                    });
                return;
            }
        }

        Edit e = l.removeText(pos, text);

        if (e != null)
            pm.bumpAfter(e.getPos(), - text.length());

        db.dump(0);
    }

    public static void textAdded(final String text, final int pos)
    {
        if (EDITING_INTERNALLY)
            return;

        PositionManager pm = PositionManager.getInstance();
        LayerDB db = LayerDB.getInstance();
        VibeEditor editor = VibeEditor.getInstance();
        final Document doc = editor.getEditor().getDocument();

        Layer l = db.getCurrentLayer();
        Layer l1 = db.editOverlaps(pos);

        if (l1 != null) {
            if (db.addDependency(l1, l) == false) {
                // Attempting circular edit. Undo the edit.
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                Control.EDITING_INTERNALLY = true;
                                doc.remove(pos, text.length());
                                Control.EDITING_INTERNALLY = false;
                            } catch (BadLocationException e) {
                                Control.failedException(e);
                            }
                        }
                    });
                return;
            }
        }

        Edit e = l.addText(pos, text);

        if (e != null)
            pm.bumpAfter(e.getPos(), text.length());

        LayerDB.getInstance().dump(0);
    }

    public static void addBaseLayer()
    {
        LayerDB db = LayerDB.getInstance();
        VibeEditor editor = VibeEditor.getInstance();

        Layer layer = new BaseLayer();
        layer.turnOn();

        db.addLayer(layer);
        db.setCurrentLayer(layer);

        GUILayer guiLayer = new GUILayer(layer, editor.defaultAttr());

        editor.getLayersSideList().addEntry(guiLayer);

        setCurrentLayer(guiLayer);
    }

    private static void turnOffChildren(GUILayer layer) {
        VibeEditor editor = VibeEditor.getInstance();
        LayerDB db = LayerDB.getInstance();

        if (layer.getLayer() == null) {
            System.out.println("-- parent null");
        }

        List<Layer> children = db.getChildren(layer.getLayer());

        if (children == null)
            return;

        List<GUILayer> guiLayers = editor.getLayersSideList().getLayers();

        for (GUILayer gl : guiLayers) {
            for (Layer l : children) {
                if (l.equals(gl.getLayer()) && l.isOn())
                    toggleLayer(gl);
            }
        }
    }

    private static void turnOnParents(GUILayer layer) {
        VibeEditor editor = VibeEditor.getInstance();
        LayerDB db = LayerDB.getInstance();

        List<Layer> parents = db.getParents(layer.getLayer());

        if (parents == null)
            return;

        List<GUILayer> guiLayers = editor.getLayersSideList().getLayers();
        for (GUILayer gl : guiLayers) {
            for (Layer l : parents) {
                if (l.equals(gl.getLayer()) && !l.isOn()) {
                    toggleLayer(gl);
                }
            }
        }
    }

    public static void turnOnNext(Layer l) {
        VibeEditor editor = VibeEditor.getInstance();
        LayerDB db = LayerDB.getInstance();

        Layer next = db.getNextActiveLayer(l);
        GUILayer gl = editor.getLayersSideList().getGuiLayer(next);

        setCurrentLayer(gl);
    }

    public static void toggleLayer(GUILayer layer) {
        System.out.println("toggle layer " + layer);

        LayerDB db = LayerDB.getInstance();
        PositionManager pm = PositionManager.getInstance();
        VibeEditor editor = VibeEditor.getInstance();
        Set edits = layer.getLayer().getEdits();
        Document doc = editor.getEditor().getDocument();

        if (layer.getLayer().isOn()) {
            turnOffChildren(layer);

            for (Object o : edits) { 
                Edit e = (Edit)o;

                EDITING_INTERNALLY = true;

                try {
                    doc.remove(e.getPos().get(), e.getBuffer().length());
                } catch (BadLocationException ee) {
                    Control.failedException(ee);
                }

                pm.bumpAfter(e.getPos(), -e.getBuffer().length());

                AttributeSet oldAttr = layer.getAttr();
                Layer underLayer = db.editOverlaps(e.getPos().get(), layer.getLayer());
                AttributeSet attr = 
                    editor.getLayersSideList().getAttr(underLayer);

                editor.getEditor().setAttributes(attr);

                try {
                    doc.insertString(e.getPos().get(),
                                     e.getReplaced().toString(),
                                     attr);
                } catch (BadLocationException ee) {
                    Control.failedException(ee);
                }

                editor.getEditor().setAttributes(oldAttr);
                pm.bumpAfter(e.getPos(), e.getReplaced().length());

                EDITING_INTERNALLY = false;
            }

            layer.getLayer().turnOff();
            editor.getLayersSideList().setCheckbox(layer, false);

            turnOnNext(layer.getLayer());
        }
        else {
            turnOnParents(layer);
            // save previous attributes
            AttributeSet attr = editor.getEditor().getAttributes();
            // set layer's attributes
            editor.getEditor().setAttributes(layer.getAttr());

            for (Object o : edits) { 
                Edit e = (Edit)o;

                EDITING_INTERNALLY = true;

                try {
                    doc.remove(e.getPos().get(), e.getReplaced().length());
                }
                catch (BadLocationException ee) {
                    Control.failedException(ee);
                }

                pm.bumpAfter(e.getPos(), -e.getReplaced().length());

                try {
                    doc.insertString(e.getPos().get(), 
                                     e.getBuffer().toString(), 
                                     layer.getAttr());

                }
                catch (BadLocationException ee) {
                    Control.failedException(ee);
                }

                pm.bumpAfter(e.getPos(), e.getBuffer().length());

                EDITING_INTERNALLY = false;
            }

            layer.getLayer().turnOn();
            editor.getLayersSideList().setCheckbox(layer, true);
            // restore previous attributes
            editor.getEditor().setAttributes(attr);
        }
    }

    public static void setCurrentLayer(GUILayer layer) {
        LayerDB db = LayerDB.getInstance();
        db.setCurrentLayer(layer.getLayer());

        if (! layer.getLayer().isOn())
            toggleLayer(layer);

        VibeEditor editor = VibeEditor.getInstance();
        editor.getEditor().setAttributes(layer.getAttr());
        editor.getLayersSideList().setCurrentLayer(layer);
    }

    public static void changeColor(GUILayer guiLayer, Color c) {
        VibeEditor editor = VibeEditor.getInstance();

        SimpleAttributeSet attr = (SimpleAttributeSet)guiLayer.getAttr();
        StyleConstants.setBackground(attr, c);
        guiLayer.setAttr(attr);

        if (! guiLayer.getLayer().isOn())
            // nothing to do
            return;

        AbstractDocument doc = (AbstractDocument)editor.getEditor().getDocument();
        Set edits = guiLayer.getLayer().getEdits();

        AttributeSet tmpAttr = editor.getEditor().getAttributes();
        editor.getEditor().setAttributes(attr);

        for (Object o : edits) {
            Edit edit = (Edit)o;

            int pos = edit.getPos().get();
            int length = edit.getBuffer().length();

            try {
                EDITING_INTERNALLY = true;
                doc.replace(pos, length, edit.getBuffer().toString(), attr);
                EDITING_INTERNALLY = false;
            } catch (Exception e) {
                Control.failedException(e);
            }
        }

        editor.getEditor().setAttributes(tmpAttr);
    }

    private static int exnCount = 0;

    public static void failedException(Exception e) {
        VibeEditor editor = VibeEditor.getInstance();
        exnCount++;
        e.printStackTrace();
        editor.setTitle(exnCount + ": [" + e.getMessage() + "]");
    }
}
