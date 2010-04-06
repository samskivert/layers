package model.edittree;

import java.util.*;

class Edit {
    Chunk parentChunk;
    List<Chunk> childChunks;

    public Edit (Chunk parentChunk, List<Chunk> childChunks) {
        this.parentChunk = parentChunk;
        this.childChunks = childChunks;
    }

    public void render() {
        for (Chunk c : childChunks) {
            c.render();
        }
    }
}

class Chunk {
    String text;
    Edit replacement;

    public Chunk (String text, Edit replacement) {
        this.text = text;
        this.replacement = replacement;
    }

    public void render() {
        if (replacement != null)
            replacement.render();
        else
            System.out.print(text);
    }
}

public class EditTree { 
    public EditTree() {
        Chunk c1 = new Chunk("Hello", null);
        Chunk c2 = new Chunk("Marius", null);

        List<Chunk> list = new ArrayList<Chunk>();
        list.add(c1);
        list.add(c2);

        Edit e = new Edit(null, list);
        e.render();
        System.out.println();
    }
}
