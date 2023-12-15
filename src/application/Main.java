package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
	GridPane userGrid;

	String btnTextColor = "#003366";
	String bgColor = "#CCCCCC";

	AES aesCipherForKey;

//	List<Button> buttonList = new ArrayList<>();
//	List<Label> pageTitles = new ArrayList<>();

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
//			this.getKeyFromFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleMouseClick(MouseEvent event) {
		if (event.getTarget() instanceof GridPane) {
			root.requestFocus();
		}
	}

	private void getKeyFromFile() throws NoSuchAlgorithmException {
		aesCipherForKey = new AES();
		CaesarCipher cc = new CaesarCipher();
		String filePath = "kk.bin";
		try {
			if (!Files.exists(Path.of(filePath))) {
				Files.createFile(Path.of(filePath));
				SecretKey secretkeyskey = aesCipherForKey.getSecretkey();
				byte[] encryptedKeysKey = cc.binaryCipher(secretkeyskey.getEncoded());
				Files.write(Path.of(filePath), encryptedKeysKey, StandardOpenOption.WRITE);
				System.out.println("Data has been written to file: " + filePath);
			} else {
				byte[] encryptedKeysKey = Files.readAllBytes(Path.of(filePath));
				byte[] decryptedKeysKey = cc.deBinaryCipher(encryptedKeysKey);
				SecretKey secretkeyskey = new SecretKeySpec(decryptedKeysKey, 0, decryptedKeysKey.length, "AES");
				aesCipherForKey.setSecretkey(secretkeyskey);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// set buttons styles
	private void setButtonStyle(Button btn) {
		btn.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + btnTextColor + "; " + "-fx-border-radius: 4px;"
				+ "-fx-padding: 2px 6px;" + "-fx-font-size: 16px;" + "-fx-text-fill: " + btnTextColor + ";"
				+ "-fx-font-family: 'Arial';" + "-fx-opacity: 0.7; ");

	}

	private void setTitleLabelStyle(Label lb) {
		lb.setStyle("-fx-font-size: 20px; " + "-fx-font-family: 'Arial'; " + "-fx-text-fill: blue; "
				+ "-fx-font-weight: bold;");
	}

	private void setHomePage() {
		homepageBox = new VBox();
		homepageBox.setAlignment(Pos.CENTER);
		homepageBox.setSpacing(10);

		Label homepageTitle = new Label("Welcome to CipherCraft");
		this.setTitleLabelStyle(homepageTitle);

		Button loginButton = new Button("Login");
		this.setButtonStyle(loginButton);
		loginButton.setOnAction(action -> {
			this.setLoginPage();
			root.setCenter(loginGrid);
			root.requestFocus();
		});

		Button registerButton = new Button("Register");
		this.setButtonStyle(registerButton);
		registerButton.setOnAction(action -> {
			this.setRegisterPage();
			root.setCenter(registerGrid);
			root.requestFocus();
		});

		homepageBox.getChildren().addAll(homepageTitle, loginButton, registerButton);
		root.setCenter(homepageBox);
		homepageBox.requestFocus();
	}

	private void setLoginPage() {
		loginGrid = new GridPane();
		loginGrid.setAlignment(Pos.CENTER);
		loginGrid.setOnMouseClicked(this::handleMouseClick);

		Label loginTitle = new Label("Login");
		this.setTitleLabelStyle(loginTitle);
		GridPane.setColumnSpan(loginTitle, 2);

		Label usernameLable = new Label("Username: ");
		TextField usernameTextField = new TextField();
		Label usernameTip = new Label();
		usernameTip.setWrapText(true);
		usernameTip.setMaxWidth(200);
		usernameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				if (usernameTextField.getText().trim().isEmpty()) {
					usernameTip.setText("Username cannot be empty.");
					usernameTip.setTextFill(Color.RED);
				} else {
					usernameTip.setText("");
				}
			}
		});

		Label passwordLable = new Label("Password: ");
		PasswordField passwordField = new PasswordField();
		Label passwordTip = new Label();
		passwordTip.setWrapText(true);
		passwordTip.setMaxWidth(200);
		passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				if (passwordField.getText().trim().isEmpty()) {
					passwordTip.setText("Password cannot be empty.");
					passwordTip.setTextFill(Color.RED);
				} else {
					passwordTip.setText("");
				}
			}
		});

		Button loginButton = new Button("Login");
		this.setButtonStyle(loginButton);
		loginButton.setOnAction(action -> {
			String usernameInput = usernameTextField.getText().trim();
			String passwordInput = passwordField.getText().trim();
			if (!usernameInput.isEmpty() && !passwordInput.isEmpty()) {
				try {
					try {
						if (this.authenticateLogin(usernameInput, passwordInput)) {
							this.setUserPage();
							root.setCenter(userGrid);
						} else {

						}
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
							| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		Button backButton = new Button("Back");
		this.setButtonStyle(backButton);
		backButton.setOnAction(action -> {
			root.setCenter(homepageBox);
			homepageBox.requestFocus();
		});

		GridPane.setColumnSpan(loginTitle, 2);
		loginGrid.setVgap(4);
		loginGrid.setHgap(10);
		loginGrid.setPadding(new Insets(5, 5, 5, 5));
		loginGrid.add(loginTitle, 0, 0);
		loginGrid.add(usernameLable, 0, 2);
		loginGrid.add(usernameTextField, 1, 2);
		loginGrid.add(usernameTip, 1, 3);
		loginGrid.add(passwordLable, 0, 4);
		loginGrid.add(passwordField, 1, 4);
		loginGrid.add(passwordTip, 1, 5);
		loginGrid.add(loginButton, 0, 6);
		loginGrid.add(backButton, 1, 6);

		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setHalignment(HPos.CENTER);
		loginGrid.getColumnConstraints().add(columnConstraints);
	}

	private void setUserPage() {
		userGrid = new GridPane();
		userGrid.setAlignment(Pos.CENTER);
		userGrid.setOnMouseClicked(this::handleMouseClick);
		Label userTitle = new Label("Login successfully!");
		this.setTitleLabelStyle(userTitle);

		GridPane.setColumnSpan(userTitle, 2);
		userGrid.setVgap(4);
		userGrid.setHgap(10);
		userGrid.setPadding(new Insets(5, 5, 5, 5));
		userGrid.add(userTitle, 0, 0);
	}

	private void setRegisterPage() {
		registerGrid = new GridPane();
		registerGrid.setAlignment(Pos.CENTER);
		registerGrid.setOnMouseClicked(this::handleMouseClick);

		Label registerTitle = new Label("Create your account");
		this.setTitleLabelStyle(registerTitle);

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

		Label submitTip = new Label();
		Button sumbitButton = new Button("Submit");
		this.setButtonStyle(sumbitButton);
		sumbitButton.setOnAction(action -> {
			GridPane.setColumnSpan(submitTip, 2);
			String usernameInput = usernameTextField.getText().trim();
			String passwordInput = passwordField.getText().trim();
			String confirmPasswordInput = confirmPasswordField.getText().trim();
			String usernameValidityTip = usernameTip.getText().trim();
			String passwordValidityTip = passwordTip.getText().trim();
			String confirmPasswordValidity = confirmPasswordTip.getText().trim();

			if (usernameValidityTip.equals("Username is valid")) {
				if (passwordValidityTip.equals("Password is valid")) {
					if (confirmPasswordValidity.equals("Password has been comfirmed")) {
						if (passwordInput.equals(confirmPasswordInput)) {
							try {
								this.processRegistrationInfo(usernameInput, passwordInput);
							} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
									| InvalidAlgorithmParameterException | IllegalBlockSizeException
									| BadPaddingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						submitTip.setText("Please confirm the password before submit");
					}
				} else {
					submitTip.setText("Please enter a valid password before submit");
				}
			} else {
				submitTip.setText("Please enter a valid username before submit");
			}

		});

		Button backButton = new Button("Back");
		this.setButtonStyle(backButton);
		backButton.setOnAction(action -> {
			root.setCenter(homepageBox);
			homepageBox.requestFocus();
		});

		GridPane.setColumnSpan(registerTitle, 2);
		registerGrid.setVgap(4);
		registerGrid.setHgap(10);
		registerGrid.setPadding(new Insets(5, 5, 5, 5));
		registerGrid.add(registerTitle, 0, 0);
		registerGrid.add(usernameLable, 0, 2);
		registerGrid.add(usernameTextField, 1, 2);
		registerGrid.add(usernameTip, 1, 3);
		registerGrid.add(passwordLable, 0, 4);
		registerGrid.add(passwordField, 1, 4);
		registerGrid.add(passwordTip, 1, 5);
		registerGrid.add(confirmPasswordLable, 0, 6);
		registerGrid.add(confirmPasswordField, 1, 6);
		registerGrid.add(confirmPasswordTip, 1, 7);
		registerGrid.add(submitTip, 0, 8);
		registerGrid.add(sumbitButton, 0, 9);
		registerGrid.add(backButton, 1, 9);

		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setHalignment(HPos.CENTER);
		registerGrid.getColumnConstraints().add(columnConstraints);
	}

	private boolean authenticateLogin(String usernameInput, String passwordInput)
			throws ClassNotFoundException, SQLException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// Register JDBC driver
		Class.forName("com.mysql.cj.jdbc.Driver");
		// Open a connection
		System.out.println("Connecting to database...");
		connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
		// Execute a query

		byte[] passphrase = this.getPassphrase();

		// for test
		System.out.println("Checking username..." + usernameInput);
//		`user_id`, `user_password`, `user_key`

		String sql = "SELECT user_id, AES_DECRYPT(user_name, ?) AS decrypted_username, user_password, user_key FROM users_info WHERE user_name = AES_ENCRYPT(?, ?);";
		System.out.println("Creating prepared statement...");
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setBytes(1, passphrase);
		preparedStatement.setString(2, usernameInput);
		preparedStatement.setBytes(3, passphrase);
		ResultSet resultSet = preparedStatement.executeQuery();

		// for test
//		System.out.println("resultnext..." + resultSet.next());
		while (resultSet.next()) {
			System.out.println("get data...");
			int id = resultSet.getInt("user_id");
			String username = resultSet.getString("decrypted_username");
			byte[] encryptedPassword = resultSet.getBytes("user_password");
			byte[] encryptedKey = resultSet.getBytes("user_key");

			// for test
			System.out.println("id..." + id);
			System.out.println("username" + username);

			if (this.checkPassword(passwordInput, encryptedPassword, encryptedKey)) {
				return true;
			}

		}
		// Close external resources
		resultSet.close();
		preparedStatement.close();
		connection.close();
		return false;
	}

	private boolean checkPassword(String passwordInput, byte[] encryptedPassword, byte[] encryptedKey)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		this.getKeyFromFile();
		SecretKey secretKey = aesCipherForKey.decryptedKey(encryptedKey);

		DES des = new DES();
		des.setSecretkey(secretKey);

		String hashedPassword = des.decrypt(encryptedPassword);
		String hashedPasswordInput = this.hashPassword(passwordInput);
		if (hashedPassword.equals(hashedPasswordInput)) {
			return true;
		}
		return false;
	}

	private void processRegistrationInfo(String usernameInput, String passwordInput)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// hash password
		String hashedPassword = this.hashPassword(passwordInput);

		// encrypt hashed password
		DES des = new DES();
		byte[] encryptedHashedPassword = des.encryptString(hashedPassword);
		System.out.println(
				"The DES encrypted message 64: " + Base64.getEncoder().encodeToString(encryptedHashedPassword));

		SecretKey secretkey = des.getSecretkey();
		this.getKeyFromFile();
		byte[] encryptedKey = aesCipherForKey.encryptedKey(secretkey);

		this.sumbitRegistrationToDB(usernameInput, encryptedHashedPassword, encryptedKey);
	}

	private void sumbitRegistrationToDB(String username, byte[] password, byte[] key) {
		try {
			// Register JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Open a connection
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			// Execute a query
			byte[] passphrase = this.getPassphrase();
			String sql = "INSERT INTO users_info (user_name, user_password, user_key) VALUES (AES_ENCRYPT(?, ?), ?, ?)";
			System.out.println("Creating prepared statement...");
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setBytes(2, passphrase);
			preparedStatement.setBytes(3, password);
			preparedStatement.setBytes(4, key);
			preparedStatement.executeUpdate();

			Label registeredMessage = new Label("Your account has been created successfully!");
			registeredMessage.setStyle("-fx-font-size: 16px; " + "-fx-font-family: 'Arial'; " + "-fx-text-fill: green; "
					+ "-fx-font-weight: bold;");
			homepageBox.getChildren().add(registeredMessage);
			root.setCenter(homepageBox);

//			String sql = "INSERT INTO `users_info` (`user_name`, `user_password`, `user_key`)\n"
//					+ "VALUES ( AES_ENCRYPT(\"" + passphrase + "\",\"" + username + "\" ), \"" + password + "\", \""
//					+ key + "\"));";
			preparedStatement.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private byte[] getPassphrase() {
		CaesarCipher cc = new CaesarCipher();
		String filePath = "pp.bin";
		try {
			if (!Files.exists(Path.of(filePath))) {
				Files.createFile(Path.of(filePath));
				SecureRandom secureRandom = new SecureRandom();
				byte[] randomPassBytes = new byte[16];
				secureRandom.nextBytes(randomPassBytes);
				byte[] encryptedPassBytes = cc.binaryCipher(randomPassBytes);
				Files.write(Path.of(filePath), encryptedPassBytes, StandardOpenOption.WRITE);
				System.out.println("Data has been written to file: " + filePath);
				return randomPassBytes;
			} else {
				byte[] encryptedPassBytes = Files.readAllBytes(Path.of(filePath));
				byte[] decryptedPassBytes = cc.deBinaryCipher(encryptedPassBytes);
				return decryptedPassBytes;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String hashPassword(String plainPassword) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(plainPassword.getBytes());

			StringBuilder stringBuilder = new StringBuilder();
			for (byte b : hashedBytes) {
				stringBuilder.append(String.format("%02x", b));
			}

			return stringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

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
		} else if (!this.dbUsernameTaken(newUsername)) {
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
			byte[] passphrase = this.getPassphrase();

			// for test
			System.out.println("Checking username..." + newUsername);
			String sql = "SELECT * FROM `users_info` WHERE AES_DECRYPT(`user_name`, \"" + passphrase + "\") = \""
					+ newUsername + "\";";

			ResultSet resultSet = statement.executeQuery(sql);

			if (resultSet.next()) {
				return false;
			}
			// Close external resources
			resultSet.close();
			statement.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

//	private boolean checkPassword(String plainPassword, String hashedPassword) {
//		return hashedPassword.equals(hashPassword(plainPassword));
//	}

	private void encryptRSA() {
		try {
			RSA rsa = new RSA(1024);
			rsa.createKeys();
			rsa.writeKeyToFile("KeyPair/publicKey", rsa.getPublicKey().getEncoded());
			rsa.writeKeyToFile("KeyPair/privateKey", rsa.getPrivateKey().getEncoded());

			PrivateKey privateKey = rsa.getPrivate("KeyPair/privateKey");
			PublicKey publicKey = rsa.getPublic("KeyPair/publicKey");

			if (new File("KeyPair/text.txt").exists()) {
				rsa.encryptFile(rsa.getFileInBytes(new File("KeyPair/text.txt")),
						new File("KeyPair/text_encrypted.txt"), privateKey);
				rsa.decryptFile(rsa.getFileInBytes(new File("KeyPair/text_encrypted.txt")),
						new File("KeyPair/text_decrypted.txt"), publicKey);
			} else {
				System.out.println("Create a file text.txt under folder KeyPair");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String encryptedDESText(String plainStr) {
		try {
			DES des = new DES();
			byte[] encText = des.encryptString(plainStr);
			System.out.println("The DES encrypted message 64: " + Base64.getEncoder().encodeToString(encText));
			return Base64.getEncoder().encodeToString(encText);

		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
			return null;
		}

	}

	private void decryptDES(byte[] encryptedText, String encodedKey)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		DES des = new DES();
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
		des.setSecretkey(des.stringToSecretKey(encodedKey));

		String decText = des.decrypt(encryptedText);
		System.out.println("The DES decrypted message: " + decText);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
