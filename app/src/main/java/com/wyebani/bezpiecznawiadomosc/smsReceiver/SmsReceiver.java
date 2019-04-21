package com.wyebani.bezpiecznawiadomosc.smsReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.activity.BaseActivity;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.model.Message;
import com.wyebani.bezpiecznawiadomosc.model.Receiver;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class SmsReceiver extends BroadcastReceiver {

    private final static String TAG = ToolSet.getTag(SmsReceiver.class.toString());
    private final static String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if( Objects.equals(intent.getAction(), SMS_RECEIVED) ) {
            Bundle dataBundle = intent.getExtras();

            if( dataBundle != null ) {
                Object[] myPdu = (Object[]) dataBundle.get("pdus");

                if( myPdu != null ) {
                    final SmsMessage[] smsMessage = new SmsMessage[myPdu.length];

                    for ( int i=0; i<myPdu.length; ++i ) {
                        String format = dataBundle.getString( "format" );
                        smsMessage[i] = SmsMessage.createFromPdu( (byte[]) myPdu[i], format );

                        String msg = smsMessage[i].getMessageBody();
                        String phoneNo = smsMessage[i].getOriginatingAddress();

                        if( msg == null || phoneNo == null ) {
                            return;
                        }

                        Log.d(TAG, "Message receiver");
                        Log.d(TAG, "Sender: " + phoneNo);
                        Log.d(TAG, "Message: " + msg);

                        Message message;


                        Conversation conversation = Conversation.findByPhoneNo(phoneNo);

                        if( conversation == null ) {
                            Receiver receiver = Receiver.findByPhoneNo(phoneNo);
                            if( receiver == null ) {
                                if( BaseActivity
                                        .sContactMap
                                        .containsValue(ToolSet.getStandardPhoneNo(phoneNo)) ) {
                                    receiver = new Receiver(
                                            ToolSet.getNameByPhoneNo(BaseActivity.sContactMap, phoneNo),
                                            phoneNo,
                                            null /* TODO */
                                    );
                                } else {
                                    receiver = new Receiver(
                                            null,
                                            phoneNo,
                                            null /* TODO */
                                    );
                                }
                            }
                            conversation = new Conversation(receiver, new ArrayList<>());
                        } /* conversation == null */

                        message = new Message(
                                conversation.getReceiver(),
                                msg,
                                false,
                                false,
                                false, /* TODO */
                                new Date(),
                                null      /* TODO */
                        );

                        conversation.addMessage(message);
                        conversation.save();
                        updateView(context);
                    }
                } /* myPdu != null */
            } /* dataBundle != null */
        } /* Objects.equals(intent.getAction(), SMS_RECEIVED) */
    }

    private void updateView(Context context) {
        Intent intent = new Intent("sms.received");
        context.sendBroadcast(intent);
    }
}
