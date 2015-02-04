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
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controls the help dialog
 * 
 * @author Matthias Schimek
 */
public class HelpController extends GUIElement implements Initializable {
    @FXML
    private MenuItem help_about;
    @FXML
    private Text about_version;
    @FXML
    private Text about_authors;

    private Stage dialogStage;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (help_about != null) {
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
        FXMLLoader loader = new FXMLLoader(
                HelpController.class.getResource("About.fxml"));

        AnchorPane page;

        try {

            page = (AnchorPane) loader.load();
            dialogStage = new Stage();

            // give the controller in the newly created thread a reference to
            // the current stage;
            ((HelpController) loader.getController())
                    .setCurrentStage(dialogStage);

            dialogStage.setTitle("About");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.show();
        } catch (IOException e1) {

            e1.printStackTrace();
        }
    }

    /**
     * Sets the current stage
     * 
     * @param stage
     *            current stage
     */
    public void setCurrentStage(Stage stage) {
        dialogStage = stage;
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
