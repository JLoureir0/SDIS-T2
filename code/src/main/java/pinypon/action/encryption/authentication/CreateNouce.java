package pinypon.action.encryption.authentication;


import org.abstractj.kalium.crypto.Random;
import pinypon.utils.Defaults;

/**
 * Created by hacker on 20/05/2016.
 */
public class CreateNouce {



    public byte[] createNouce() {

        Random random = new Random();
        byte[] nounce = random.randomBytes(Defaults.NOUNCE_SIZE);

        return nounce;
    }

}
