import pinypon.action.encryption.authentication.CreateNouce;
import org.junit.Test;
import pinypon.utils.Defaults;

import static org.junit.Assert.*;

/*
 * This Java source file was auto generated by running 'gradle init --type java-library'
 * by 'hugdru' at '05/05/16 17:46' with Gradle 2.12
 *
 * @author hugdru, @date 05/05/16 17:46
 */
public class ActionCrypto {
    @Test public void testSomeLibraryMethod() {
        CreateNouce classUnderTest = new CreateNouce();
        assertEquals(Defaults.NOUNCE_SIZE, classUnderTest.createNouce().length);
    }
}