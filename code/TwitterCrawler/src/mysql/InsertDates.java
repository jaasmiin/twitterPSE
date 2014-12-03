package mysql;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

public class InsertDates {

    public static void main(String[] args) throws ParseException {
        try {
            DBWrite t = new DBWrite(new AccessData("localhost",
                    "3306", "twitter", "root", ""), Logger.getLogger("logger"));
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
            } while (d.compareTo((new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"))
                    .parse("12.12.2017 00:00:00")) <= 0);
            t.disconnect();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
