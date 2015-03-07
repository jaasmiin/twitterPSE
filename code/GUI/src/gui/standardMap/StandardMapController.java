package gui.standardMap;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import unfolding.MyDataEntry;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;
import gui.Labels;
import gui.OutputElement;

/**
 * Controls the standardMap and the DetailedInformation dialog
 * 
 * @author Matthias
 * 
 */
public class StandardMapController extends OutputElement implements
        Initializable {
    @FXML
    private Tab tabStandardMap;
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
    @FXML
    private Button b_StandMap_confirm;
    @FXML
    private Button b_StandMap_reset;
    @FXML
    private Text lbl_StandMap_retweetsQuery;
    @FXML
    private Text lbl_StandMap_retweetsTotal;
    @FXML
    private Button b_StandMap_StartDateShow;
    @FXML
    private Button b_StandMap_StopDateShow;

    private LocalDate start = null;
    private LocalDate end = null;
    private StandardMapDialog dialog;
    private Thread t1 = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
        dialog = new StandardMapDialog(superController);

        // set DatePicker on Action
        // set text
        setText();
        date_SliderMap_startDate.setOnAction(new MyActionHandler());
        date_SliderMap_endDate.setOnAction(new MyActionHandler());

        b_StandMap_confirm.setOnMouseClicked(new MyEventHandler());

        b_StandMap_reset.setOnMouseClicked(new MyEventHandler());
        b_StandMap_StartDateShow.setOnMouseClicked(new MyEventHandler());
        b_StandMap_StopDateShow.setOnMouseClicked(new MyEventHandler());

    }

    /**
     * sets texts
     */
    private void setText() {
        b_StandMap_confirm.setText(Labels.STANDMAP_CONFIRM);
        b_StandMap_reset.setText(Labels.STANDMAP_RESET);
        tabStandardMap.setText(Labels.DETAIL_INFORMATION);
        b_StandMap_StartDateShow.setText(Labels.START_SHOW);
        b_StandMap_StopDateShow.setText(Labels.STOP_SHOW);
    }

    @Override
    public void update(UpdateType type) {
        if (type == UpdateType.MAP_DETAIL_INFORMATION) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    // update detailed information view
                    MyDataEntry entry = superController
                            .getMapDetailInformation();
                    setVisible(true);
                    txt_StandMap_country.setText(entry.getCountryName());
                    txt_StandMap_retweetsQuery.setText(Integer.toString(entry
                            .getRetweetsLandFiltered()));
                    txt_StandMap_retweetsTotal.setText(Integer.toString(entry
                            .getRetweetsLand()));
                }
            });
        } else {
            dialog.update(type, start, end);
        }
    }

    private void setVisible(boolean visible) {
        lbl_StandMap_retweetsQuery.setVisible(visible);
        lbl_StandMap_retweetsTotal.setVisible(visible);
        txt_StandMap_country.setVisible(visible);
        txt_StandMap_retweetsQuery.setVisible(visible);
        txt_StandMap_retweetsTotal.setVisible(visible);
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
                start = date_SliderMap_startDate.getValue();
                // System.out.println("Mein Start datum" + start);

            }
            if (event.getSource().equals(date_SliderMap_endDate)) {
                end = date_SliderMap_endDate.getValue();
                // System.out.println("Mein end datum" + end);
            }

        }

    }

    private class MyEventHandler implements EventHandler<Event> {

        @Override
        public void handle(Event event) {
            if (event.getSource().equals(b_StandMap_confirm)) {
                // System.out.println("Mein Start datum: "
                // + date_SliderMap_startDate.getValue());
                // System.out.println("Mein end datum: "
                // + date_SliderMap_endDate.getValue());
                dialog.update(UpdateType.TWEET_BY_DATE,
                        date_SliderMap_startDate.getValue(),
                        date_SliderMap_endDate.getValue());
            }
            if (event.getSource().equals(b_StandMap_reset)) {
                date_SliderMap_startDate.setValue(null);
                date_SliderMap_endDate.setValue(null);
                dialog.update(UpdateType.TWEET_BY_DATE,
                        date_SliderMap_startDate.getValue(),
                        date_SliderMap_endDate.getValue());
            }
            if (event.getSource().equals(b_StandMap_StartDateShow)) {
                // start Date show
                LocalDate start;
                LocalDate end;
                DateShow show;
                // System.out.println("Mein Start datum: "
                // + date_SliderMap_startDate.getValue());
                // System.out.println("Mein end datum: "
                // + date_SliderMap_endDate.getValue());
                // set start and end dates
                if (date_SliderMap_startDate.getValue() == null) {

                    start = LocalDate.of(2015, 1, 20);
                } else {
                    start = date_SliderMap_startDate.getValue();
                }

                if (date_SliderMap_endDate.getValue() == null) {
                    end = LocalDate.now();
                } else {
                    end = date_SliderMap_endDate.getValue();
                }

                superController.setDontLoadFromDB(true);
                show = new DateShow(superController, start, end);
                t1 = new Thread(show);
                t1.start();
            }
            if (event.getSource().equals(b_StandMap_StopDateShow)) {
                if (t1 != null) {
                    t1.interrupt();
                    superController.setDontLoadFromDB(false);
                }
            }

        }

    }

}