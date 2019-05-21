package com.wyebani.bezpiecznawiadomosc.crypto;

import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private final static String TAG = ToolSet.getTag(AES.class.toString());
    private final static String ALGORITHM = "AES";

    private final byte[] keyValue;

    public AES(String key) {
        keyValue = key.getBytes();
    }

    public String encrypt(String message) {
        Log.e(TAG, "Encrypting message");

        String encryptedMsg = null;
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = cipher.doFinal(message.getBytes());
            encryptedMsg = ToolSet.hexToString(encVal);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return encryptedMsg;
    }

    public String decrypt(String encryptedMessage) {
        Log.e(TAG, "Decrypting message");

        String decryptedMessage = null;
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedVal = ToolSet.stringToHex(encryptedMessage);
            byte[] decValue = cipher.doFinal(decodedVal);
            decryptedMessage = new String(decValue);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return decryptedMessage;
    }

    private Key generateKey() {
        return new SecretKeySpec(keyValue, ALGORITHM);
    }

}
