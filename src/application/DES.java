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

class DES {
	private SecretKey secretkey;

	protected DES() throws NoSuchAlgorithmException {
		generateKey();
	}

	protected void generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		this.setSecretkey(keyGen.generateKey());
	}

	protected byte[] encryptStringToBytes(String strDataToEncrypt)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher desCipher = Cipher.getInstance("DES");

		desCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
		byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
		byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);
		return byteCipherText;
	}

	protected String decryptBytesToString(byte[] strCipherText) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher desCipher = Cipher.getInstance("DES");

		desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
		byte[] byteDecryptedText = desCipher.doFinal(strCipherText);
		return new String(byteDecryptedText);
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
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
		return secretKey;
	}
}
