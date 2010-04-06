package model;

import java.util.*;

public class Edit implements Comparable
{
    private Pos pos;
    private StringBuffer replaced;
    private StringBuffer text;

    public Edit(String text, int pos) {
        PositionManager pm = PositionManager.getInstance();

        this.pos = pm.newPosition(pos);
        this.replaced = new StringBuffer();
        this.text = new StringBuffer();
        this.text.append(text);
    }

    public Edit(String replaced, String added, int pos) {
        PositionManager pm = PositionManager.getInstance();

        this.pos  = pm.newPosition(pos);
        this.replaced = new StringBuffer();
        this.replaced.append(replaced);
        this.text = new StringBuffer();
        this.text.append(text);
    }

    public void setReplaced(String s) {
        this.replaced = new StringBuffer();
        this.replaced.append(s);
    }

    public StringBuffer getReplaced() {
        return replaced;
    }

    public StringBuffer getBuffer() {
        return text;
    }

    public Pos getPos() {
        return pos;
    }

    public int compareTo(Object o) {
        if (o == null)
            return -1;

        if (! (o instanceof Edit))
            return -1;

        Edit edit = (Edit)o;

        if (edit.pos == this.pos)
            return 0;

        if (edit.pos.get() > this.pos.get())
            return -1;
        return 1;
    }

    public String toString() {
        return "edit: [" + pos + ":" + text.toString() + "]";
    }

    public void dump(int indent) {
        Util.printlnWithIndent(indent, "Edit {");
        Util.printlnWithIndent(indent+2, "Position: " + pos);
        Util.printlnWithIndent(indent+2, "Text: " + text.toString());
        Util.printlnWithIndent(indent+2, "Replaced: " + replaced.toString());
        Util.printlnWithIndent(indent, "}");
    }
}
