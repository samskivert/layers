package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import java.io.*;

import controller.Control;

public class MiniEditor {
    private ActionTable actions;
    private JTextPane textPane;
    private AbstractDocument doc;
    private Selection selection;
    private ArrayList<IVibeListener> listeners;
    private VibeUndo undo;
    private static final int MAX_CHARACTERS = 100000;
    private boolean inSelectionClear;
    private AttributeSet currentAttr;
    private File file;

    private class VibeStyledDocument extends DefaultStyledDocument
    {
        public void remove(int off, int len)
            throws BadLocationException
        {
            String str = getText(off, len);

            for (IVibeListener lst : listeners) {
                lst.textRemoved(str, off);
            }

            selection.empty();

            super.remove(off, len);
        }

        protected void insertUpdate(AbstractDocument.DefaultDocumentEvent evt,
                                    AttributeSet set)
        {
            super.insertUpdate(evt, currentAttr);
            //super.insertUpdate(evt, VibeEditor.getInstance().nextBgAttr());
        }
    }

    /* i'm implementing kind of a wonky way to get selections.
       there's probably an easier way to get them through the Document interface...
    */
    private class VibeCaretListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {
            // don't process if we're calling setSelectionStart/setSelectionEnd internally
            if (inSelectionClear)
                return;

            int dot = e.getDot();
            int mark = e.getMark();

