package locate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;
/**
 * Utility class that provides  method(s) to format string to a suitable format for access to WEBSERVICES.
 * @author Matthias
 *
 */
public class Formatter {
	private Logger logger;
	public Formatter(Logger logger) {
		this.logger = logger;
	}
	/**
     * formats input string to a suitable WEBSERVICE access format. E.g. all '@', '&' are deleted
     * and the input string is encoded in URL format.
     * @param unformattedStr input string
     * @return formatted string
     */
    public String formatString(String unformattedStr) {
    	
    	if (unformattedStr != null) {
            unformattedStr = unformattedStr.replace(' ', '+');
            unformattedStr = unformattedStr.replaceAll(",", "+");
            unformattedStr = unformattedStr.replaceAll("[.!#$%&'()*,/:;=?@\\[\\]]", "");
        }
    	else {
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
