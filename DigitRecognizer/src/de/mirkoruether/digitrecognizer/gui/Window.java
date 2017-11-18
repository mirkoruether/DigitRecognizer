package de.mirkoruether.digitrecognizer.gui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class Window extends JFrame
{
    private static final long serialVersionUID = -7431265748893293519L;

    private final JTabbedPane tabs;
    private final PredictPanel predictPanel;

    public Window()
    {
        this.tabs = new JTabbedPane();
        this.predictPanel = new PredictPanel();

        tabs.add("Ziffererkennung", predictPanel);
        add(tabs);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(() -> new Window().setVisible(true));
    }
}
