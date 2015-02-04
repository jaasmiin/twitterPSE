package gui;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/**
 * Class to show an information on a ListView.
 * @author Maximilian Awiszus
 */
public class InfoRunnable implements Runnable {
	private String text;
	private ListView<String> list;
	private Integer delay;
	/**
	 * Create a new InfoRunnable.
	 * @param list where the message should be displayed
	 * @param text which should be set
	 * @param delay in which the text is going to disappear
	 */
	public InfoRunnable(ListView<String> list, String text, Integer delay) {
		this(list, text);
		this.delay = delay;
	}
	/**
	 * Create a new InfoRunnable.
	 * @param list where the message should be displayed
	 * @param text which should be set for 3s.
	 */
	public InfoRunnable(ListView<String> list, String text) {
		super();
		this.list = list;
		this.text = text;
		delay = 3000;
	}
	@Override
	public void run() {
		new Thread(new RunnableParameter<String>(text) {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						list.getItems().remove(parameter);
					}
				});
				
			}
		}).start();

	}
}
