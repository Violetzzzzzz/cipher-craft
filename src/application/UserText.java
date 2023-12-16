package application;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

class UserText {
	private int textID;
	private int userID;
	private String title;
	private String plainText;
	private String cipherText;
	private String cipherType;
	private SecretKey secretKey;
	private int caeserKey;
	private Main main;

	public UserText(int textID, int userID, String title, Main main) {
		this.textID = textID;
		this.userID = userID;
		this.title = title;
		this.main = main;
	}

	@Override
	public String toString() {
		return title;
	}

	public boolean readyToDisplay()
			throws ClassNotFoundException, SQLException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if (cipherText == null) {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Open a connection
			System.out.println("Connecting to database...");
			Connection connection = DriverManager.getConnection(Main.JDBC_URL, Main.USERNAME, Main.PASSWORD);
			// Execute a query

			String sql = "SELECT message_content, message_key, message_type FROM messages_info WHERE message_id = ?;";
			System.out.println("Creating prepared statement...");
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, textID);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				System.out.println("get text data...");
				this.cipherText = resultSet.getString("message_content");
				byte[] encryptedKey = resultSet.getBytes("message_key");
				byte[] encryptedCipherType = resultSet.getBytes("message_type");
				this.cipherType = main.aesCipherForKey.decrypt(encryptedCipherType);
				if (this.cipherType.equals("CaesarCipher")) {
					String ccKey = main.aesCipherForKey.decrypt(encryptedKey);
					this.caeserKey = Integer.valueOf(ccKey);
				}
			}
			// Close external resources
			resultSet.close();
			preparedStatement.close();
			connection.close();
			return true;

		}
		return true;
	}

	protected int getTextID() {
		return textID;
	}

	protected void setTextID(int textID) {
		this.textID = textID;
	}

	protected int getUserID() {
		return userID;
	}

	protected void setUserID(int userID) {
		this.userID = userID;
	}

	protected String getTitle() {
		return title;
	}

	protected void setTitle(String title) {
		this.title = title;
	}

	protected String getPlainText() {
		return plainText;
	}

	protected void setPlainText(String plainText) {
		this.plainText = plainText;
	}

	protected String getCipherText() {
		return cipherText;
	}

	protected void setCipherText(String cipherText) {
		this.cipherText = cipherText;
	}

	protected String getCipherType() {
		return cipherType;
	}

	protected void setCipherType(String cipherType) {
		this.cipherType = cipherType;
	}

	protected SecretKey getSecretKey() {
		return secretKey;
	}

	protected void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	protected int getCaeserKey() {
		return caeserKey;
	}

	protected void setCaeserKey(int caeserKey) {
		this.caeserKey = caeserKey;
	}

}
