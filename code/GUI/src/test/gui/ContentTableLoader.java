package test.gui;


import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Loads an instance of ContentTableController.
 * 
 * @author Philipp
 *
 */
public class ContentTableLoader extends Application {

	private static URL location;
	
	private static Object controller;
	
	/**
	 * Call before invoking main()
	 * 
	 * @param locationOfComponent a unique identifier for the location of the fxml-File
	 * of the ContentTable
	 */
	public static void setComponent(URL locationOfComponent) {
		ContentTableLoader.location = locationOfComponent;
	}
	
	/**
	 * Launches the Application.
	 * 
	 * @param args the command line arguments. None expected.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		if (location == null) {
			throw new IllegalStateException("Location of Component is not set!");
		}
		
		try {
			FXMLLoader loader = new FXMLLoader(location);
			Tab root = (Tab) loader.load();
			
			controller = loader.getController();
			
			BorderPane bPane = new BorderPane();
			TabPane tPane = new TabPane();
			tPane.getTabs().add(root);
			bPane.getChildren().add(tPane);
			
			Scene scene = new Scene(bPane);
			primaryStage.setScene(scene);			
			primaryStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	
	/**
	 * Gets the instance of ContentTableController that controls the launched application.
	 * @return the instance of ContentTableController that controls the launched application
	 */
	public static Object getController() {
		return controller;
	}
	
	/**
	 * Closes the Application.
	 */
	public static void closeComponent() {
		Platform.exit();
	}
	
	
	
}
