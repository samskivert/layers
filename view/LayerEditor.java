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

public class LayerEditor extends JFrame
{
    java.util.List editors;
    MiniEditor editor;
    //MiniEditor edRight;
    JTextArea changeLog;
    LayersSideList layersList;
    ControlPanel controlPanel;

    JTabbedPane tabbedPane;

    File currentFile;
    //File currentFileRight;

    private static LayerEditor instance;
    static {
        /*
          this is kind of a hack. many actions that happen within
          the body of the LayerEditor constructor demand an instance
          of the editor. so the constructor initializes "instance" to
          "this" and proceeds. i'm not sure of this, but presumably
          these actions don't depend on stuff that happens later in
          the constructor.
        */
        new LayerEditor();
    }

    public MiniEditor getEditor() {
        return editor;
    }   

    public void addLayerListener(ILayerListener listener)
    {
        editor.addLayerListener(listener);
    }

    public static LayerEditor getInstance() {
        if (instance == null) {
            System.out.println("Editor instance does not yet exist!");
            System.exit(1);
        }
        return instance;
    }


    /* this constructor is kind of a fragile mess; can't really be
       bothered to figure out right now how to get windows to resize
       properly, etc.
    */
    private LayerEditor() { 
        super("Layer");

        instance = this;

        // the editing window
        editor = new MiniEditor(defaultAttr());

        // put them in scroll panes
        JScrollPane scrollPaneLeft = new JScrollPane(editor.getTextPane());
        scrollPaneLeft.setPreferredSize(new Dimension(400, 400));

        layersList = new LayersSideList();

        // toss it in a scroll pane
        JScrollPane sideListScrollPane = new JScrollPane(layersList);

        // status log
        changeLog = new JTextArea(5, 30);
        changeLog.setEditable(false);
        JScrollPane scrollPaneForLog = new JScrollPane(changeLog);
        scrollPaneForLog.setPreferredSize(new Dimension(4, 200));

        // XXX dead code for now
        tabbedPane = new LayerTabbedPane();
        tabbedPane.addTab(editor.getName(), scrollPaneLeft);

        // split pane for the split pane above and the text log
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                              scrollPaneLeft, 
                                              sideListScrollPane);
        splitPane.setDividerLocation(530);
        splitPane.setOneTouchExpandable(true);
                                              

        //Create the status area.
        JPanel statusPane = new JPanel(new GridLayout(1, 1));
        CaretListenerLabel caretListenerLabel =
            new CaretListenerLabel("Caret Status", editor.getTextPane()); // XXX

        statusPane.add(caretListenerLabel);

        //Add the components.
        getContentPane().add(splitPane, BorderLayout.PAGE_START);
        getContentPane().add(statusPane, BorderLayout.PAGE_END);

        // add bindings
        addBindings(editor.getTextPane());

        createMenuBar();

