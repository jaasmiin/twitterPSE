package test.gui;

import static org.junit.Assert.*;
import gui.standardMap.Dates;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.Test;

/**
 * Class to test the functions of the standardMap concerning the chosen
 * period of time. 
 * @author Lidia
 *
 */
public class StandardMapTest {

    /**
     * Tests if Dates method inRange() returns correct value.
     */
    @Test
    public void testInRange1() {
        LocalDate startDate = LocalDate.of(1, 1, 1);
        LocalDate endDate = LocalDate.of(1000, 10, 22);
        LocalDate inBetween = LocalDate.of(922, 2, 2);
        assertTrue(Dates.inRange(startDate, endDate, inBetween));
    }

    /**
     * Tests if Dates method inRange() returns correct value.
     */
    @Test
    public void testInRange2() {
        LocalDate startDate = LocalDate.of(1, 1, 1);
        LocalDate endDate = LocalDate.of(1000, 10, 22);
        LocalDate inBetween = LocalDate.of(1000, 10, 22);
        assertTrue(Dates.inRange(startDate, endDate, inBetween));
    }

    /**
     * Tests if Dates method inRange() returns correct value.
     */
    @Test
    public void testInRange3() {
        LocalDate startDate = LocalDate.of(1, 1, 1);
        LocalDate endDate = LocalDate.of(1000, 10, 22);
        LocalDate inBetween = LocalDate.of(1, 1, 1);
        assertTrue(Dates.inRange(startDate, endDate, inBetween));
    }

    /**
     * Tests if correct localDate is build.
     */
    @Test
    public void testBuildLocalDate1() {
        Date date = new Date(System.currentTimeMillis());
        LocalDate cur = LocalDate.now();
        LocalDate con = Dates.buildLocalDate(date);
        assertTrue(con.getDayOfMonth() == cur.getDayOfMonth()
                && con.getYear() == cur.getYear()
                && con.getMonth() == cur.getMonth());
    }

    /**
     * Tests if correct localDate is build.
     */
    @Test
    public void testBuildLocalDate2() {

        Date date = new Date(0);
        LocalDate cur = LocalDate.of(1970, 1, 1);
        LocalDate con = Dates.buildLocalDate(date);
        System.out.println(date);
        assertTrue(con.getDayOfMonth() == cur.getDayOfMonth()
                && con.getYear() == cur.getYear()
                && con.getMonth() == cur.getMonth());
    }

}
