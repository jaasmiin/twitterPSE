package gui;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class InfoRunnable implements Runnable {
	private String text;
	private Label label;
	private Integer delay;
	public InfoRunnable(Label label, String text, Integer delay) {
		this(label, text);
		this.delay = delay;
	}
	public InfoRunnable(Label label, String text) {
		super();
		this.label = label;
		this.text = text;
	}
	@Override
	public void run() {
		label.setText(text);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						label.setText("");
					}
				});
				
			}
		}).start();

	}
}
