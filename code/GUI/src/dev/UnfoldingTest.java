package dev;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import processing.core.PApplet;
import unfolding.MyUnfoldingMap;

public class UnfoldingTest extends JFrame {

    /**
     * default serial version uid
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UnfoldingTest frame = new UnfoldingTest();
                    frame.setVisible(true);
                    frame.resize(200, 300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public UnfoldingTest() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        PApplet map = new MyUnfoldingMap();
        contentPane.add(map);
        map.init();
        map.setSize(100, 120);
    }

}
