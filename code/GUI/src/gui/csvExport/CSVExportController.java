package gui.csvExport;

import gui.InputElement;
import gui.Labels;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import mysql.result.Account;
import mysql.result.Location;
import mysql.result.Retweets;

/**
 * class provides method to export data into a csv file
 * 
 * @author Holger Ebhart and Maximilian Awiszus
 * 
 */
public class CSVExportController extends InputElement implements Initializable {

    @FXML
    private MenuItem mnFile;
    @FXML
    private MenuItem mniExport;

    /**
     * exports the current data into a csv file
     * 
     * @param accounts
     *            a list of the loaded accounts as List<Account>
     * @param locations
     *            a list of the available locations as List<Location>
     * @param stage
     *            the parent stage to show the save-file-dialog as final Stage
     * @return true if the export was successful, else false
     */
    public static boolean exportAsCSV(List<Account> accounts,
            List<Location> locations, final Stage stage) {
        String path = getFilePath(stage);
        if (path == null)
            return false;
        File file = new File(path);
        String[][] string = buildFile(accounts, locations);
        writeFile(file, string);
        return true;
    }

    private static String getFilePath(final Stage stage) {

        // only show .csv files
        ExtensionFilter ef = new ExtensionFilter("csv", "*.csv");

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(ef);
        fc.setTitle("Speichern unter...");

        String path = null;
        // get file path
        File file = fc.showSaveDialog(stage);
        if (file != null) {

            path = file.getAbsolutePath();
            if (path != null) {
                if (!path.endsWith(".csv")) {
                    path = path + ".csv";
                }
            }
        }

        return path;
    }

    private static void writeFile(File file, String[][] string) {

        Writer w;
        try {
            w = new FileWriter(file);
        } catch (IOException e1) {
            return;
        }
        BufferedWriter writer = new BufferedWriter(w);

        // write file line per line
        for (String[] x : string) {
            // build line
            String line = x[0];
            for (int i = 1; i < x.length; i++) {
                line += "," + (x[i] == null ? "0" : x[i]);
            }

            try {
                // write line
                writer.append(line);
                writer.newLine();
            } catch (IOException e) {
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
        }

    }

    private static String[][] buildFile(List<Account> accounts,
            List<Location> locations) {

        HashMap<String, Integer> h = new HashMap<String, Integer>();

        // write header information
        String[][] file = new String[accounts.size() + 1][locations.size() + 3];
        file[0][0] = "Accountname";
        file[0][1] = "Country";
        file[0][2] = "Follower";
        int i = 3;
        for (Location l : locations) {
            file[0][i] = l.toString();
            h.put((l.getLocationCode().equals("0") ? "-" : l.getLocationCode()), i);
            i++;
        }

        // write data into String field
        int j = 1;
        for (Account a : accounts) {
            file[j][0] = a.getName();
            file[j][1] = a.getLocationCode();
            file[j][2] = String.valueOf(a.getFollower());

            for (Retweets r : a.getRetweets()) {
                file[j][h.get(r.getLocationCode())] = String.valueOf(r
                        .getCounter());
            }

            j++;
        }

        return file;
    }

    @Override
    public void update(UpdateType type) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
        mnFile.setText(Labels.FILE);
        mniExport.setText(Labels.EXPORT);
        mniExport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                superController.setInfo(Labels.EXPORTING);
                if (exportAsCSV(superController.getDataByAccount(),
                        superController.getLocations(),
                        superController.getStage())) {
                    superController.setInfo(Labels.EXPORTED, Labels.EXPORTING);
                } else {
                    superController.setInfo(Labels.EXPORT_FAILED,
                            Labels.EXPORTING);
                }
            }
        });
    }
}
