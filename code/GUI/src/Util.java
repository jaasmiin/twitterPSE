/**
 * Class with different functions used in the gui.
 * @author Maximilian Awiszus
 *
 */
public class Util {
	/**
	 * Get the text with big first letter.
	 * @param text
	 * @return text with big first letter.
	 */
	public static String getUppercaseStart(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1, text.length());
	}
}
