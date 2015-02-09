package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import mysql.result.Account;

/**
 * class provides static utility methods
 * 
 * @author Holger Ebhart, Matthias Schimek
 * 
 */
public class Util {

    /**
     * removes \ and " from a String and cuts him off at a determined length
     * 
     * @param word
     *            the String to check
     * @param maxLength
     *            the max. length of the string as int
     * @param byDefault
     *            the String to return by default (for example if word == null)
     * @return the formatted String
     */
    public static String checkString(String word, int maxLength,
            String byDefault) {

        if (word == null) {
            return byDefault;

        } else {
            // replace " and \
            String ret = word.replace("\\", "/");
            ret = ret.replace("\"", "\"\"");
            // check for max. allowed length
            if (ret.length() > maxLength) {
                ret = ret.substring(0, maxLength);
            }
            return ret;
        }
    }

    /**
     * formats an url (removes the part "http://www." and \ and ")
     * 
     * @param url
     *            the url to format
     * @return the formatted url
     */
    public static String checkURL(String url) {
        if (url != null) {
            if (url.startsWith("http://www.")) {
                url = url.substring(11, url.length());
            } else if (url.startsWith("http://")) {
                url = url.substring(7, url.length());
            }
        }
        url = Util.checkString(url, 100, null);
        return url;
    }

    /**
     * converts an Account-Collection into a List
     * 
     * @param c
     *            the collection to convert as Collection<Account>
     * @return the converted collection as List<Account>
     */
    public static List<Account> collectionToList(Collection<Account> c) {
        List<Account> ret = new ArrayList<Account>();
        Iterator<Account> it = c.iterator();
        while (it.hasNext()) {
            ret.add(it.next());
        }
        return ret;
    }

    /**
     * method to format string to a suitable format for access to WEBSERVICES.
     * 
     * formats input string to a suitable WEBSERVICE access format. E.g. all
     * '@', '&' are deleted and the input string is encoded in URL format.
     * 
     * @param unformattedStr
     *            input string
     * @param logger
     *            logger to protocol exceptional behavior
     * @return formatted string
     */
    public static String formatString(String unformattedStr, Logger logger) {

        if (unformattedStr != null) {
            unformattedStr = unformattedStr.replace(' ', '+');
            unformattedStr = unformattedStr.replaceAll(",", "+");
            unformattedStr = unformattedStr.replaceAll(
                    "[.!#$%&'()*,/:;=?@\\[\\]]", "");
        } else {
            unformattedStr = "";
        }
        // build URL
        try {
            unformattedStr = URLEncoder.encode(unformattedStr, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            logger.info("unsupported URL Encodign Exceptin" + e.getMessage());
        }
        return unformattedStr;
    }
    
    /**
     * returns the given text with tall names
     * 
     * @param text
     *            the text to make names tall as String
     * @return the given text with tall names as String
     */
    public static String getUppercaseCountry(String text) {

        if (text == null || text.length() < 1)
            return text;

        if (text.startsWith(" ")) {
            text = text.substring(1);
        }

        // try to make the chars of nones tall

        // look for 'and', 'of' and 'the'

        // next char to uppercase
        boolean uppercase = true;

        char[] c = text.toCharArray();

        // to uppercase
        for (int i = 0; i < c.length; i++) {
            if (uppercase) {
                c[i] = Character.toUpperCase(c[i]);
                uppercase = false;
            } else {
                c[i] = Character.toLowerCase(c[i]);
            }
            if (c[i] == ' ' || c[i] == '(' || c[i] == '-') {
                uppercase = true;
            }
            if (c[i] == '.') {
                c[i - 1] = Character.toUpperCase(c[i - 1]);
            }
        }

        // look for word's of the length 2
        for (int i = 0; i < c.length - 1; i++) {

            // look for 'Of'
            if (c[i] == 'O' && c[i + 1] == 'f') {
                c[i] = 'o';
            }
        }

        // look for word's of the length 3
        for (int i = 0; i < c.length - 2; i++) {

            // look for 'And' (Attention with Andorra)
            if (c[i] == 'A' && c[i + 1] == 'n' && c[i + 2] == 'd'
                    && c[i + 3] != 'o') {
                c[i] = 'a';
            }

            // look for 'The'
            if (c[i] == 'T' && c[i + 1] == 'h' && c[i + 2] == 'e') {
                c[i] = 't';
            }

            // look for 'Da '
            if (c[i] == 'D' && c[i + 1] == 'a' && c[i + 2] == ' ') {
                c[i] = 'd';
            }

            // look for 'D'i'
            if (c[i] == 'D' && c[i + 1] == '\'' && c[i + 2] == 'i') {
                c[i] = 'd';
                c[i + 2] = 'I';
            }
        }

        c[0] = Character.toUpperCase(c[0]);

        return new String(c);
    }

}
