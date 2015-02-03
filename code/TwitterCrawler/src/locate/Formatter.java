package locate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

/**
 * Utility class that provides method(s) to format string to a suitable format
 * for access to WEBSERVICES.
 * 
 * @author Matthias
 * @version 1.0
 * 
 */
public class Formatter {


    /**
     * formats input string to a suitable WEBSERVICE access format. E.g. all
     * '@', '&' are deleted and the input string is encoded in URL format.
     * 
     * @param unformattedStr
     *            input string
     * @param Logger logger to protocol exceptional behavior
     * @return formatted string
     */
    public  static String formatString(String unformattedStr, Logger logger) {

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
}
