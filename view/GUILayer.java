package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import model.*;

public class GUILayer 
{
    Layer layer;
    AttributeSet attr;

    public GUILayer(Layer layer, AttributeSet attr) {
        this.layer = layer;
        this.attr = attr;
    }

    public Layer getLayer() {
        return layer;
    }

    public AttributeSet getAttr() { 
        return attr; 
    }

    public void setAttr(AttributeSet attr) {
        this.attr = attr;
    }
}
