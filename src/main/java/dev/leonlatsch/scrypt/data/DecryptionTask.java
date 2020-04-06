package dev.leonlatsch.scrypt.data;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;

/**
 * @author Leon Latsch
 * @since 2.3
 */
public class DecryptionTask extends BaseTask {

    public DecryptionTask(File in, File out, SecretKeySpec key, EncryptionCallback callback) {
        super(in, out, key, callback);
    }

    @Override
    protected StreamContext getStreamContext() {
        return getEncryptionManager().decrypt(getIn(), getOut(), getKey());
    }
}