            if (dot != mark) {
                int off = 0;
                int len = 0;

                off = dot > mark ? mark : dot;
                len = dot > mark ? dot - mark : mark - dot;

                try {
                    selection.setAll(off, len, doc.getText(off, len));
                    System.out.println("updated selection: " + selection.getText());
                } catch (BadLocationException ble) {
                    System.out.println("Bad location exception in caretUpdate");
                    Control.failedException(ble);
                } 
            }
            else {
                selection.empty(); // XXX not sure if this is right...
            }
        }
    }

    private class VibeUndoableEditListener implements UndoableEditListener
    {
        public void undoableEditHappened(UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.getUndoManager().addEdit(e.getEdit());
            undo.getUndoAction().updateUndoState();
            undo.getRedoAction().updateRedoState();
        }
    }

    //And this one listens for any changes to the document.
    protected class VibeDocumentListener implements DocumentListener
    {
        public void insertUpdate(DocumentEvent e) {
            int off = e.getOffset();
            int len = e.getLength();
            Document parent = e.getDocument();
            String text = "";

            try {
                text = parent.getText(off, len);
            } catch (BadLocationException ble) {
                Control.failedException(ble);
                //System.out.println("Bad location\n");
            }

            for (IVibeListener lst : listeners) {
                if (! selection.isEmpty()) {
                    lst.textReplaced(selection.getText(), text, off);
                    selection.empty();
                } 
                else {
                    lst.textAdded(text, off);
                }
            }

            //displayEditInfo(e);
        }
        /* This method is worthless for getting the text that was
         * actually removed. For that purpose I overrode remove() in
         * Document. 
         */
        public void removeUpdate(DocumentEvent e) {
            //displayEditInfo(e);
        }
        public void changedUpdate(DocumentEvent e) {
            //displayEditInfo(e);
        }
        private void displayEditInfo(DocumentEvent e) {
//             Document document = e.getDocument();
//             int changeLength = e.getLength();
//             changeLog.append(e.getType().toString() + ": " +
//                 changeLength + " character" +
//                 ((changeLength == 1) ? ". " : "s. ") +
//                 " Text length = " + document.getLength() +
//                 "." + newline);
        }
    }

    private JTextPane createEditorPane(StyledDocument doc) {
        JTextPane tp = new JTextPane();
        
        tp.setCaretPosition(0);
        tp.setMargin(new Insets(5, 5, 5, 5));
        tp.addCaretListener(new VibeCaretListener());
        tp.setStyledDocument(doc);

        return tp;
    }

    private AbstractDocument createDocument(Document styledDoc) {
        AbstractDocument absDoc = null;

        if (styledDoc instanceof AbstractDocument) {
            absDoc = (AbstractDocument)styledDoc;
        } else {
            System.err.println("Text pane's document isn't an AbstractDocument!");
            System.exit(-1);
        }

        absDoc.setDocumentFilter(new DocumentSizeFilter(MAX_CHARACTERS));
        absDoc.addUndoableEditListener(new VibeUndoableEditListener());
        absDoc.addDocumentListener(new VibeDocumentListener());

        return absDoc;
    }

    public MiniEditor(AttributeSet attr) {
        selection = new Selection();
        listeners = new ArrayList<IVibeListener>();
        undo = new VibeUndo();

        VibeStyledDocument styledDoc = new VibeStyledDocument();
        StyleContext stctx = new StyleContext();
        StyleContext.NamedStyle st = stctx.new NamedStyle();
        st.addAttributes(attr);

        styledDoc.setLogicalStyle(0,st);

        doc = createDocument(styledDoc);
        textPane = createEditorPane(styledDoc);

        actions = new ActionTable(textPane);

        currentAttr = attr;
    }

    public String getName() {
        if (this.file == null)
            return "* new file *";
        else
            return this.file.getName();
    }

    public boolean hasFile() {
        return this.file != null;
    }

    public void loadFile(File file)
    {
        try {
            this.file = file;
            getDocument().remove(0, getDocument().getLength());
            initialize(new BufferedReader(new FileReader(file)));
        }
        catch (BadLocationException ble) {
            System.out.println("Could not reload file");
            Control.failedException(ble);
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("File not found");
            Control.failedException(fnfe);
        }
    }

    public void initialize(Reader in) {
        int buflen = 500;
        char buf[] = new char[buflen];

        VibeEditor editor = VibeEditor.getInstance();

        SimpleAttributeSet attr = editor.defaultAttr();
        Document doc = getDocument();

        while (true) {
            int bytes = 0;
            try {
                bytes = in.read(buf, 0, buflen);

                if (bytes > 0) {
                    String str = new String(buf, 0, bytes);
                    
                    doc.insertString(doc.getLength(), str, attr);
                }

                if (bytes < buflen)
                    break;

            } catch (IOException e) {
                System.out.println("Couldn't read character");
                Control.failedException(e);
            } catch (BadLocationException e) {
                System.out.println("Couldn't insert text");
                Control.failedException(e);
            }
        }

        return;
    }

    public void setAttributes(AttributeSet attr) {
        currentAttr = attr;
    }

    public AttributeSet getAttributes() {
        return currentAttr;
    }

//     public void changeStyle() {
//         StyleContext stctx = new StyleContext();
//         StyleContext.NamedStyle st = stctx.new NamedStyle();
//         st.addAttributes(VibeEditor.getInstance().nextBgAttr());
//         if (doc instanceof VibeStyledDocument) {
//             VibeStyledDocument styledDoc = (VibeStyledDocument)doc;
//             styledDoc.setLogicalStyle(0, st);
//         }
//     }

    public JTextPane getTextPane() {
        return textPane;
    }

    public AbstractDocument getDocument() {
        return doc;
    }

    public VibeUndo getUndoManager() {
        return undo;
    }

    public ActionTable getActionTable() {
        return actions;
    }

    public Selection getSelection() {
        return selection;
    }

    public void clearSelection() {
        inSelectionClear = true;

        selection.empty();
        textPane.setSelectionStart(0);
        textPane.setSelectionEnd(0);

        inSelectionClear = false;
    }

    public String getAllText() {
        try {
            return doc.getText(0, doc.getLength());
        } catch (BadLocationException ble) {
            Control.failedException(ble);
            return "";
        }
    }

    public void addVibeListener(IVibeListener listener)
    {
        listener.setDocument(doc);
        listeners.add(listener);
    }
}
