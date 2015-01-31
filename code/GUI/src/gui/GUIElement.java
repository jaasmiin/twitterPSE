package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

/**
 * 
 * @author
 * @version 1.0
 * 
 */
public abstract class GUIElement implements Initializable {

    protected GUIController superController;

    /**
     * 
     * @author
     * @version 1.0
     * 
     */
    public static enum UpdateType {
        TWEET, CATEGORY, LOCATION, ACCOUNT, ERROR, 
        CATEGORY_SELECTION, LOCATION_SELECTION, ACCOUNT_SELECTION, CLOSE, GUI_STARTED, MAP_DETAIL_INFORMATION
    };

    public abstract void update(UpdateType type);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        superController = GUIController.getInstance();
    }

}
