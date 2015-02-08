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
