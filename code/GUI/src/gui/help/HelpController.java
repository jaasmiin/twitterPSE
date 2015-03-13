package gui.help;

import gui.GUIElement;
import gui.Labels;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controls the help dialog
 * 
 * @author Matthias Schimek
 */
public class HelpController extends GUIElement implements Initializable {
    @FXML
    private MenuItem help_about;
    @FXML
    private Label about_version;
    @FXML
    private Label about_authors;
    @FXML
    private Menu menu_help;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (help_about != null) {
        	menu_help.setText(Labels.HELP);
        	help_about.setText(Labels.ABOUT);
            help_about.setOnAction(new MyActionEventHandler());
        }
        if (about_version != null) {
            about_version.setText(Labels.ABOUT_VERSION);
            about_authors.setText(Labels.ABOUT_AUTHORS);
        }

    }

    @Override
    public void update(UpdateType type) {

    }
    
    /**
     * shows the about dialog
     */
    private void showAboutDialog() {
    	Parent parent = null;
		try {
			parent = FXMLLoader.load(getClass().getResource("About.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (parent != null) {
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("About");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setHeight(125);
        stage.setWidth(425);
        stage.show();
		}
    }

    /**
     * Special ActionEvent handler for this class
     * 
     * @author Matthias
     * 
     */
    private class MyActionEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (event.getSource().equals(help_about)) {
                showAboutDialog();
            }
        }

    }

}
