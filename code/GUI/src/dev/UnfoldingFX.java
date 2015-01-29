package dev;

import javafx.application.Application;
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