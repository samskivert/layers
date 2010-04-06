package view;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import java.io.*;

class CaretListenerLabel extends JLabel implements CaretListener {
    JTextPane textPane;
    String newline = "\n";

    public CaretListenerLabel(String label, JTextPane textPane) {
        super(label);
        this.textPane = textPane;
        this.textPane.addCaretListener(this);
    }

    //Might not be invoked from the event dispatch thread.
    public void caretUpdate(CaretEvent e) {
        displaySelectionInfo(e.getDot(), e.getMark());
    }

    //This method can be invoked from any thread.  It 
    //invokes the setText and modelToView methods, which 
    //must run on the event dispatch thread. We use
    //invokeLater to schedule the code for execution
    //on the event dispatch thread.
    protected void displaySelectionInfo(final int dot,
                                        final int mark) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (dot == mark) {  // no selection
                        try {
                            Rectangle caretCoords = textPane.modelToView(dot);
                            //Convert it to view coordinates.
                            setText("caret: text position: " + dot
                                    + ", view location = ["
                                    + caretCoords.x + ", "
                                    + caretCoords.y + "]"
                                    + newline);
                        } catch (BadLocationException ble) {
                            setText("caret: text position: " + dot + newline);
                        }
                    } else if (dot < mark) {
                        setText("selection from: " + dot
                                + " to " + mark + newline);
                    } else {
                        setText("selection from: " + mark
                                + " to " + dot + newline);
                    }
                }
            });
    }
}
