package com.wyebani.bezpiecznawiadomosc.tools;

import android.util.Base64;
import android.widget.EditText;

import com.goodiebag.pinview.Pinview;
import com.wyebani.bezpiecznawiadomosc.activity.BaseActivity;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.model.Receiver;

import java.util.ArrayList;
import java.util.Map;

public class ToolSet {

    /**
     * Returns debug tag for class
     * @param className - class name
     * @return Application debug tag
     */
    public static String getTag(String className) {
        return "appLogger:" + className;
    }

    /**
     * Validate phone number with/without +48
     * @param p1 - Phone number to validate
     * @param p2 - Phone number to compare
     * @return true or false
     */
    public static boolean validatePhoneNo(String p1, String p2) {
        if ( p1 != null ) {
            if ( p1.equals(p2) ) {
                return true;
            }

            if ( !p1.startsWith("+") ) {
                String temp = "+48" + p1;
                return temp.equals(p2);
            }
        }
        return false;
    }

    /**
     * Function added +48 if needed
     * @param phoneNo - phone number to check
     * @return Correct phone number
     */
    public static String getStandardPhoneNo(String phoneNo) {
        if ( !phoneNo.startsWith("+") ) {
            String tmp = phoneNo;
            phoneNo = "+48" + tmp;
        }

        return phoneNo;
    }

    /**
     * Find contact name in map by phone no as Key
     * @param map - TreeMap<Name, PhoneNo>
     * @param phoneNo - In phone No
     * @return Name of contact / null if not found
     */
    public static String getNameByPhoneNo(Map<String, String> map, String phoneNo) {
        for( Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();

            if( val.equals(phoneNo) ) {
                return key;
            }
        }
        return null;
    }

    /**
     * Change hex value into String
     * @param hex - byte array
     * @return String with hex value
     */
    public static String hexToString(byte[] hex) {
        return Base64.encodeToString(hex, Base64.DEFAULT);
    }

    /**
     * Change String value into byte array
     * @param hex - String with hex value
     * @return byte array
     */
    public static byte[] stringToHex(String hex) {
        return Base64.decode(hex, Base64.DEFAULT);
    }

    /**
     * Check if conversation exists in DB:
     *       if yes => return
     *       if no  => create new Conversation object
     * @param phoneNo - receiver phone number
     * @return Conversation object
     */
    public static Conversation getConversationByPhoneNo(String phoneNo) {
        Conversation conversation = null;
        conversation = Conversation.findByPhoneNo(phoneNo);

        if( conversation == null ) {
            Receiver receiver = null;
            if ( BaseActivity.sContactMap.containsValue(ToolSet.getStandardPhoneNo(phoneNo)) ) {
                receiver = new Receiver(
                        ToolSet.getNameByPhoneNo(BaseActivity.sContactMap, phoneNo),
                        phoneNo,
                        null
                );
            } else {
                receiver = new Receiver(
                        "",
                        phoneNo,
                        null
                );
            }
            receiver.save();
            conversation = new Conversation(receiver, new ArrayList<>());
            conversation.save();
        }

        return conversation;
    }

    /**
     * Returns binary data from string
     * @param str - in string
     * @param numberOfCharactersWanted - number of wanted characters
     * @return string with binary data
     */
    public static String getBinary(String str, int numberOfCharactersWanted) {
        StringBuilder result = new StringBuilder();
        byte[] byt = str.getBytes();
        for (int i = 0; i < numberOfCharactersWanted; i++) {
            result.append(String.format("%8s", Integer.toBinaryString(byt[i])).replace(' ', '0')).append(' ');
        }
        return result.toString();
    }

    /**
     * Clear pinview value
     * @param pv - pinview to clear
     */
    public static void clearPinview(Pinview pv) {
        for(int i=0; i<pv.getPinLength(); ++i) {
            EditText edittext = (EditText) pv.getChildAt(i);
            edittext.setText("");
        }
    }
}
