package model;

import java.util.*;

class DepPair
{
    Layer parent;
    List<Layer> children;

    public DepPair(Layer parent, List<Layer> children) {
        this.parent = parent;
        this.children = children;
    }

    public Layer getParent() {
        return parent;
    }

    public List<Layer> getChildren() {
        return children;
    }
}

class DepDB
{
    List<DepPair> dependencies;
    static DepDB instance;

    private DepDB() {
        dependencies = new ArrayList<DepPair>();
    }

    public static DepDB getInstance() {
        if (instance == null)
            instance = new DepDB();
        return instance;
    }

    public boolean hasDep(Layer parent, Layer child) {
        List<Layer> children = getChildren(parent);

        if (children == null)
            return false;

        for (Layer l : children) {
            if (l.equals(child))
                return true;
            if (hasDep(l, child))
                return true;
        }
        return false;
    }

    public boolean addDep(Layer parent, Layer child) {
        System.out.println("adddep parent is null");

        if (hasDep(child, parent))
            return false;
        if (hasDep(parent, child))
            return true;

        List<Layer> children = getChildren(parent);

        if (children == null) {
            children = new ArrayList<Layer>();
            dependencies.add(new DepPair(parent, children));
        }

        children.add(child);

        return true;
    }

    public List<Layer> getChildren(Layer parent) {
        for (DepPair pair : dependencies) {
            if (pair == null)
                System.out.println("pair null");
            if (pair.getParent() == null)
                System.out.println("parent null");
            if (pair.getParent().equals(parent))
                return pair.getChildren();
        }

        return null;
    }

    public List<Layer> getParents(Layer child) {
        List<Layer> ret = new ArrayList<Layer>();

        for (DepPair pair : dependencies) {
            for (Layer l : pair.getChildren())
                if (child.equals(l)) {
                    ret.add(pair.getParent());
                    break;
                }
        }

        return ret;
    }
}

public class LayerDB
{
    List<Layer> layers;
    //Set layers;
    List<DepPair> dependencies;
    Layer currentLayer;
    static LayerDB instance;

    private LayerDB() {
        layers = new ArrayList();
        dependencies = new ArrayList<DepPair>();
    }

    public static LayerDB getInstance() {
        if (instance == null)
            instance = new LayerDB();
        return instance;
    }

    public List getLayers() {
        return layers;
    }

    public Layer getNextActiveLayer(Layer l) {
        Layer ret = null;

        for (Layer layer : layers) {
            if (layer.isOn())
                ret = layer;
            if (layer.equals(l))
                return ret;
        }

        return null;
    }

    public Layer getBaseLayer() {
        for (Layer layer : layers) {
            if (layer.isBase())
                return layer;
        }

        return null;
    }

    public void addLayer(Layer layer) {
        System.out.println("adding " + layer);
        layers.add(layer);
    }

    public Layer getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(Layer l) {
        currentLayer = l;
    }

    public Layer editOverlaps(int pos) {
        return editOverlaps(pos, currentLayer);
    }

    // XXX only works for inserts currently
    public Layer editOverlaps(int pos, Layer avoidLayer)
    {
        int minDistance = Util.BIG_NUM;
        Layer ret = null;

        for (Layer layer : layers) {
            if (! layer.isOn())
                continue;

            if (layer == avoidLayer)
                continue;

            for (Object o1 : layer.getEdits()) {
                Edit edit = (Edit) o1;

                if (pos > edit.getPos().get() &&
                    pos <= edit.getPos().get() + edit.getBuffer().length())
                {
                    int distance = pos - edit.getPos().get();

                    if (distance <= minDistance) {
                        minDistance = distance;
                        ret = layer;
                    }
                }
            }
        }

        return ret;
    }
    
    public boolean addDependency(Layer parent, Layer child)
    {
        DepDB db = DepDB.getInstance();
        return db.addDep(parent, child);
    }

    public List<Layer> getChildren(Layer parent)
    {
        DepDB db = DepDB.getInstance();
        return db.getChildren(parent);
    }

    public List<Layer> getParents(Layer child) {
        DepDB db = DepDB.getInstance();
        return db.getParents(child);
    }

    public void dump(int indent) {
        Util.printIndent(indent);
        System.out.println("LAYER DATABASE:");
        for (Layer layer : layers) {
            layer.dump(indent + 2);
        }
    }
}
