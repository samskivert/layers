package view;

import javax.swing.text.*;

public interface ILayerListener
{
    public void setDocument(Document doc);
    public void textRemoved(String text, int pos);
    public void textAdded(String text, int pos);
    public void textReplaced(String oldText, String newText, int pos);
}
