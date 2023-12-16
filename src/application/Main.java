package application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
	static final String JDBC_URL = "jdbc:mysql://database-violetsassignment.cwiyapmmjmbk.ap-southeast-2.rds.amazonaws.com:3306/db_assignment";
	static final String USERNAME = "admin";
	static final String PASSWORD = "qazxswedc";
//	Connection connection;
	User user;
	Stage primaryStage;
	Stage alertStage;
	Scene scene;
	Scene userScene;
	Scene alertScene;
	BorderPane root = new BorderPane();
	BorderPane userroot = new BorderPane();
	BorderPane alertroot = new BorderPane();

	VBox homepageBox;
	GridPane registerGrid;
	GridPane loginGrid;
	GridPane userGrid;
	ListView<UserText> textTitleListView = new ListView<>();
	Label userPageSubtitleTitle;
	HBox pageFocusBox;
	Label userTitle;

	CaesarCipher cc;
	UserText userTextInDisplay;
	String currentCipher;
	String textNameForSaving;
	String cipherTextForSaving;

	String btnTextColor = "#003366";
	String bgColor = "#CCCCCC";

	AES aesCipherForKey;

//	List<Button> buttonList = new ArrayList<>();
//	List<Label> pageTitles = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			scene = new Scene(root, 600, 450);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("CipherCraft");
			primaryStage.setScene(scene);
			primaryStage.show();

			// set pages layout
			this.setHomePage();
			this.setAlertStage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setAlertStage() {
		alertStage = new Stage();
		alertStage.setAlwaysOnTop(true);
		alertStage.setTitle("Alert");
		alertScene = new Scene(alertroot, 200, 150);
		alertStage.setScene(alertScene);
		alertStage.initModality(Modality.APPLICATION_MODAL);
	}

	private void handleMouseClick(MouseEvent event) {
		if (event.getTarget() instanceof GridPane) {
			root.requestFocus();
			userroot.requestFocus();
		}
	}

	// set buttons styles
	private void setButtonStyle(Button btn) {
		btn.setStyle("-fx-border-width: 1px; " + "-fx-border-color: " + btnTextColor + "; " + "-fx-border-radius: 4px;"
				+ "-fx-padding: 4px 8px;" + "-fx-font-size: 16px;" + "-fx-text-fill: " + btnTextColor + ";"
				+ "-fx-font-family: 'Arial';" + "-fx-opacity: 0.7; ");
		btn.setWrapText(true);

	}

	private void setTitleLabelStyle(Label lb) {
		lb.setStyle("-fx-font-size: 24px; " + "-fx-font-family: 'Arial'; " + "-fx-text-fill: blue; "
				+ "-fx-font-weight: bold;");
		lb.setWrapText(true);
	}

	private void setBodyLabelStyle(Label lb) {
		lb.setStyle("-fx-font-size: 16px; " + "-fx-font-family: 'Arial'; " + "-fx-text-fill: black; ");
		lb.setWrapText(true);
	}

	private void setLayoutBorderStyle(Node layout) {
//		layout.setStyle("-fx-border-color: black; -fx-border-width: 0 0 2 0;");
	}

	private void setHomePage() {
		homepageBox = new VBox();
		homepageBox.setAlignment(Pos.CENTER);
		homepageBox.setSpacing(20);

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
		this.setBodyLabelStyle(usernameLable);
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
		this.setBodyLabelStyle(passwordLable);
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

		// for easy test
		usernameTextField.setText("violetz");
		passwordField.setText("Abc123@@");

		Label loginTip = new Label();
		loginTip.setWrapText(true);
		loginTip.setMaxWidth(220);

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
							userroot.setCenter(userGrid);
							if (userScene == null) {
								userScene = new Scene(userroot, 1200, 800);
							}
							this.primaryStage.setScene(userScene);
							textTitleListView.getItems().clear();
							this.loadUserSavedTextTitleList();
						} else {
							loginTip.setText("Username and password don't match. \nPlease try again.");
							loginTip.setTextFill(Color.RED);
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
		GridPane.setColumnSpan(loginTip, 2);
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
		loginGrid.add(loginTip, 0, 6);
		loginGrid.add(loginButton, 0, 7);
		loginGrid.add(backButton, 1, 7);

		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setHalignment(HPos.CENTER);
		loginGrid.getColumnConstraints().add(columnConstraints);
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

	private void setUserPage() {
		userGrid = new GridPane();
		userGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		userGrid.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
		userGrid.setAlignment(Pos.CENTER);
		userGrid.setOnMouseClicked(this::handleMouseClick);

		userTitle = new Label("Login successfully. Welcome " + user.getName() + " :)");
		this.setTitleLabelStyle(userTitle);

		Button settingButton = new Button("Settings");
		this.setButtonStyle(settingButton);

		Button logoutButton = new Button("Log out");
		this.setButtonStyle(logoutButton);
		logoutButton.setOnAction(action -> {
			root.setCenter(homepageBox);
			homepageBox.requestFocus();
			user = null;
			this.primaryStage.setScene(scene);
		});

		Button newTextButton = new Button("New Text");
		this.setButtonStyle(newTextButton);

		Button fileTextButton = new Button("Load Text From File");
		this.setButtonStyle(fileTextButton);

		Label listviewTitle = new Label("Your Texts: ");
		this.setBodyLabelStyle(listviewTitle);
		textTitleListView.setMaxWidth(140);
		textTitleListView.setPrefHeight(470);

		this.userPageSubtitleTitle = new Label("Start with New Text or Choose One From Sidebar");
		this.userPageSubtitleTitle.setStyle("-fx-font-size: 20px; " + "-fx-font-family: 'Arial'; "
				+ "-fx-text-fill: blue; " + "-fx-font-weight: bold;");

		TextField textTitleField = new TextField();
		textTitleField.setMaxWidth(200);

		Label plainTextTitle = new Label("Plain Text: ");
		plainTextTitle.setWrapText(true);
		plainTextTitle.setMaxWidth(420);
		this.setBodyLabelStyle(plainTextTitle);

		TextArea plainTextArea = new TextArea();
		plainTextArea.setWrapText(true);
		plainTextArea.setMaxWidth(420);
		plainTextArea.setPrefHeight(400);

		Label cipherTextTitle = new Label("Cipher Text: ");
		cipherTextTitle.setWrapText(true);
		cipherTextTitle.setMaxWidth(420);
		this.setBodyLabelStyle(cipherTextTitle);

		TextArea cipherTextArea = new TextArea();
		cipherTextArea.setWrapText(true);
		cipherTextArea.setMaxWidth(420);
		cipherTextArea.setEditable(false);
		cipherTextArea.setPrefHeight(400);

		Label caesarKeyLabel = new Label("CaeserCipher Key: ");
		caesarKeyLabel.setWrapText(true);
		caesarKeyLabel.setMaxWidth(140);
		caesarKeyLabel.setVisible(false);
		this.setBodyLabelStyle(caesarKeyLabel);

		TextField caesarKeyField = new TextField();
		caesarKeyField.setVisible(false);
		caesarKeyField.setMaxWidth(140);
		caesarKeyField.setPromptText("Enter a number");
		caesarKeyField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getControlNewText().matches("\\d{0,4}")) {
				return change;
			} else {
				return null;
			}
		}));

		Label cipherTypeLabel = new Label("Choose Cipher Type:");
		cipherTypeLabel.setWrapText(true);
		cipherTypeLabel.setMaxWidth(140);
		this.setBodyLabelStyle(cipherTypeLabel);

		ComboBox<String> cipherDropDown = new ComboBox<>();
		cipherDropDown.setItems(FXCollections.observableArrayList("CaesarCipher", "DES", "AES", "RSA"));
		cipherDropDown.setPrefWidth(140);
		cipherDropDown.setValue(user.getPreferCipher());
		cipherDropDown.setOnAction(event -> {
			String selectedOption = cipherDropDown.getValue();
			System.out.println("Selected option: " + selectedOption);
			if (selectedOption.equals("CaesarCipher")) {
				caesarKeyLabel.setVisible(true);
				caesarKeyField.setVisible(true);
				caesarKeyField.setEditable(true);
			} else {
				caesarKeyLabel.setVisible(false);
				caesarKeyField.setVisible(false);
			}
		});

		Button encryptButton = new Button("Encrypt ->");
		this.setButtonStyle(encryptButton);
		encryptButton.setPrefWidth(140);
		Button decryptButton = new Button("<- Decrypt");
		this.setButtonStyle(decryptButton);
		decryptButton.setPrefWidth(140);

		Label errorMessage = new Label();
		errorMessage.setMaxWidth(140);
		errorMessage.setWrapText(true);
		errorMessage.setTextFill(Color.RED);

		Button saveButton = new Button("Save Cipher Text to Database");
		this.setButtonStyle(saveButton);
		Button exportCipherTextButton = new Button("Export Cipher Text to File");
		this.setButtonStyle(exportCipherTextButton);
		exportCipherTextButton.setMinWidth(200);
		Button exportPlainTextButton = new Button("Export Plain Text to File");
		this.setButtonStyle(exportPlainTextButton);

		HBox headerButtonsBox = new HBox(12);
		headerButtonsBox.setAlignment(Pos.CENTER);
		headerButtonsBox.getChildren().addAll(newTextButton, fileTextButton, saveButton, exportPlainTextButton,
				exportCipherTextButton, settingButton, logoutButton);
		this.setLayoutBorderStyle(headerButtonsBox);
		headerButtonsBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 2 0;");

		VBox listviewBox = new VBox(10);
		listviewBox.setAlignment(Pos.CENTER);
		listviewBox.getChildren().addAll(listviewTitle, textTitleListView);
		this.setLayoutBorderStyle(listviewBox);

		HBox subtitleBox = new HBox(12);
		subtitleBox.setAlignment(Pos.CENTER);
		subtitleBox.getChildren().addAll(userPageSubtitleTitle);
		this.setLayoutBorderStyle(subtitleBox);

		VBox plainTextBox = new VBox(10);
		plainTextBox.setAlignment(Pos.CENTER);
		plainTextBox.getChildren().addAll(plainTextTitle, plainTextArea);
		this.setLayoutBorderStyle(plainTextBox);

		VBox cipherTextBox = new VBox(10);
		cipherTextBox.setAlignment(Pos.CENTER);
		cipherTextBox.getChildren().addAll(cipherTextTitle, cipherTextArea);
		this.setLayoutBorderStyle(cipherTextBox);

		VBox cipherControlsBox = new VBox(10);
		cipherControlsBox.setAlignment(Pos.CENTER);
		cipherControlsBox.getChildren().addAll(cipherTypeLabel, cipherDropDown, caesarKeyLabel, caesarKeyField,
				encryptButton, decryptButton);
		this.setLayoutBorderStyle(cipherControlsBox);

		this.pageFocusBox = new HBox(10);
		this.pageFocusBox.setAlignment(Pos.CENTER);
		this.pageFocusBox.getChildren().addAll(plainTextBox, cipherControlsBox, cipherTextBox);
		this.setLayoutBorderStyle(pageFocusBox);

		this.pageFocusBox.setVisible(false);

		newTextButton.setOnAction(action -> {
			userTitle.setText("Hi " + user.getName() + ". Encrypt and Decrypt New Text");
			pageFocusBox.setVisible(true);
			userPageSubtitleTitle.setText("Text Title: ");
			subtitleBox.getChildren().add(textTitleField);
			userTextInDisplay = null;
		});

		encryptButton.setOnAction(action -> {
			String selectedCipherOption = cipherDropDown.getValue();
			if (plainTextArea.getText().trim().isEmpty()) {
				errorMessage.setText("Plain textarea cannot be empty");
				alertroot.setCenter(errorMessage);
				alertStage.show();
			} else {
				if (selectedCipherOption.equals("CaesarCipher")) {
					String keyString = caesarKeyField.getText().trim();
					if (keyString.isEmpty()) {
						errorMessage.setText("Please enter a numeric key");
						alertroot.setCenter(errorMessage);
						alertStage.show();
					} else {
						String plainText = plainTextArea.getText().trim();
						int key = Integer.parseInt(keyString);
						cc = new CaesarCipher(key);
						String cipherText = cc.encryptString(plainText, key);
						cipherTextArea.setText(cipherText);
						currentCipher = "CaesarCipher";
					}
				}
			}
		});

		decryptButton.setOnAction(action -> {
			String cipherText = cipherTextArea.getText().trim();
			if (!cipherText.isEmpty()) {
				if (currentCipher.equals("CaesarCipher")) {
					String plainText = cc.decryptString(cipherText, cc.getKey());
					plainTextArea.setText(plainText);
				} else if (currentCipher.equals("AES")) {

				} else if (currentCipher.equals("DES")) {

				} else if (currentCipher.equals("RSA")) {

				} else {
					errorMessage.setText("Can't recognize cipher type.");
					alertroot.setCenter(errorMessage);
					alertStage.show();
					System.out.println("Lost current cipher type..." + currentCipher);
				}
			}
		});

		saveButton.setOnAction(action -> {
			String cipherText = cipherTextArea.getText().trim();
			if (!cipherText.isEmpty()) {
				String textTitle = textTitleField.getText().trim();
				if (!textTitle.isEmpty()) {
					if (currentCipher.equals("CaesarCipher")) {
						try {
							this.saveCaesarCipherTextToDB(cipherText, textTitle);
							subtitleBox.getChildren().remove(textTitleField);
							cipherTextArea.clear();
							plainTextArea.clear();
							caesarKeyField.clear();
							cipherDropDown.setValue(user.getPreferCipher());
							textTitleField.clear();
							userTitle.setText("Hi " + user.getName());
						} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
								| InvalidAlgorithmParameterException | IllegalBlockSizeException
								| BadPaddingException e) {
							// TODO Auto-generated catch block
							subtitleBox.getChildren().add(textTitleField);
							e.printStackTrace();
						}
					} else if (currentCipher.equals("AES")) {

					} else if (currentCipher.equals("DES")) {

					} else if (currentCipher.equals("RSA")) {

					} else {
						errorMessage.setText("Can't recognize cipher type.");
						alertroot.setCenter(errorMessage);
						alertStage.show();
						System.out.println("Lost current cipher type..." + currentCipher);
					}
				} else {
					errorMessage.setText("Text title cannot be empty");
					alertroot.setCenter(errorMessage);
					alertStage.show();
				}
			} else {
				errorMessage.setText("Plain textarea cannot be empty");
				alertroot.setCenter(errorMessage);
				alertStage.show();
			}
		});

		ContextMenu textTitleMenu = new ContextMenu();
		MenuItem textTitleOpenMenuItem = new MenuItem("Open");
		textTitleOpenMenuItem.setOnAction(event -> {
			UserText selectedText = textTitleListView.getSelectionModel().getSelectedItem();
			if (selectedText != null) {
				try {
					if (selectedText.readyToDisplay()) {
						userTextInDisplay = selectedText;
						pageFocusBox.setVisible(true);
						cipherTextArea.setText(selectedText.getCipherText());
						textTitleField.setText(selectedText.getTitle());
						if (!subtitleBox.getChildren().contains(textTitleField)) {
							subtitleBox.getChildren().add(textTitleField);
						}
						userPageSubtitleTitle.setText("Text Title: ");
						this.currentCipher = selectedText.getCipherType();
						cipherDropDown.setValue(selectedText.getCipherType());
						userTitle.setText("Hi " + user.getName() + ". Encrypt and Decrypt Your Existing Text. ");
						if (selectedText.getCipherType().equals("CaesarCipher")) {
							cc = new CaesarCipher(selectedText.getCaeserKey());
							caesarKeyField.setText(Integer.toString(selectedText.getCaeserKey()));
						}
					}
				} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException
						| NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
						| BadPaddingException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pageFocusBox.setVisible(true);

			}
		});

		textTitleMenu.getItems().addAll(textTitleOpenMenuItem);
		textTitleListView.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				textTitleMenu.show(textTitleListView, event.getScreenX(), event.getScreenY());
			}
		});
