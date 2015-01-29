package dev;

import gui.RunnableParameter;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import processing.core.PApplet;
import unfolding.MyUnfoldingMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UnfoldingFX extends Application {

    @Override
    public void start(final Stage stage) {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
		        



			}
		}).start();

    }

    public static void main(String[] args) {
        launch(args);
    }
}