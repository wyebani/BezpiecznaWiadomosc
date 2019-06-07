package com.wyebani.bezpiecznawiadomosc;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.crypto.AES;
import com.wyebani.bezpiecznawiadomosc.crypto.DiffieHellman;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import java.security.KeyPair;
import java.util.concurrent.Delayed;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public Timeout timeout = Timeout.millis(2000000);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.wyebani.bezpiecznawiadomosc", appContext.getPackageName());
    }

    @Test
    public void keyExchange() {
        // Bob generate his key pair
        KeyPair bobKeys = DiffieHellman.generateKeys();
        String bobPrivKey = ToolSet.hexToString(bobKeys.getPrivate().getEncoded());
        String bobPubKey = ToolSet.hexToString(bobKeys.getPublic().getEncoded());

        // Alice generate her key pair
        KeyPair aliceKeys = DiffieHellman.generateKeys();
        String alicePrivKey = ToolSet.hexToString(aliceKeys.getPrivate().getEncoded());
        String alicePubKey = ToolSet.hexToString(aliceKeys.getPublic().getEncoded());

        // Bob sends his public key to Alice
        String aliceReceivedBobsPubKey = bobPubKey;

        // Alice sends her public key to Bob
        String bobReceivedAlicesPubKey = alicePubKey;

        // Bob and Alice generate secret key
        String bobSecret = DiffieHellman.generateCommonSecretKey(bobPrivKey, bobReceivedAlicesPubKey);
        String aliceSecret = DiffieHellman.generateCommonSecretKey(alicePrivKey, aliceReceivedBobsPubKey);

        // Should be the same
        Log.d("[KeyExchangeTest]", "Bob secret: " + bobSecret + "\n");
        Log.d("[KeyExchangeTest]", "Alice secret: " + aliceSecret + "\n");
        assertEquals(bobSecret, aliceSecret);
    }

    @Test
    public void messageExchange() throws InterruptedException {
        KeyPair bobKeys = DiffieHellman.generateKeys();
        String bobPrivKey = ToolSet.hexToString(bobKeys.getPrivate().getEncoded());
        String bobPubKey = ToolSet.hexToString(bobKeys.getPublic().getEncoded());
        KeyPair aliceKeys = DiffieHellman.generateKeys();
        String alicePrivKey = ToolSet.hexToString(aliceKeys.getPrivate().getEncoded());
        String alicePubKey = ToolSet.hexToString(aliceKeys.getPublic().getEncoded());
        String aliceReceivedBobsPubKey = bobPubKey;
        String bobReceivedAlicesPubKey = alicePubKey;
        String bobSecret = DiffieHellman.generateCommonSecretKey(bobPrivKey, bobReceivedAlicesPubKey);
        String aliceSecret = DiffieHellman.generateCommonSecretKey(alicePrivKey, aliceReceivedBobsPubKey);

        AES bobAes = new AES(bobSecret);



        AES aliceAes = new AES(aliceSecret);
        String plainText = "Test message";

        // Bob encrypt message
        String encryptedMessage = bobAes.encrypt(plainText);

        // Alice try to decrypt Bob's message
        String decryptedMessage = aliceAes.decrypt(encryptedMessage);

        // Should be the same

        Log.d("[MessageExchange]", "Plain text: " + plainText + "\n");
        Log.d("[MessageExchange]", "Encrypted message: " + encryptedMessage + "\n");
        Log.d("[MessageExchange]", "Decrypted message: " + decryptedMessage + "\n");
        assertEquals(plainText, decryptedMessage);

    }
}
