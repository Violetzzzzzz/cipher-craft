package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class CaesarCipher {
	int key;

	protected int getKey() {
		return key;
	}

	protected void setKey(int key) {
		this.key = key;
	}

	public CaesarCipher() {
		key = 3;
	}

	public CaesarCipher(int key) {
		this.key = key;
	}

//	public static void main(String[] args) {
//		Scanner scanner = new Scanner(System.in);
//
//		System.out.println("Enter the key (shift value) for the Caesar Cipher:");
//		int key = scanner.nextInt();
//		scanner.nextLine();
//
//		System.out.println("Enter the path of the file to encrypt:");
//		String filePath = scanner.nextLine();
//
//		// Encrypt the file
//		encryptFile(filePath, key);
//
//		// Decrypt the file
//		decryptFile(filePath + ".encrypted", key);
//
//		scanner.close();
//	}

	protected byte[] binaryCipher(byte[] bytes) {
		byte[] encryptedBytes = new byte[bytes.length];

		for (int i = 0; i < bytes.length; i++) {
			encryptedBytes[i] = (byte) (bytes[i] + key);
		}

		return encryptedBytes;
	}

	protected byte[] deBinaryCipher(byte[] encryptedBytes) {
		byte[] decryptedBytes = new byte[encryptedBytes.length];

		for (int i = 0; i < encryptedBytes.length; i++) {
			decryptedBytes[i] = (byte) (encryptedBytes[i] - key);
		}
		return decryptedBytes;
	}

	protected String encryptString(String plainText, int key) {
		String cipherText = "";
		for (int i = 0; i < plainText.length(); i++) {
			char encryptedChar = encrypt(plainText.charAt(i), key);
			cipherText = cipherText + encryptedChar;
		}
		System.out.println("String encrypted successfully.");
		System.out.println("Plain text: " + plainText);
		System.out.println("Cipher text: " + cipherText);
		return cipherText;
	}

	protected String decryptString(String cipherText, int key) {
		String plainText = "";
		for (int i = 0; i < cipherText.length(); i++) {
			char decryptedChar = decrypt(cipherText.charAt(i), key);
			plainText = plainText + decryptedChar;
		}
		System.out.println("String decrypted successfully.");
		System.out.println("Cipher text: " + cipherText);
		System.out.println("Plain text: " + plainText);
		return plainText;
	}

	private void encryptFile(String filePath, int key) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
				BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + ".encrypted"))) {

			int currentChar;
			while ((currentChar = reader.read()) != -1) {
				char encryptedChar = encrypt((char) currentChar, key);
				writer.write(encryptedChar);
			}

			System.out.println("File encrypted successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void decryptFile(String filePath, int key) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
				BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + ".decrypted"))) {

			int currentChar;
			while ((currentChar = reader.read()) != -1) {
				char decryptedChar = decrypt((char) currentChar, key);
				writer.write(decryptedChar);
			}

			System.out.println("File decrypted successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private char encrypt(char c, int key) {
		if (Character.isLetter(c)) {
			char base;
			if (Character.isLowerCase(c)) {
				base = 'a';
			} else {
				base = 'A';
			}
			return (char) ((((c - base) + key) % 26) + base);
		}
		return c;
	}

	private char decrypt(char c, int key) {
		return encrypt(c, 26 - key); // Decryption is just encryption with the opposite key
	}
}
