package test.gui;


import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ComponentLoader extends Application {

	private static URL location;
	
	private static Object controller;
	
	/**
	 * Call before invoking main()
	 * 
	 * @param locationOfComponent
	 */
	public static void setComponent(URL locationOfComponent) {
		ComponentLoader.location = locationOfComponent;
	}
	
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
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);			
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}				
	}
	
	public static Object getController() {
		return controller;
	}
	
	public static void closeComponent() {
		Platform.exit();
	}
	
	
	
}
