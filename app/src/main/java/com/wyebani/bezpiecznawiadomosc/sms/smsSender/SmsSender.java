package com.wyebani.bezpiecznawiadomosc.sms.smsSender;

import android.telephony.SmsManager;
import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.model.Receiver;
import com.wyebani.bezpiecznawiadomosc.sms.SmsBase;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

public class SmsSender extends SmsBase {
    private final static String TAG = ToolSet.getTag(SmsSender.class.toString());

    public static void sendSms(Receiver receiver, String message) {
        Log.d(TAG, "sendSms()");

        if( receiver != null
            && !message.isEmpty() ) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(receiver.getPhoneNo(),
                    null,
                    message,
                    null,
                    null
            );

            Log.d(TAG, "Message sent");
            Log.d(TAG, "Receiver: " + receiver.getPhoneNo());
            Log.d(TAG, "Message: " + message);
        } else {
            Log.d(TAG, "Cannot send message!");
        }
    }
}
