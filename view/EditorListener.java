package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;


public class EditorListener implements ILayerListener
{
    Document doc;

    public void setDocument(Document doc) {
        this.doc = doc;
    }

    public void textRemoved(String text, int pos) {
        controller.Control.textRemoved(text, pos);
        System.out.println("Removed " + text + " at " + pos);
    }

    public void textAdded(String text, int pos) {
        controller.Control.textAdded(text, pos);
        System.out.println("Added " + text + " at " + pos);
    }

    public void textReplaced(String oldText, String newText, int pos) {
        System.out.println("Replaced " + oldText + " with " + newText + " at " + pos);
    }
}