//		textTitleListView.

		GridPane.setColumnSpan(userTitle, 8);
		GridPane.setColumnSpan(headerButtonsBox, 8);
		GridPane.setRowSpan(listviewBox, 8);
		GridPane.setColumnSpan(subtitleBox, 7);
		GridPane.setRowSpan(pageFocusBox, 7);
		GridPane.setColumnSpan(pageFocusBox, 7);

		userGrid.setVgap(4);
		userGrid.setHgap(10);
		userGrid.setPadding(new Insets(5, 5, 5, 5));
		userGrid.add(userTitle, 0, 0);
		userGrid.add(headerButtonsBox, 0, 1);
		userGrid.add(listviewBox, 0, 2);
		userGrid.add(subtitleBox, 1, 2);
		userGrid.add(pageFocusBox, 1, 3);

		double columnPercentageWidth = 95 / 8.0;
		for (int i = 0; i < 8; i++) {
			ColumnConstraints column = new ColumnConstraints();
			column.setPercentWidth(columnPercentageWidth);
			userGrid.getColumnConstraints().add(column);
		}

		double rowPercentageWidth = 80.0 / 8.0;
		for (int i = 0; i < 12; i++) {
			RowConstraints row = new RowConstraints();
			row.setPercentHeight(rowPercentageWidth);
			userGrid.getRowConstraints().add(row);
		}

	}

	private void saveCaesarCipherTextToDB(String cipherText, String textTitle)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		int key = cc.getKey();
		String keyString = Integer.toString(key);
		String cipherType = "CaesarCipher";
		byte[] encryptedKey = this.aesCipherForKey.encrypt(keyString);
		byte[] encryptedCipherType = this.aesCipherForKey.encrypt(cipherType);
		this.sumbitAllTextInfoToDB(textTitle, cipherText, encryptedKey, encryptedCipherType);
	}

	private void sumbitAllTextInfoToDB(String textTitle, String cipherText, byte[] encryptedKey,
			byte[] encryptedCipherType) {
		try {
			// Register JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Open a connection
			System.out.println("Connecting to database...");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			// Execute a query
			byte[] passphrase = this.getPassphrase();

			String sql = "INSERT INTO messages_info (user_id, message_title, message_content, message_key, message_type) VALUES (?, AES_ENCRYPT(?, ?), ?, ?, ?)";
			if (userTextInDisplay != null) {
				sql = "UPDATE messages_info SET user_id = ?, message_title = AES_ENCRYPT(?, ?), message_content = ?, message_key = ?, message_type = ? WHERE message_id = ?";
			}
			System.out.println("Creating prepared statement...");
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, user.getId());
			preparedStatement.setString(2, textTitle);
			preparedStatement.setBytes(3, passphrase);
			preparedStatement.setString(4, cipherText);
			preparedStatement.setBytes(5, encryptedKey);
			preparedStatement.setBytes(6, encryptedCipherType);
			if (userTextInDisplay != null) {
				preparedStatement.setInt(7, userTextInDisplay.getTextID());
			}
			preparedStatement.executeUpdate();
			preparedStatement.close();
			connection.close();

			this.userPageSubtitleTitle.setText("Your cipher text has been saved to database");
			this.pageFocusBox.setVisible(false);
			this.userTextInDisplay = null;
			textTitleListView.getItems().clear();
			this.loadUserSavedTextTitleList();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void loadUserSavedTextTitleList() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		// Open a connection
		System.out.println("Connecting to database...");
		Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
		// Execute a query

		byte[] passphrase = this.getPassphrase();

		String sql = "SELECT message_id, user_id, AES_DECRYPT(message_title, ?) AS decrypted_title, message_content, message_key, message_type FROM messages_info WHERE user_id = ?;";
		System.out.println("Creating prepared statement...");
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setBytes(1, passphrase);
		preparedStatement.setInt(2, user.getId());
		ResultSet resultSet = preparedStatement.executeQuery();

		System.out.println("get texts titles...");
		while (resultSet.next()) {
			int textID = resultSet.getInt("message_id");
			String textTitle = resultSet.getString("decrypted_title");
			UserText userText = new UserText(textID, user.getId(), textTitle, this);
			textTitleListView.getItems().add(userText);
		}

//		System.out.println(textTitleListView.getItems().isEmpty());

		// Close external resources
		resultSet.close();
		preparedStatement.close();
		connection.close();

	}

	private boolean authenticateLogin(String usernameInput, String passwordInput)
			throws ClassNotFoundException, SQLException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// Register JDBC driver
		Class.forName("com.mysql.cj.jdbc.Driver");
		// Open a connection
		System.out.println("Connecting to database...");
		Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
		// Execute a query

		byte[] passphrase = this.getPassphrase();

		// for test
		System.out.println("Checking username..." + usernameInput);

		String sql = "SELECT user_id, AES_DECRYPT(user_name, ?) AS decrypted_username, user_password, user_key FROM users_info WHERE user_name = AES_ENCRYPT(?, ?);";
		System.out.println("Creating prepared statement...");
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setBytes(1, passphrase);
		preparedStatement.setString(2, usernameInput);
		preparedStatement.setBytes(3, passphrase);
		ResultSet resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			System.out.println("get user data...");
			int id = resultSet.getInt("user_id");
			String username = resultSet.getString("decrypted_username");
			byte[] encryptedPassword = resultSet.getBytes("user_password");
			byte[] encryptedKey = resultSet.getBytes("user_key");

			if (this.checkPassword(passwordInput, encryptedPassword, encryptedKey)) {
				user = new User(id, username, this);
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
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
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

			preparedStatement.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	byte[] getPassphrase() {
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
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
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

	private void getKeyFromFile() throws NoSuchAlgorithmException {
		aesCipherForKey = new AES();
		CaesarCipher cCipher = new CaesarCipher();
		String filePath = "kk.bin";
		try {
			if (!Files.exists(Path.of(filePath))) {
				Files.createFile(Path.of(filePath));
				SecretKey secretkeyskey = aesCipherForKey.getSecretkey();
				byte[] encryptedKeysKey = cCipher.binaryCipher(secretkeyskey.getEncoded());
				Files.write(Path.of(filePath), encryptedKeysKey, StandardOpenOption.WRITE);
				System.out.println("Data has been written to file: " + filePath);
			} else {
				byte[] encryptedKeysKey = Files.readAllBytes(Path.of(filePath));
				byte[] decryptedKeysKey = cCipher.deBinaryCipher(encryptedKeysKey);
				SecretKey secretkeyskey = new SecretKeySpec(decryptedKeysKey, 0, decryptedKeysKey.length, "AES");
				aesCipherForKey.setSecretkey(secretkeyskey);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	private void encryptRSA() {
//		try {
//			RSA rsa = new RSA(1024);
//			rsa.createKeys();
//			rsa.writeKeyToFile("KeyPair/publicKey", rsa.getPublicKey().getEncoded());
//			rsa.writeKeyToFile("KeyPair/privateKey", rsa.getPrivateKey().getEncoded());
//
//			PrivateKey privateKey = rsa.getPrivate("KeyPair/privateKey");
//			PublicKey publicKey = rsa.getPublic("KeyPair/publicKey");
//
//			if (new File("KeyPair/text.txt").exists()) {
//				rsa.encryptFile(rsa.getFileInBytes(new File("KeyPair/text.txt")),
//						new File("KeyPair/text_encrypted.txt"), privateKey);
//				rsa.decryptFile(rsa.getFileInBytes(new File("KeyPair/text_encrypted.txt")),
//						new File("KeyPair/text_decrypted.txt"), publicKey);
//			} else {
//				System.out.println("Create a file text.txt under folder KeyPair");
//			}
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

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
