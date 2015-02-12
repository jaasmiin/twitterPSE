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
	/**
	 * Contains the supercontroller.
	 */
    protected GUIController superController;

    /**
     * Type of update which is called on each GUIElement.
     * @author Maximilian Awiszus
     * 
     */
    public static enum UpdateType {
    	/** Data has changed */
    	TWEET,
    	/** Categories have changed */
    	CATEGORY, 
    	/** Locations have changed */
        LOCATION,
        /** Accounts have changed */
        ACCOUNT,
        /** A error has happened */
        ERROR, 
        /** Selection of categories has changed */
        CATEGORY_SELECTION,
        /** Selection of locations has changed */
        LOCATION_SELECTION,
        /** Selection of accounts has changed */
        ACCOUNT_SELECTION,
        /** The main application closes */
        CLOSE, GUI_STARTED,
        /** The map detail information has changed */
        MAP_DETAIL_INFORMATION,
        /** Data grouped by date has changed */
        TWEET_BY_DATE,
        /** The main application was resized */
        WINDOW_RESIZE,
        MAP_SELECTED,
        MAP_UNSELECTED,
        WINDOW_HIDING
    };

    /**
     * Update the GUIElement.
     * @param type of Update
     */
    public abstract void update(UpdateType type);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        superController = GUIController.getInstance();
    }

}
