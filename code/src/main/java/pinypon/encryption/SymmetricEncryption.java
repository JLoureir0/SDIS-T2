package pinypon.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

public class SymmetricEncryption {
    private static final int KEY_SIZE = 128;
    private static final int SALT_SIZE = KEY_SIZE / 16;
    private static final int IV_SIZE = 128 / 8;
    private static final String HASH = "PBKDF2WithHmacSHA512";
    private static final String ALGORITHM = "AES";
    private static final String MODE = "CBC";
    private static final String PADDING = "PKCS5PADDING";
    private static final String CIPHER = ALGORITHM + "/" + MODE + "/" + PADDING;
    private static final int ITERATION_COUNT = 65535;

    public static byte[] encrypt(final String dataToEncrypt, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, InvalidParameterSpecException {
        byte[] salt = new byte[SALT_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(HASH);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);

        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] initializationVector = cipher.getIV();
        byte[] encryptedTextBytes = cipher.doFinal(dataToEncrypt.getBytes());

        byte[] initializationVectorEncryptedTextBytes = new byte[salt.length + initializationVector.length + encryptedTextBytes.length];
        System.arraycopy(salt, 0, initializationVectorEncryptedTextBytes, 0, SALT_SIZE);
        System.arraycopy(initializationVector, 0, initializationVectorEncryptedTextBytes, SALT_SIZE, initializationVector.length);
        System.arraycopy(encryptedTextBytes, 0, initializationVectorEncryptedTextBytes, SALT_SIZE + initializationVector.length, encryptedTextBytes.length);
        return initializationVectorEncryptedTextBytes;
    }

    public static String decrypt(final byte[] dataToDecrypt, String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        byte[] initializationVectorEncryptedTextBytes = dataToDecrypt;
        if (initializationVectorEncryptedTextBytes.length <= SALT_SIZE + IV_SIZE) {
            throw new IllegalStateException("Bad String for decryption");
        }
        byte[] salt = new byte[SALT_SIZE];
        byte[] initializationVector = new byte[IV_SIZE];
        byte[] encryptedTextBytes = new byte[initializationVectorEncryptedTextBytes.length - IV_SIZE - SALT_SIZE];

        System.arraycopy(initializationVectorEncryptedTextBytes, 0, salt, 0, SALT_SIZE);
        System.arraycopy(initializationVectorEncryptedTextBytes, SALT_SIZE, initializationVector, 0, IV_SIZE);
        System.arraycopy(initializationVectorEncryptedTextBytes, SALT_SIZE + IV_SIZE, encryptedTextBytes, 0, encryptedTextBytes.length);

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(HASH);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);

        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(initializationVector));
        byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);

        return new String(decryptedTextBytes);
    }
}
