package com.wyebani.bezpiecznawiadomosc.tools;

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
}
