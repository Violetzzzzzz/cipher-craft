package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class User {
	private int id;
	private String name;
	private Main main;
	private String preferCipher = "AES";
	private String preferColor = "#003366";
	private List<String> textTitleList = new ArrayList<>();

	protected User(int id, String name, Main main) {
		this.id = id;
		this.name = name;
		this.main = main;
	}

	protected int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected List<String> getTextTitleList() {
		return textTitleList;
	}

	protected void setTextTitleList(List<String> textTitleList) {
		this.textTitleList = textTitleList;
	}

	protected String getPreferCipher() {
		return preferCipher;
	}

	protected void setPreferCipher(String preferCipher) {
		this.preferCipher = preferCipher;
		try {
			// Register JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Open a connection
			System.out.println("Connecting to database...");
			Connection connection = DriverManager.getConnection(Main.JDBC_URL, Main.USERNAME, Main.PASSWORD);
			// Execute a query
			byte[] passphrase = main.getPassphrase();
			String sql = "INSERT INTO user_setting (user_id, button_color, prefer_cipher) VALUES (?, AES_ENCRYPT(?, ?), AES_ENCRYPT(?, ?)) ON DUPLICATE KEY UPDATE button_color = AES_ENCRYPT(?, ?), prefer_cipher = AES_ENCRYPT(?, ?);";
			System.out.println("Creating prepared statement...");
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, this.preferColor);
			preparedStatement.setBytes(3, passphrase);
			preparedStatement.setString(4, this.preferCipher);
			preparedStatement.setBytes(5, passphrase);
			preparedStatement.setString(6, this.preferColor);
			preparedStatement.setBytes(7, passphrase);
			preparedStatement.setString(8, this.preferCipher);
			preparedStatement.setBytes(9, passphrase);
			preparedStatement.executeUpdate();

			preparedStatement.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String getPreferColor() {
		return preferColor;
	}

	protected void setPreferColor(String preferColor) {
		this.preferColor = preferColor;
		try {
			// Register JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Open a connection
			System.out.println("Connecting to database...");
			Connection connection = DriverManager.getConnection(Main.JDBC_URL, Main.USERNAME, Main.PASSWORD);
			// Execute a query
			byte[] passphrase = main.getPassphrase();
			String sql = "INSERT INTO user_setting (user_id, button_color, prefer_cipher) VALUES (?, AES_ENCRYPT(?, ?), AES_ENCRYPT(?, ?)) ON DUPLICATE KEY UPDATE button_color = AES_ENCRYPT(?, ?), prefer_cipher = AES_ENCRYPT(?, ?);";
			System.out.println("Creating prepared statement...");
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, this.preferColor);
			preparedStatement.setBytes(3, passphrase);
			preparedStatement.setString(4, this.preferCipher);
			preparedStatement.setBytes(5, passphrase);
			preparedStatement.setString(6, this.preferColor);
			preparedStatement.setBytes(7, passphrase);
			preparedStatement.setString(8, this.preferCipher);
			preparedStatement.setBytes(9, passphrase);
			preparedStatement.executeUpdate();

			preparedStatement.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void loadSetting() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		// Open a connection
		System.out.println("Connecting to database...");
		Connection connection = DriverManager.getConnection(Main.JDBC_URL, Main.USERNAME, Main.PASSWORD);
		// Execute a query

		byte[] passphrase = main.getPassphrase();

		String sql = "SELECT AES_DECRYPT(button_color, ?) AS decrypted_color, AES_DECRYPT(prefer_cipher, ?) AS decrypted_cipher FROM user_setting WHERE user_id = ?;";
		System.out.println("Creating prepared statement...");
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setBytes(1, passphrase);
		preparedStatement.setBytes(2, passphrase);
		preparedStatement.setInt(3, id);
		ResultSet resultSet = preparedStatement.executeQuery();

		System.out.println("get settings...");
		while (resultSet.next()) {
			this.preferColor = resultSet.getString("decrypted_color");
			main.btnTextColor = this.preferColor;
			main.refreshButtons();
			this.preferCipher = resultSet.getString("decrypted_cipher");
		}

		// Close external resources
		resultSet.close();
		preparedStatement.close();
		connection.close();
	}

}
