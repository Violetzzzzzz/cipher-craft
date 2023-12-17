package application;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	private SecretKey secretkey;

	protected AES() throws NoSuchAlgorithmException {
		generateKey();
	}

	protected void generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256);
		this.setSecretkey(keyGen.generateKey());
	}

	protected byte[] encryptStringToBytes(String strDataToEncrypt)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
		byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
		byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
		return byteCipherText;
	}

	protected String decryptBytesToString(byte[] strCipherText) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
		byte[] byteDecryptedText = aesCipher.doFinal(strCipherText);

		return new String(byteDecryptedText);
	}

	protected byte[] encryptedKey(SecretKey secretK) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
		byte[] byteDataToEncrypt = secretK.getEncoded();
		byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
		return byteCipherText;
	}

	protected SecretKey decryptedKey(byte[] encryptedKey) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
		byte[] decryptedKeyText = aesCipher.doFinal(encryptedKey);
		SecretKey secretKey = new SecretKeySpec(decryptedKeyText, 0, decryptedKeyText.length, "DES");
		return secretKey;
	}

	protected SecretKey decryptedAESKey(byte[] encryptedKey) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
		byte[] decryptedKeyText = aesCipher.doFinal(encryptedKey);
		SecretKey secretKey = new SecretKeySpec(decryptedKeyText, 0, decryptedKeyText.length, "AES");
		return secretKey;
	}

	/**
	 * @return the secretkey
	 */
	protected SecretKey getSecretkey() {
		return secretkey;
	}

	/**
	 * @param secretkey the secretkey to set
	 */
	protected void setSecretkey(SecretKey secretkey) {
		this.secretkey = secretkey;
	}

	protected String secretKeyToString() {
		byte[] encodedKey = this.secretkey.getEncoded();
		String encodedKeyString = Base64.getEncoder().encodeToString(encodedKey);
		return encodedKeyString;
	}

	protected SecretKey stringToSecretKey(String encodedKeyString) {
		byte[] decodedKey = Base64.getDecoder().decode(encodedKeyString);
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return secretKey;
	}
}
