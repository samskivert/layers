package model;

import java.util.*;

public class Layer implements Comparable
{
    private String name;
    private boolean on;
    private Set edits;
    private Edit currentEdit;

    public Layer() { }

    public Layer(String name) {
        this.name = name;
        this.edits = new TreeSet();
        on = false;
    }

    public Set getEdits() { 
        return edits; 
    }

    public Edit removeText(int pos, String text) {
        System.out.println("Layer.removeText: " + pos + ", " + text);

        //if (isBase())
        //  return null;

        int position = pos; //.get();

        for (Object o : edits) {
            Edit edit = (Edit)o;
            int editPosition = edit.getPos().get();
            StringBuffer editBuffer = edit.getBuffer();

            if (position >= editPosition &&
                position < editPosition + editBuffer.length())
            {
                System.out.println("POsition: " + position);
                System.out.println("Edit position: " + editPosition);

                String removed = editBuffer.substring(position - editPosition,
                                                      position - editPosition + text.length());
                editBuffer.delete(position - editPosition,
                                  position - editPosition + text.length());

                System.out.println("Removed: " + removed);
                return edit;
            }

            if (pos + text.length() == edit.getPos().get()) {
                System.out.println("success");

                StringBuffer replacedBuffer = edit.getReplaced();
                replacedBuffer.insert(0, text);

                edit.getPos().set(pos);
                return edit;
            }

            System.out.println("Looked at " + edit.getPos().get());
            System.out.println(" with " + pos + " and " + text);
        }

        System.out.println("returning null");

         Edit e = new Edit(text, "", pos);
         edits.add(e);

        return null;
    }

    public Edit addText(int pos, String text) {
        System.out.println("Layer.setEdit: " + pos + ", " + text);

        /* XXX problem: here and in removeText:

           This code basically assumes that there are no edits
           depending on this edit... If other edits sit on this edit,
           then the position calculation is going to be wrong. 

           The position computation is dependent on the presence of
           child layers...

           Hmmmrmrmrmrmmr -- might have to rebuild afterall.

         */

        //if (isBase()) 
        //  return null;

        if (currentEdit == null) {
            currentEdit = new Edit(text, pos);
            edits.add(currentEdit);
            return currentEdit;
        }

        if (pos == currentEdit.getPos().get() +
                   currentEdit.getBuffer().length())
        {
            currentEdit.getBuffer().append(text);
            return currentEdit;
        }
        else {
            boolean inEdit = false;

            for (Object o : edits) {
                Edit e = (Edit)o;

                if (pos >= e.getPos().get() 
                    && pos <= e.getPos().get() + e.getBuffer().length())
                {
                    e.getBuffer().insert(pos - e.getPos().get(), text);
                    inEdit = true;
                    System.out.println("in edit");
                    return e;
                }
            }

            if (!inEdit) {
                System.out.println("new edit");
                currentEdit = new Edit(text, pos);
                edits.add(currentEdit);
                return currentEdit;
            }
        }

        return null;
    }

    public void turnOn() {
        on = true;
    }

    public void turnOff() {
        on = false;
    }

    public boolean isOn() {
        return on;
    }
    
    public String getName() { 
        return name; 
    }

    public boolean isBase() { 
        return false;
    }

    public int compareTo(Object o) {
        // XXX hack
        if (! (o instanceof Layer))
            return -1;

        Layer layer = (Layer)o;

        if (layer.name.equals(name))
            return 0;

        else
            return 1;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Layer)) {
            return false;
        }
        Layer l = (Layer)o;
        return this.name.equals(l.getName());
    }

    // assuming layers' names are unique, which is the case
    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return name + ", " + (on ? "on" : "off");
    }

    public void dump(int indent) {
        Util.printlnWithIndent(indent, "Layer Name: " + name);
        for (Object o : edits) {
            ((Edit)o).dump(indent + 2);
        }
    }
}
