package gui.standardMap;

import java.net.URL;
import java.util.ResourceBundle;

import unfolding.MyDataEntry;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import gui.OutputElement;
/**
 * Controls the standardMap and the DetailedInformation dialog
 * @author Matthias
 *
 */
public class StandardMapController extends OutputElement implements Initializable {
	@FXML
	private Text txt_StandMap_country;
	@FXML 
	private Text txt_StandMap_retweetsQuery;
	@FXML
	private Text txt_StandMap_retweetsTotal;
	
	private StandardMapDialog dialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
    	dialog = new StandardMapDialog(superController);
    }   
 
   @Override
    public void update(UpdateType type) {
	   
	   
	   if (type == UpdateType.MAP_DETAIL_INFORMATION) {
		   // update detailed information view
		   MyDataEntry entry = superController.getMapDetailInformation();
		   txt_StandMap_country.setText(entry.getCountryName());
		   txt_StandMap_retweetsQuery.setText(Integer.toString(entry.getRetweetsLandFiltered()));
		   txt_StandMap_retweetsTotal.setText(Integer.toString(entry.getRetweetsLand()));
		   
	   }
	   else {
		   dialog.update(type);
	   }
    } 

}