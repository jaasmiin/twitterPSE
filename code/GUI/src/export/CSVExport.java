package export;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * class provides method to export data into a csv file
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class CSVExport {

    private static String getFilePath() {

        // only show .csv files
        FileNameExtensionFilter f = new FileNameExtensionFilter(
                "Plaintext: csv", "csv");

        JFileChooser fc = new JFileChooser();
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setFileFilter(f);
        fc.setDialogTitle("Speichern unter...");
        fc.setVisible(true);

        // fc.showSaveDialog(arg0);
        //
        // int result = fc.showSaveDialog();

        String path = "";
        // if (result == JFileChooser.APPROVE_OPTION) {
        // path = fc.getSelectedFile().toString();
        // }
        // fc.setVisible(false);

        return path;
    }

    public static void exportAsCSV() {
        String path = getFilePath();

    }

}
