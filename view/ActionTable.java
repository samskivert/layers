package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import java.io.*;

class ActionTable {
    private HashMap<Object, Action> actions;

    public ActionTable(JTextComponent textComp) {
        actions = new HashMap<Object, Action>();
        Action[] actionsArray = textComp.getActions();

        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
    }

    public Action getActionByName(String name) {
        return actions.get(name);
    }
}
