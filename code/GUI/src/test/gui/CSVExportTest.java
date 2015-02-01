package test.gui;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.stage.Stage;
import mysql.AccessData;
import mysql.DBgui;
import util.LoggerUtil;
import gui.csvExport.CSVExportController;

/**
 * 
 * class provides testcases for the csv export function
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class CSVExportTest extends Application {

    private static final AccessData ACCESS_DATA = new AccessData(
            "172.22.214.133", "3306", "twitter", "gui", "272b28");
    private static Stage stage;

    /**
     * start a test to export data into a .csv file
     * 
     * @param args
     *            no arguments are required
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * testcase to select a file by hand
     */
    public static void test() {

        DBgui dbg = null;
        try {
            dbg = new DBgui(ACCESS_DATA, LoggerUtil.getLogger("TestLog"));
            dbg.connect();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SecurityException | IOException
                | SQLException e) {
            e.printStackTrace();
        }

        CSVExportController.exportAsCSV(dbg.getAccounts("bara"), dbg.getLocations(),
                stage);
        dbg.disconnect();
    }

    @Override
    public void start(final Stage arg0) throws Exception {
        stage = arg0;
        test();
        this.stop();
        System.exit(0);
    }

}
