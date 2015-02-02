package gui.timeSliderMap;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import unfolding.MyDataEntry;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;
import gui.OutputElement;

/**
 * Controls the standardMap and the DetailedInformation dialog
 * 
 * @author Matthias
 * @version 1.0
 * 
 */
public class TimeSliderMapController extends OutputElement implements
        Initializable {
    @FXML
    private Text txt_StandMap_country;
    @FXML
    private Text txt_StandMap_retweetsQuery;
    @FXML
    private Text txt_StandMap_retweetsTotal;
    @FXML
    private DatePicker date_SliderMap_startDate;
    @FXML
    private DatePicker date_SliderMap_endDate;

    private LocalDate start;
    private LocalDate end;
    private TimeSliderDialog dialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);

        dialog = new TimeSliderDialog(superController);

        // set DatePicker on Action
        date_SliderMap_startDate.setOnAction(new MyActionHandler());
        date_SliderMap_endDate.setOnAction(new MyActionHandler());

        // set default value for dateRange
        start = LocalDate.MAX;
        end = LocalDate.MIN;
    }

    @Override
    public void update(UpdateType type) {

        if (type == UpdateType.MAP_DETAIL_INFORMATION) {
            // update detailed information view
            MyDataEntry entry = superController.getMapDetailInformation();
            txt_StandMap_country.setText(entry.getCountryName());
            txt_StandMap_retweetsQuery.setText(Integer.toString(entry
                    .getRetweetsLandFiltered()));
            txt_StandMap_retweetsTotal.setText(Integer.toString(entry
                    .getRetweetsLand()));

        } else {
            dialog.update(type, date_SliderMap_startDate.getValue(), date_SliderMap_endDate.getValue());
        }
    }

    /**
     * handles all Action events in this class
     * 
     * @author Matthias
     * 
     */
    private class MyActionHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {

            if (event.getSource().equals(date_SliderMap_startDate)) {
                // set start date
                LocalDate start = date_SliderMap_startDate.getValue();
                System.out.println(start);

            }
            if (event.getSource().equals(date_SliderMap_endDate)) {
                LocalDate end = date_SliderMap_endDate.getValue();
                System.out.println(end);
            }

        }

    }

}