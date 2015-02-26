package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mysql.result.Account;

import org.junit.Test;

import util.Util;

/**
 * testclass to test the util-methods f the class util
 * 
 * @author Holger Ebhart
 * 
 */
public class UtilTest {

    /**
     * check the normalization of a String
     */
    @Test
    public void test1CheckString() {
        assertEquals("Hallo", Util.checkString("Hallo", 10, null));
    }

    /**
     * check the normalization of a String
     */
    @Test
    public void test2CheckString() {
        assertEquals("Hallo", Util.checkString("Halloz", 5, null));
        assertEquals("1", Util.checkString("1234567890", 1, null));
        assertEquals("", Util.checkString("Hallo", 0, null));
    }

    /**
     * check the normalization of a String
     */
    @Test
    public void test3CheckString() {
        assertEquals("default", Util.checkString(null, 10, "default"));
        assertNull(Util.checkString(null, 10, null));
    }

    /**
     * check the normalization of an url
     */
    @Test
    public void test1CheckURL() {
        assertEquals("url", Util.checkURL("url"));
        assertEquals(
                "zehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbol",
                Util.checkURL("zehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolzehnSymbolurl"));
    }

    /**
     * check the normalization of an url
     */
    @Test
    public void test2CheckURL() {
        assertEquals("", Util.checkURL("http://www."));
        assertEquals("", Util.checkURL("http://"));
        assertEquals("http://", Util.checkURL("http://www.http://"));

        assertEquals("url.de", Util.checkURL("http://www.url.de"));
        assertEquals(".de", Util.checkURL("http://.de"));
    }

    /**
     * check the normalization of an url
     */
    @Test
    public void test3CheckURL() {
        assertNull(Util.checkURL(null));
        assertEquals("", Util.checkURL(""));
    }

    /**
     * test to convert a collection into a list with invalid parameters
     */
    @Test
    public void test1CollectionToList() {
        assertEquals(0, Util.collectionToList(null).size());
    }

    /**
     * test to convert a collection into a list with empty collection
     */
    @Test
    public void test2CollectionToList() {
        // ArrayList
        List<Account> t1 = new ArrayList<Account>();
        assertEquals(0, Util.collectionToList(t1).size());
        // HashSet
        HashSet<Account> t2 = new HashSet<Account>();
        assertEquals(0, Util.collectionToList(t2).size());
        // HashMap
        HashMap<Account, Object> t3 = new HashMap<Account, Object>();
        assertEquals(0, Util.collectionToList(t3.keySet()).size());
        // Set
        Set<Account> t4 = new TreeSet<Account>();
        assertEquals(0, Util.collectionToList(t4).size());
    }

    /**
     * test to convert a collection into a list
     */
    @Test
    public void test3CollectionToList() {
        // ArrayList
        List<Account> t1 = new ArrayList<Account>();
        t1.add(new Account(1, "name", 0));
        List<Account> l1 = Util.collectionToList(t1);
        assertEquals(1, l1.size());
        assertEquals(1, l1.get(0).getId());
        // HashSet
        HashSet<Account> t2 = new HashSet<Account>();
        t2.add(new Account(1, "name", 0));
        t2.add(new Account(2, "xyz", 10));
        List<Account> l2 = Util.collectionToList(t2);
        assertEquals(2, l2.size());
        assertEquals(1, l2.get(0).getId());
        assertEquals(2, l2.get(1).getId());
        // HashMap
        HashMap<Account, Object> t3 = new HashMap<Account, Object>();
        t3.put(new Account(1, "name", 0), new Object());
        t3.put(new Account(2, "name", 0), new Object());
        List<Account> l3 = Util.collectionToList(t3.keySet());
        assertEquals(2, l3.size());
        assertEquals(1, l3.get(0).getId());
        assertEquals(2, l3.get(1).getId());
        // Set
        Set<Account> t4 = new LinkedHashSet<Account>();
        t4.add(new Account(1, "name1", 0));
        t4.add(new Account(2, "name2", 0));
        t4.add(new Account(3, "name3", 0));
        List<Account> l4 = Util.collectionToList(t4);
        assertEquals(3, l4.size());
        assertEquals(1, l4.get(0).getId());
        assertEquals(2, l4.get(1).getId());
        assertEquals(3, l4.get(2).getId());
    }

    /**
     * test is countries were formated right
     */
    @Test
    public void test1GetUppercaseCountry() {
        assertNull(Util.getUppercaseCountry(null));
        assertEquals("", Util.getUppercaseCountry(""));
        assertEquals("", Util.getUppercaseCountry(" "));
    }

    /**
     * test is countries were formated right
     */
    @Test
    public void test2GetUppercaseCountry() {
        assertEquals("Deutschland", Util.getUppercaseCountry("Deutschland"));
        assertEquals("England", Util.getUppercaseCountry(" england"));
        assertEquals("Germany", Util.getUppercaseCountry("gERMANY"));
        assertEquals("U.S.", Util.getUppercaseCountry("u.s."));
        assertEquals("(Germany and Germany)",
                Util.getUppercaseCountry("(gERMANY AND gERMANY)"));
        assertEquals("U of A", Util.getUppercaseCountry("u OF a"));
        assertEquals("Countrya, Countryb, Countryc",
                Util.getUppercaseCountry("COUNTRYa, COUNTRYB, COUNTRYc"));
    }

    /**
     * test is countries were formated right
     */
    @Test
    public void test3GetUppercaseCountry() {
        assertEquals("Cote d'Ivore", Util.getUppercaseCountry("COTE D'IVORE"));
        assertEquals("Blabla da Bla", Util.getUppercaseCountry("BLABLA DA BLA"));
        assertEquals("The King", Util.getUppercaseCountry("THE kING"));
        assertEquals("The the the", Util.getUppercaseCountry("tHE THE THE"));
    }

}
