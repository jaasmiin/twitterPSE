package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

/**
 * Superclass for a GUIElement containing the UpdateTypes.
 * @author Maximilian Awiszus
 * 
 */
public abstract class GUIElement implements Initializable {

    protected GUIController superController;

    /**
     * Type of update which is called on each GUIElement.
     * @author Maximilian Awiszus
     * 
     */
    public static enum UpdateType {
        TWEET, CATEGORY, LOCATION, ACCOUNT, ERROR, 
        CATEGORY_SELECTION, LOCATION_SELECTION, ACCOUNT_SELECTION, CLOSE, GUI_STARTED, MAP_DETAIL_INFORMATION, TWEET_BY_DATE
    };

    public abstract void update(UpdateType type);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        superController = GUIController.getInstance();
    }

}
