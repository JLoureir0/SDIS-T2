package pinypon.action.encryption;

import org.abstractj.kalium.keys.KeyPair;
import org.abstractj.kalium.keys.PrivateKey;
import org.abstractj.kalium.keys.PublicKey;

public class Nacl {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Nacl() {
        KeyPair kp = new KeyPair();
        privateKey = kp.getPrivateKey();
        publicKey = kp.getPublicKey();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
