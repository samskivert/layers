package view;

public class Selection {
    private String text;
    private int off;
    private int len;
    private boolean empty;

    public Selection(int off, int len, String text) {
        setAll(off, len, text);
    }

    public Selection() {
        text = "";
        off = 0;
        len = 0;
        empty = true;
    }

    public void setAll(int off, int len, String text) {
        this.text = text;
        this.off = off;
        this.len = len;
        empty = false;
    }

    public int getOffset() {
        return off;
    }

    public int getLength() {
        return len;
    }

    public String getText() {
        return text;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void empty() {
        empty = true;
    }

    public String toString() {
        if (isEmpty())
            return "<empty>";
        return new Integer(off).toString()
            + "+" + new Integer(len).toString()
            + ":\"" + text + "\"";
    }
}
