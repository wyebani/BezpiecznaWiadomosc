package com.wyebani.bezpiecznawiadomosc.crypto;

import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

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

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private final static String TAG = ToolSet.getTag(Crypto.class.toString());

    public static byte[] encryptMessage(final byte[] message, byte[] secretKey) {
        Log.d(TAG, "Encrypting message");
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            final Cipher cipher  = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(message);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static byte[] decryptMessage(byte[] message, byte[] secretKey) {
        Log.d(TAG, "Decrypting message");
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            final Cipher cipher  = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return cipher.doFinal(message);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static KeyPair generateKeys() {
        Log.d(TAG, "Generating keys");
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static byte[] generateCommonSecretKey(byte[] myPrivateKey, byte[] receivedPublicKey) {
        Log.d(TAG, "Generate receiver private key");
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(myPrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            PublicKey receiverPublicKey = restorePublicKeyFrom(receivedPublicKey);

            final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receiverPublicKey, true);

            return shortenSecretKey(keyAgreement.generateSecret());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private static PublicKey restorePublicKeyFrom(byte[] receiverKey) {
        Log.d(TAG, "Restore public key from message");
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(receiverKey);
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