        //Put the initial text into the text pane.
        try {
            initEditor(editor, new StringReader(""));
        } catch (Exception e) {
            System.out.println("Shit\n");
            System.exit(0);
        }
    }

    public void setControlListener(ControlListener cl) {
        controlPanel.setListener(cl);
    }

    private void createMenuBar() {
        JMenu fileMenu = createFileMenu();
        JMenu editMenu = createEditMenu();
        JMenuBar mb = new JMenuBar();

        mb.add(fileMenu);
        mb.add(editMenu);

        setJMenuBar(mb);
    }

    //Add a couple of emacs key bindings for navigation.
    protected void addBindings(JTextPane textPane) {
        InputMap inputMap = textPane.getInputMap();

        //Ctrl-b to go backward one character
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.backwardAction);

        //Ctrl-f to go forward one character
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.forwardAction);

        //Ctrl-p to go up one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.upAction);

        //Ctrl-n to go down one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.downAction);
    }

    //Create the edit menu.
    protected JMenu createFileMenu() {
        JMenu menu = new JMenu("File");
        
        FileOpenAction openAction = new FileOpenAction();
        FileSaveAction saveAction = new FileSaveAction();
        StateSaveAction stateAction = new StateSaveAction();
        ExitAction exitAction = new ExitAction();

        menu.add(openAction);
        menu.add(saveAction);
        menu.add(stateAction);
        menu.add(exitAction);

        return menu;
    }

    protected JMenu createEditMenu() {
        JMenu menu = new JMenu("Edit");

        //Undo and redo are actions of our own creation.
        //undoAction = new UndoAction();
        menu.add(editor.getUndoManager().createUndoAction());
        //menu.add(edRight.getUndoManager().createUndoAction());

        //redoAction = new RedoAction();
        menu.add(editor.getUndoManager().createRedoAction());
        //menu.add(edRight.getUndoManager().createRedoAction());

        menu.addSeparator();

        //These actions come from the default editor kit.
        //Get the ones we want and stick them in the menu.
        menu.add(editor.getActionTable().getActionByName(DefaultEditorKit.cutAction));
        menu.add(editor.getActionTable().getActionByName(DefaultEditorKit.copyAction));
        menu.add(editor.getActionTable().getActionByName(DefaultEditorKit.pasteAction));

        menu.addSeparator();

        menu.add(editor.getActionTable().getActionByName(
                                         DefaultEditorKit.selectAllAction));
        return menu;
    }

    private Color colors[] = {
        Color.cyan,
        Color.magenta,
        Color.lightGray,
        Color.orange,
        Color.yellow
    };

    private int colorsIdx = 0;

    public SimpleAttributeSet defaultAttr()
    {
        SimpleAttributeSet attr = new SimpleAttributeSet();

        StyleConstants.setFontFamily(attr, "Courier");
        StyleConstants.setFontSize(attr, 14);
        StyleConstants.setBackground(attr, Color.white);

        return attr;
    }

    public AttributeSet peekBgAttr() {
        SimpleAttributeSet attr = (SimpleAttributeSet)defaultAttr().clone();

        StyleConstants.setBackground(attr, colors[colorsIdx]);

        return attr;
    }

    public AttributeSet nextBgAttr() {
        AttributeSet attr = peekBgAttr();

        if (++colorsIdx == colors.length)
            colorsIdx = 0;

        return attr;
    }

    public void setSelectionColors(Selection left, Selection right) {
        StyledDocument st = (StyledDocument)editor.getDocument();
        AttributeSet attr = nextBgAttr();

        st.setCharacterAttributes(left.getOffset(), left.getLength(), attr, true);
    }

    protected void initEditor(MiniEditor ed, Reader in) {
        int buflen = 500;
        char buf[] = new char[buflen];

        SimpleAttributeSet attr = defaultAttr();
        Document doc = ed.getDocument();

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

    class ExitAction extends AbstractAction {
        public ExitAction() {
            super("Quit");
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    class StateSaveAction extends AbstractAction {
        public StateSaveAction() {
            super("Save state");
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("Saving state not yet implemented");
        }
    }

    class FileSaveAction extends AbstractAction {
        public FileSaveAction() {
            super("Save file");
        }

        public void actionPerformed(ActionEvent e) {
            if (currentFile == null) {
                JFileChooser fc = new JFileChooser();

                int ret = fc.showSaveDialog(LayerEditor.getInstance());

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    try {
                        file.createNewFile();
                    } 
                    catch (IOException ioe) {
                        System.out.println("File create failed");
                        return;
                    }

                    currentFile = file;
                } 
                else {
                    return;
                }
            }

            try {
                PrintStream ps = new PrintStream(currentFile);
                Document doc = editor.getDocument();
                String contents = doc.getText(0, doc.getLength());

                ps.println(contents);
                ps.flush();
                ps.close();
            } 
            catch (FileNotFoundException fnfe) {
                System.out.println("File not found: " + currentFile.getPath());
                Control.failedException(fnfe);
            } 
            catch (BadLocationException ble) {
                Control.failedException(ble);
            }
        }
    }

    class FileOpenAction extends AbstractAction {
        public FileOpenAction() {
            super("Open");
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();

            int ret = fc.showOpenDialog(LayerEditor.getInstance());

            if (ret == JFileChooser.APPROVE_OPTION)
                loadFile(fc.getSelectedFile());
        }
    }

    public void loadFile(File file) {
        currentFile = file;
        reloadFile(editor, file);
    }

    private void reloadFile(MiniEditor ed, File file) 
    {
        try {
            ed.getDocument().remove(0, ed.getDocument().getLength());
            initEditor(ed, new BufferedReader(new FileReader(file)));
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

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        final LayerEditor frame = getInstance();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void bootEditor()
    {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    createAndShowGUI();
                }
            });
    }

    public LayersSideList getLayersSideList()
    {
        return layersList;
    }
}
