package model;

public class Pos
{
    int position;

    public Pos(int position) {
        this.position = position;
    }

    public int get() {
        return position;
    }

    public void set(int position) {
        this.position = position;
    }

    public String toString() {
        return position + "";
    }
}
