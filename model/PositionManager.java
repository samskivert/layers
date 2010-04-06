package model;

import java.util.*;

public class PositionManager
{
    private List<Pos> positionList;
    private static PositionManager instance;

    private PositionManager() {
        positionList = new LinkedList<Pos>();
    }

    public static PositionManager getInstance() {
        if (instance == null)
            instance = new PositionManager();
        return instance;
    }

    public Pos newPosition(int pos) {
        System.out.println("New position: " + pos);

        Pos newPos = new Pos(pos);

        int end = positionList.size();

        for (int i = 0; i < end; ++i) {
            Pos p = positionList.get(i);

            if (p.get() >= pos) {
                positionList.add(i, newPos);
                return newPos;
            }
        }

        // not added yet; add it at the end.
        if (end == positionList.size())
            positionList.add(newPos);

        return newPos;
    }

    public void bumpAfter(Pos pos, int amount) {
        boolean on = false;

        System.out.print("Position list: ");
        for (Pos p : positionList)
            System.out.print(p.get() + " ");
        System.out.println();

        System.out.println("Bumping after " + pos.get() + " by " + amount);

        for (Pos p : positionList) {
            System.out.print(p.get() + " ");

            if (on)
                p.set(p.get() + amount);
            else if (p == pos)
                on = true;
        }
        System.out.println();
    }
}
