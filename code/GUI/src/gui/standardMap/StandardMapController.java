package gui.standardMap;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import gui.OutputElement;

public class StandardMapController extends OutputElement implements Initializable {
	private StandardMapDialog dialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
    	dialog = new StandardMapDialog(superController);
    }   
 
   @Override
    public void update(UpdateType type) {
	   dialog.update(type);
    } 

}