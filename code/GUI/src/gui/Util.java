package gui;

/**
 * Class with different functions used in the gui.
 * 
 * @author Maximilian Awiszus
 * @version 1.0
 * 
 */
public class Util {
    /**
     * Get the text with big first letter.
     * 
     * @param text
     *            the text where the first letter should be big as String
     * @return text with big first letter.
     */
    public static String getUppercaseStart(String text) {
        return text.substring(0, 1).toUpperCase()
                + text.substring(1, text.length());
    }

    /**
     * Get the text with big first letter and the rest small
     * 
     * @param text
     *            the text where the first letter should be big as String
     * @return text with big first letter and the rest small
     */
    public static String getUppercaseStartAndRestLowerCase(String text) {
        return text.substring(0, 1).toUpperCase()
                + text.substring(1, text.length()).toLowerCase();
    }

}
