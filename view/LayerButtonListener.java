package view;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class LayerButtonListener implements ActionListener
{
    public void actionPerformed(ActionEvent evt)
    {
        //System.out.println("Click!");
        NewLayerWindow w = new NewLayerWindow();
    }
}
