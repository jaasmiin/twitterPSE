

package gui.standardMap;

import java.time.LocalDate;
import java.sql.Date;

/**
 * Util class to do some date transformation
 * @author Matthias
 *
 */
public class Dates {
    /**
     * converts LocalDate to int numbers
     * 
     * @param input
     *            LocalDate
     * @return array with 0:Year 1:Month 3:Day null if invalid input
     */
    private static int[] convertLocalDateToInt(LocalDate date) {
        if (date == null) {
            return null;
        }
        int year, month, day;
        year = date.getYear();
        month = date.getMonthValue();
        day = date.getDayOfMonth();
        int[] result = {year, month, day };
        return result;
    }

    /**
     * converts Date to int numbers
     * 
     * @param input
     *            LocalDate
     * @return array with 0:Year 1:Month 3:Day null if invalid input
     */
    private static int[] converDateToInt(Date date) {
        if (date == null) {
            return null;
        }

        int year, month, day;
        String string = date.toString();
        String[] result = string.split("-");
        if (result.length != 3) {
            return null;
        }
        year = Integer.parseInt(result[0]);
        month = Integer.parseInt(result[1]);
        day = Integer.parseInt(result[2]);
        // System.out.println(year + "  " + month + "  "+ day);
        int[] intResult = {year, month , day };
        return intResult;
    }

    /**
     * decides whether date is in a given date range
     * 
     * @param rangeStart
     *            start of range
     * @param rangeEnd
     *            end of range
     * @param date
     *            date to test
     * @return true if date is in range (date == endpoint is also true), false
     *         otherwise
     */
    public static boolean inRange(LocalDate rangeStart, LocalDate rangeEnd,
            LocalDate date) {
        if (rangeStart == null || rangeEnd == null || date == null) {
            return false;
        }
        // check intverall
        if (rangeEnd.isBefore(rangeStart)) {
            return false;
        }
        if (date.isBefore(rangeStart)) {
            return false;
        }
        if (date.isAfter(rangeEnd)) {
            return false;
        }
        return true;
    }

    /**
     * build LocalDate of given Date
     * 
     * @param date
     *            date in format Date
     * @return localDate of the same date or null if input was invalid
     */
    public static LocalDate buildLocalDate(Date date) {
        int[] result = converDateToInt(date);
        if (result == null) {
            return null;
        }
        /*
         * Month month = ; switch (result[1]) { case 1: month = Month.JANUARY;
         * break; case 2: month = Month.FEBRUARY; break; case 3: month =
         * Month.MARCH; break; case 4: month = Month.APRIL; break; case 5: month
         * = Month.MAY; break; case 6: month = Month.JUNE; break; case 7: month
         * = Month.JULY; break; case 8: month = Month.AUGUST; break; case 9:
         * month = Month.SEPTEMBER; break; case 10: month = Month.OCTOBER;
         * break; case 11: month = Month.NOVEMBER; break; case 12: month =
         * Month.DECEMBER; break; default: return null; }
         */
        LocalDate locDate = LocalDate.of(result[0], result[1], result[2]);
        return locDate;
    }
}
