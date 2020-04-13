package dev.leonlatsch.scrypt.data;

import javafx.concurrent.Task;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;

/**
 * @author Leon Latsch
 * @since 2.3
 */
public abstract class BaseTask extends Task<Void> {

    private static final int BUFFER_SIZE = 1024;

    private File in, out;
    private SecretKeySpec key;
    private EncryptionManager encryptionManager;
    private EncryptionCallback callback;

    public BaseTask(File in, File out, SecretKeySpec key, EncryptionCallback callback) {
        super();
        this.in = in;
        this.out = out;
        this.key = key;
        encryptionManager = EncryptionManager.getInstance();
        this.callback = callback;
    }

    protected abstract StreamContext getStreamContext();

    @Override
    protected Void call() {
        boolean success;

        // Get StreamObject with in and out
        StreamContext streams = getStreamContext();
        if (streams == null) {
            getCallback().onEncryptionFinished(false);
            return null;
        }

        try {
            // Calc the amount of loop rounds
            long run = 0;
            long runs = (streams.getSize() / BUFFER_SIZE) * 2;
            if (runs < 1) {
                runs = 1L;
            }

            // Copy the streams. Updates the progress property while copying
            int i;
            byte[] b = new byte[BUFFER_SIZE];

            while ((i = streams.getIn().read(b)) != -1) {
                streams.getOut().write(b, 0, i);
                updateProgress(run, runs);
                run++;
            }

            success = true;
        } catch (Exception e) {
            // Catch any error. Mainly wrong key
            success = false;
        } finally {
            streams.close();
        }

        getCallback().onEncryptionFinished(success);
        return null; // Ignore
    }

    public File getIn() {
        return in;
    }

    public File getOut() {
        return out;
    }

    public SecretKeySpec getKey() {
        return key;
    }

    public EncryptionManager getEncryptionManager() {
        return encryptionManager;
    }

    public EncryptionCallback getCallback() {
        return callback;
    }
}
