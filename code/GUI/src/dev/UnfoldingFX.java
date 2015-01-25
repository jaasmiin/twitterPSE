package dev;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import processing.core.PApplet;
import unfolding.MyUnfoldingMap;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UnfoldingFX extends Application {

    @Override
    public void start(Stage stage) {
        final SwingNode swingNode = new SwingNode();
        createAndSetSwingContent(swingNode);

        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);

        stage.setScene(new Scene(pane, 100, 50));
        stage.show();
    }

    private void createAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame jf = new JFrame();
                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        		jf.setBounds(100, 100, 450, 300);
        		JPanel contentPane = new JPanel();
        		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        		contentPane.setLayout(new BorderLayout(0, 0));
        		jf.setContentPane(contentPane);
        		PApplet map = new MyUnfoldingMap();
        		jf.getContentPane().add(map);
        		map.init();
        		swingNode.setContent(contentPane);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}