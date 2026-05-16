package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.MainWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/MainWindow.fxml"));
	    Parent root = loader.load();

	    Scene scene = new Scene(root, 1000, 700);

	    primaryStage.setTitle("Music Score Viewer");
	    primaryStage.setScene(scene);

	    primaryStage.setMinWidth(900);
	    primaryStage.setMinHeight(600);

	    primaryStage.show();
	}

    public static void main(String[] args) {
        launch(args);
    }
}