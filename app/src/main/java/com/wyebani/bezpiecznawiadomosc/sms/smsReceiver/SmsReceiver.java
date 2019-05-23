package com.wyebani.bezpiecznawiadomosc.sms.smsReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.activity.BaseActivity;
import com.wyebani.bezpiecznawiadomosc.crypto.DiffieHellman;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.model.DHKeys;
import com.wyebani.bezpiecznawiadomosc.model.Message;
import com.wyebani.bezpiecznawiadomosc.model.Receiver;
import com.wyebani.bezpiecznawiadomosc.sms.SmsBase;
import com.wyebani.bezpiecznawiadomosc.sms.smsSender.SmsSender;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static java.lang.Integer.getInteger;

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

                        Log.d(TAG, "Message received");
                        Log.d(TAG, "Sender: " + phoneNo);
                        Log.d(TAG, "Message: " + msg);

                        int msgType = getMessageType(msg);
                        switch( msgType ) {
                            case SmsBase.MSG_TYPE_ENCRYPTED:
                                processEncryptedMessage(msg, phoneNo, context);
                                break;

                            case SmsBase.MSG_TYPE_KEY_EXCHANGE_REQUEST:
                                processKeyExchangeRequest(msg, phoneNo, context);
                                break;

                            case SmsBase.MSG_TYPE_OTHER:
                            default:
                                processMessage(msg, phoneNo, context);
                                break;
                        }

                    }
                } /* myPdu != null */
            } /* dataBundle != null */
        } /* Objects.equals(intent.getAction(), SMS_RECEIVED) */
    }

    private void processEncryptedMessage(String msg, String phoneNo, Context context) {
        // TODO - sprawdzić czy są klucze i czy udało się odszyfrować
        // jeżeli tak to ok
        // jeżeli nie ma, to wygenerować klucz i wysłać
        // jeżeli się nie udało odszyfrować, to wygenerować klucz i wysłać
    }

    private void processKeyExchangeRequest(String msg, String phoneNo, Context context) {
        KeyPair keyPair = DiffieHellman.generateKeys();

        String receiverPubKey = msg.substring(1);
        String myPrivateKey = ToolSet.hexToString(keyPair.getPrivate().getEncoded());
        String myPublicKey = ToolSet.hexToString(keyPair.getPublic().getEncoded());

        String secretKey = DiffieHellman.generateCommonSecretKey(
                myPrivateKey,
                receiverPubKey
        );
        DHKeys dhKeys = new DHKeys(phoneNo, receiverPubKey, myPrivateKey);
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
                            null
                    );
                } else {
                    receiver = new Receiver(
                            null,
                            phoneNo,
                            null
                    );
                }
            }
            conversation = new Conversation(receiver, new ArrayList<>());
        } /* conversation == null */

        Message message = new Message(
                conversation.getReceiver(),
                msg,
                false,
                true,
                true,
                new Date(),
                dhKeys
        );
        conversation.addMessage(message);
        conversation.getReceiver().setDHKeys(dhKeys);
        conversation.save();
        updateView(context);

        String response = SmsSender.createKeyExchangeResponse(myPublicKey);
        SmsSender.sendSms(conversation.getReceiver(), response);
    }

    private void processMessage(String msg, String phoneNo, Context context) {
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

    private int getMessageType(String msg) {
        int msgType = SmsBase.MSG_TYPE_OTHER;
        boolean isDigit = Character.isDigit(msg.charAt(0));
        if( isDigit ) {
            msgType = (int) msg.charAt(0) - 48;
        }
        return msgType;
    }

    private void updateView(Context context) {
        Intent intent = new Intent("sms.received");
        context.sendBroadcast(intent);
    }
}
