package util;

/**
 * class provides static utility methods
 * 
 * @author Holger Ebhart
 * @version 1.0
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
            String ret = word.replace("\\", "/");
            ret = ret.replace("\"", "\"\"");
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

}
