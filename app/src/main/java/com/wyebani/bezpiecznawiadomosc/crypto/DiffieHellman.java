package com.wyebani.bezpiecznawiadomosc.crypto;


import android.os.AsyncTask;
import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyAgreement;

public class DiffieHellman {

    private final static String TAG = ToolSet.getTag(DiffieHellman.class.toString());
    private final static String ALGORITHM = "DiffieHellman";

    public static KeyPair generateKeys() {
        Log.d(TAG, "Generating key pair");
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(200);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static String generateCommonSecretKey(String myPrivateKey, String receivedPublicKey) {
        Log.d(TAG, "Generate receiver private key");
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(ToolSet.stringToHex(myPrivateKey));
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            PublicKey receiverPublicKey = restorePublicKeyFrom(receivedPublicKey);

            final KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM);
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receiverPublicKey, true);

            return ToolSet.hexToString(shortenSecretKey(keyAgreement.generateSecret()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private static PublicKey restorePublicKeyFrom(String receiverKey) {
        Log.d(TAG, "Restore public key from message");
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(ToolSet.stringToHex(receiverKey));
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private static byte[] shortenSecretKey(final byte[] longKey) {
        Log.d(TAG, "Shorten secret key");
        try {
            final byte[] shortenedKey = new byte[16];
            System.arraycopy(longKey, 0, shortenedKey, 0, shortenedKey.length);

            return shortenedKey;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }
}