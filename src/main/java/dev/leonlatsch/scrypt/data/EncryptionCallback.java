package dev.leonlatsch.scrypt.data;

/**
 * @author Leon Latsch
 * @since 2.3
 */
public interface EncryptionCallback {

    void onEncryptionFinished(boolean success);
}
