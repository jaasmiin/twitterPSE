package mysql;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

public class InsertDates {

    /**
     * insert dates into the database
     * 
     * @param args
     *            1..Argument: The hostname of the database to use; 2.Argument:
     *            The port of the database; 3.Argument: The name of the database
     *            to use; 4.Argument: The user of the database to connect with;
     *            5. Argument: The password for the root user of the database
     *            twitter; no more arguments are required
     * */
    public static void main(String[] args) throws ParseException {

        // try {
        // DBRead t = new DBRead(new AccessData("localhost", "3306",
        // "twitter", "root", "root"), Logger.getLogger("logger"));
        // t.connect();
        // long[] a = t.getNonVerifiedAccounts();
        // for (int i = 0; i < a.length; i++) {
        // System.out.println(a[i]);
        // }
        //
        // } catch (InstantiationException | IllegalAccessException
        // | ClassNotFoundException | SQLException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        if (args.length > 4 && args[1].matches("[0-9]+")
                && args[0].length() > 0 && args[2].length() > 0
                && args[3].length() > 0) {

            try {
                DBcrawler t = new DBcrawler(new AccessData(args[0], args[1],
                        args[2], args[3], args[4]), Logger.getLogger("logger"));
                t.connect();
                Date d = new Date();
                d = (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"))
                        .parse("01.01.2006 00:00:00");
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(d);
                System.out.println(d.toString());
                do {
                    t.writeDay(d);
                    cal.add(Calendar.DATE, 1);
                    d = cal.getTime();
                } while (d.compareTo((new SimpleDateFormat(
                        "dd.MM.yyyy HH:mm:ss")).parse("12.12.2018 00:00:00")) <= 0);
                t.disconnect();
            } catch (InstantiationException | IllegalAccessException
                    | ClassNotFoundException | SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
