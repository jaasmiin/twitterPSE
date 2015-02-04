package application;

import javax.swing.JFrame;

import unfolding.examples.ChoroplethMapApp;

public class RunMain {

	public RunMain() {
		
	}

	public static void main(String[] args) {
		ChoroplethMapApp map = new ChoroplethMapApp();
		map.init();
		
		JFrame mapFrame = new JFrame();
		mapFrame.setSize(400, 300);
		
		mapFrame.getContentPane().add(map);
		
		// hides the frame containing close, minimize, ... buttons
		mapFrame.setUndecorated(true);
		
		
		mapFrame.setVisible(true);
		
		
	}
	
}
