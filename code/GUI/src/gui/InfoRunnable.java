package gui;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class InfoRunnable implements Runnable {
	private String text;
	private ListView<String> list;
	private Integer delay;
	public InfoRunnable(ListView<String> list, String text, Integer delay) {
		this(list, text);
		this.delay = delay;
	}
	public InfoRunnable(ListView<String> list, String text) {
		super();
		this.list = list;
		this.text = text;
		delay = 2000;
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
