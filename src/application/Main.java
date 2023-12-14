package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	static final String JDBC_URL = "jdbc:mysql://database-violetsassignment.cwiyapmmjmbk.ap-southeast-2.rds.amazonaws.com:3306/db_assignment";
	static final String USERNAME = "admin";
	static final String PASSWORD = "qazxswedc";
	Connection connection;

	BorderPane root;

	VBox homepageBox;
	GridPane registerGrid;
	GridPane loginGrid;

	String btnTextColor = "#003366";
	String bgColor = "#CCCCCC";

	List<Button> buttonList = new ArrayList<>();
	List<Label> pageTitles = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new BorderPane();
			Scene scene = new Scene(root, 640, 480);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("CipherCraft");
			primaryStage.setScene(scene);
			primaryStage.show();

			// set pages layout
			this.setHomePage();

			this.setStyle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void connectToDatabase() {
		try {
			// Register JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Open a connection
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			// Execute a query
			System.out.println("Creating statement...");
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM Users";

			ResultSet resultSet = statement.executeQuery(sql);
			// 1/ Go through the result set to print it
			while (resultSet.next()) {
				// Retrieve data by column name
				String userName = resultSet.getString("userName");
				String userPass = resultSet.getString("userPass");

				System.out.println("userName: " + userName + " / userPass: " + userPass);
			}
			// Close external resources
			resultSet.close();
			statement.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setStyle() {
		// set buttons styles
		for (Button btn : buttonList) {
			btn.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + btnTextColor + "; " + "-fx-border-radius: 4px;"
					+ "-fx-padding: 2px 6px;" + "-fx-font-size: 16px;" + "-fx-text-fill: " + btnTextColor + ";"
					+ "-fx-font-family: \"Arial\";" + "-fx-opacity: 0.7; ");
		}

	}

	private void setHomePage() {
		homepageBox = new VBox();
		homepageBox.setAlignment(Pos.CENTER);
		homepageBox.setSpacing(10);

		Label homepageTitle = new Label("Welcome to CipherCraft");

		Button loginButton = new Button("Login");
		loginButton.setOnAction(action -> {
			root.setCenter(loginGrid);
		});

		Button registerButton = new Button("Register");
		registerButton.setOnAction(action -> {
			this.setRegisterPage();
			root.setCenter(registerGrid);
		});

		buttonList.add(loginButton);
		buttonList.add(registerButton);
		homepageTitle.setFont(new Font(20));
		homepageBox.getChildren().addAll(homepageTitle, loginButton, registerButton);
		root.setCenter(homepageBox);
	}

	private void setRegisterPage() {
		String newUsername = null;
		String password = null;

		registerGrid = new GridPane();
		registerGrid.setAlignment(Pos.CENTER);

		Label registerTitle = new Label("Create your account");
		GridPane.setColumnSpan(registerTitle, 2);

		Label usernameLable = new Label("Username: ");
		TextField usernameTextField = new TextField();
		Label usernameTip = new Label("A valid username use only letters, numbers, or symbols (@, ., -, _), and have a "
				+ "length between 6 and 8 characters.");
		usernameTip.setWrapText(true);
		usernameTip.setMaxWidth(200);
		usernameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				String checkResult = this.usernameValidity(usernameTextField.getText().trim());
				usernameTip.setText(checkResult);
				if (checkResult.equals("Username is valid")) {
					usernameTip.setTextFill(Color.GREEN);
				} else {
					usernameTip.setTextFill(Color.RED);
				}
			}
		});

		Label passwordLable = new Label("Password: ");
		PasswordField passwordField = new PasswordField();
		Label passwordTip = new Label(
				"A valid password must include at least one digit, one uppercase letter, one lowercase letter, "
						+ "one special character from (@, ., -, _), and have a length between 8 and 10 characters.");
		passwordTip.setWrapText(true);
		passwordTip.setMaxWidth(200);
		passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				String checkResult = this.passwordValidity(passwordField.getText().trim());
				passwordTip.setText(checkResult);
				if (checkResult.equals("Password is valid")) {
					passwordTip.setTextFill(Color.GREEN);
				} else {
					passwordTip.setTextFill(Color.RED);
				}
			}
		});

		Label confirmPasswordLable = new Label("Confirm Password: ");
		PasswordField confirmPasswordField = new PasswordField();
		Label confirmPasswordTip = new Label();
		confirmPasswordTip.setWrapText(true);
		confirmPasswordTip.setMaxWidth(200);
		confirmPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				String checkResult = this.confrimPasswordValidity(confirmPasswordField.getText().trim(),
						passwordField.getText().trim());
				confirmPasswordTip.setText(checkResult);
				if (checkResult.equals("Password has been comfirmed")) {
					confirmPasswordTip.setTextFill(Color.GREEN);
				} else {
					confirmPasswordTip.setTextFill(Color.RED);
				}
			}
		});

		Button sumbitButton = new Button("Submit");
		sumbitButton.setOnAction(action -> {

		});

		Button backButton = new Button("Back");
		backButton.setOnAction(action -> {
			root.setCenter(homepageBox);
		});

		GridPane.setColumnSpan(registerTitle, 2);
		registerGrid.setVgap(4);
		registerGrid.setHgap(10);
		registerGrid.setPadding(new Insets(5, 5, 5, 5));
		registerGrid.add(registerTitle, 0, 0);
		registerGrid.add(usernameLable, 0, 1);
		registerGrid.add(usernameTextField, 1, 1);
		registerGrid.add(usernameTip, 1, 2);
		registerGrid.add(passwordLable, 0, 3);
		registerGrid.add(passwordField, 1, 3);
		registerGrid.add(passwordTip, 1, 4);
		registerGrid.add(confirmPasswordLable, 0, 5);
		registerGrid.add(confirmPasswordField, 1, 5);
		registerGrid.add(confirmPasswordTip, 1, 6);
		registerGrid.add(sumbitButton, 0, 7);
		registerGrid.add(backButton, 1, 7);

		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setHalignment(HPos.CENTER);
		registerGrid.getColumnConstraints().add(columnConstraints);
	}

	private String confrimPasswordValidity(String confirmPassword, String password) {
		String tipText;

		if (confirmPassword.isEmpty()) {
			tipText = "Confrim Password cannot be empty";
		} else if (confirmPassword.equals(password)) {
			tipText = "Password has been comfirmed";
		} else {
			tipText = "The passwords entered do not match. Please ensure they are the same.";
		}

		return tipText;
	}

	private String passwordValidity(String newPassword) {
		String regex = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[@._-]).{8,10}$";
		String tipText;
		if (newPassword.isEmpty()) {
			tipText = "Password cannot be empty. A valid password must include at least one digit, one uppercase letter, one lowercase letter, one special character from (@, ., -, _), and have a length between 8 and 10 characters.";
		} else if (!newPassword.matches(regex)) {
			tipText = "Invalid password. A valid password must include at least one digit, one uppercase letter, one lowercase letter, one special character from (@, ., -, _), and have a length between 8 and 10 characters.";
		} else {
			tipText = "Password is valid";
		}
		return tipText;
	}

	private String usernameValidity(String newUsername) {
		String regex = "^[a-zA-Z0-9@._-]{6,8}$";
		String tipText;
		if (newUsername.isEmpty()) {
			tipText = "Username cannot be empty. A valid username use only letters, numbers, or symbols (@, ., -, _), and have a length between 6 and 8 characters.";
		} else if (!newUsername.matches(regex)) {
			tipText = "Invalid username. A valid username use only letters, numbers, or symbols (@, ., -, _), and have a length between 6 and 8 characters.";
		} else if (this.dbUsernameTaken(newUsername)) {
			tipText = "This username is already in use. A valid username use only letters, numbers, or symbols (@, ., -, _), and have a length between 6 and 8 characters.";
		} else {
			tipText = "Username is valid";
		}
		return tipText;
	}

	private boolean dbUsernameTaken(String newUsername) {
		try {
			// Register JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Open a connection
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			// Execute a query
			System.out.println("Creating statement...");
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM users_info where user_name = \"" + newUsername + "\"";

			ResultSet resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				return true;
			}
			// Close external resources
			resultSet.close();
			statement.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
