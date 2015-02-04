package application;
	
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("MapView.fxml"));
			BorderPane root = (BorderPane) loader.load();
			final MapController mapctrl = (MapController) loader.getController();
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("UnfoldingIntegration");
			primaryStage.show();
			
			mapctrl.positionFrame();
			
			scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent> () {

				@Override
				public void handle(WindowEvent event) {
					event.consume();
					mapctrl.closeMap();
				}
				
			});
			
			scene.widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					mapctrl.positionFrame();
					
				}
			});
			
			scene.heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					mapctrl.positionFrame();
					
				}				
			});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
