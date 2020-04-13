package dev.leonlatsch.scrypt.data;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Leon Latsch
 * @since 1.0
 */
public class EncryptionManager {

    private static final String AES = "AES";

    private static EncryptionManager instance;

    public static EncryptionManager getInstance() {
        if (instance == null) {
            instance = new EncryptionManager();
        }

        return instance;
    }

    private EncryptionManager() {
    }

    /**
     * Generate the {@link SecretKeySpec} with the hash of the provided String
     *
     * @param str the source string
     * @return {@link SecretKeySpec}
     */
    public SecretKeySpec keyGen(String str) {
        try {
            byte[] key = str.getBytes();
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            key = md.digest(key);
            return new SecretKeySpec(key, AES);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public StreamContext encrypt(File in, File out, SecretKeySpec key) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            FileInputStream is = new FileInputStream(in);
            CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), cipher);

            return new StreamContext(is, os, in.length());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | FileNotFoundException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public StreamContext decrypt(File in, File out, SecretKeySpec key) {
        try {
            Cipher cipher;
            cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            CipherInputStream is = new CipherInputStream(new FileInputStream(in), cipher);
            FileOutputStream os = new FileOutputStream(out);

            return new StreamContext(is, os, in.length());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | FileNotFoundException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
