package com.wyebani.bezpiecznawiadomosc.sms;

public abstract class SmsBase {

    public static final int MSG_TYPE_OTHER                  = 0x00;
    public static final int MSG_TYPE_KEY_EXCHANGE_REQUEST   = 0x01;
    public static final int MSG_TYPE_KEY_EXCHANGE_RESPONSE  = 0x02;
    public static final int MSG_TYPE_ENCRYPTED              = 0x03;

    public static String createKeyExchangeRequest(String key) {
        return MSG_TYPE_KEY_EXCHANGE_REQUEST + key;
    }

    public static String createKeyExchangeResponse(String key) {
        return MSG_TYPE_KEY_EXCHANGE_RESPONSE + key;
    }

}
