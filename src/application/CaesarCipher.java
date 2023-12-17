package application;

class CaesarCipher {
	int key;

	protected int getKey() {
		return key;
	}

	protected void setKey(int key) {
		this.key = key;
	}

	protected CaesarCipher() {
		key = 3;
	}

	protected CaesarCipher(int key) {
		this.key = key;
	}

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
