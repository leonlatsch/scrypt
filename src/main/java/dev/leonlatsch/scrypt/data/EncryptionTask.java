package dev.leonlatsch.scrypt.data;

import javafx.concurrent.Task;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;

/**
 * @author Leon Latsch
 * @since 2.3
 */
public class EncryptionTask extends BaseTask {

    public EncryptionTask(File in, File out, SecretKeySpec key, EncryptionCallback callback) {
        super(in, out, key, callback);
    }

    @Override
    protected StreamContext getStreamContext() {
        return getEncryptionManager().encrypt(getIn(), getOut(), getKey());
    }

}
