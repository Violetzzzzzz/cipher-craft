package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
	BorderPane root;
	GridPane homepageGrid;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			this.setHomePage();
			this.setRegisterPage();
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setHomePage() {
		homepageGrid = new GridPane();
		Label homepageTitle = new Label("Welcome to CipherCraft");
		Button loginButton = new Button();
		Button registerButton = new Button();

		GridPane.setColumnSpan(homepageTitle, 2);
		homepageGrid.setVgap(4);
		homepageGrid.setHgap(10);
		homepageGrid.setPadding(new Insets(5, 5, 5, 5));
		homepageGrid.add(homepageTitle, 0, 0);
		homepageGrid.add(loginButton, 0, 2);
		homepageGrid.add(registerButton, 0, 4);
	}

	private void setRegisterPage() {

	}

	public static void main(String[] args) {
		launch(args);
	}
}
