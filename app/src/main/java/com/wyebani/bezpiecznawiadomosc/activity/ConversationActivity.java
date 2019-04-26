package com.wyebani.bezpiecznawiadomosc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.adapter.MessageAdapter;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.model.Message;
import com.wyebani.bezpiecznawiadomosc.model.Receiver;
import com.wyebani.bezpiecznawiadomosc.tools.PermTools;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationActivity extends BaseActivity {

    private static final String TAG = ToolSet.getTag(ConversationActivity.class.toString());

    /* Views */
    private FloatingActionButton      sendButton;
    private ListView                  msgListView;
    private EditText                  msgContentTxt;
    private EditText                  receiverNoTxt;
    private Button                    contactButton;

    /* Fields */
    private Conversation              conversation;
    private Boolean                   isContact;

    /* Broadcast Receiver */
    private BroadcastReceiver         receiver;

    public ConversationActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        sendButton      = findViewById(R.id.conversationA_sendBtn);
        msgListView     = findViewById(R.id.conversationA_msgList);
        msgContentTxt   = findViewById(R.id.conversationA_msgContent);
        receiverNoTxt   = findViewById(R.id.conversationA_receiverNo);
        contactButton   = findViewById(R.id.conversationA_contactBtn);

        sendButton.setOnClickListener(v -> sendMessage());
        contactButton.setOnClickListener(v -> selectContact());
        receiverNoTxt.addTextChangedListener(new MyTextWatcher());

        registerReceiver();

        if( getIntent().hasExtra("conversation") ) {
            conversation =
                    (Conversation) getIntent().getSerializableExtra("conversation");

            if( conversation.getReceiver().getName() != null ) {
                isContact = true;
                String tmp  = conversation.getReceiver().getName();
                tmp += " (" + conversation.getReceiver().getPhoneNo() + ")";
                receiverNoTxt.setText(tmp);
            } else {
                isContact = false;
                receiverNoTxt.setText(conversation.getReceiver().getPhoneNo());
            } /* conversation.getReceiver().getName() == null */

            updateMessageListView();
        } else {
            conversation = null;
            isContact = false;
        } /* hasExtra("conversation") */
    }

    private void sendMessage() {
        Log.d(TAG, "sendMessage()");

        if( PermTools.isPermissionsGranted(ConversationActivity.this, PERM_SMS)) {
            if( msgContentTxt.getText().length() > 0 ) {
                if( receiverNoTxt.getText().length() > 0 ) {

                    String msg = msgContentTxt.getText().toString();
                    String rcvNo = receiverNoTxt.getText()
                            .toString()
                            .replaceAll(".*\\(|\\).*", "");
                    rcvNo = ToolSet.getStandardPhoneNo(rcvNo);

                    if( conversation == null ) {
                        conversation = Conversation.findByPhoneNo(rcvNo);
                    }

                    Receiver receiver = null;
                    if( conversation == null ) {
                        if ( sContactMap.containsValue(ToolSet.getStandardPhoneNo(rcvNo)) ) {
                            receiver = new Receiver(
                                    ToolSet.getNameByPhoneNo(BaseActivity.sContactMap, rcvNo),
                                    rcvNo,
                                    null /* TODO */
                            );
                        } else {
                            receiver = new Receiver(
                                    null,
                                    rcvNo,
                                    null /* TODO */
                            );
                        }
                        receiver.save();
                        conversation = new Conversation(receiver, new ArrayList<>());
                    } else {
                        receiver = conversation.getReceiver();
                    }/* conversation != null */

                    if( conversation.getReceiver().getPhoneNo().equals(rcvNo) ) {
                        /* TODO - szyfrowanie wiadomości */
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(rcvNo,
                                null,
                                msg,
                                null,
                                null
                        );

                        Log.d(TAG, "Message sent");
                        Log.d(TAG, "Receiver: " + rcvNo);
                        Log.d(TAG, "Message: " + msg);

                        Message message = new Message(
                                receiver,
                                msg,
                                true,
                                false,
                                false, /* TODO */
                                new Date(),
                                null      /* TODO */
                        );

                        conversation.addMessage(message);
                        conversation.save();
                        updateMessageListView();
                        msgContentTxt.getText().clear();
                    }

                } else {
                    Toast.makeText(ConversationActivity.this,
                            "Wpisz numer telefonu odbiorcy!",
                            Toast.LENGTH_SHORT
                    ).show();
                    Log.d(TAG, "Receiver phone number is empty!");
                } /* receiverNoTxt.getText().length() > 0 */
            } else {
                Toast.makeText(ConversationActivity.this,
                        "Wpisz treść wiadomości!",
                        Toast.LENGTH_SHORT
                ).show();
                Log.d(TAG, "Message content is empty!");
            } /* msgContentTxt.getText().length() > 0 */
        } else {
            Log.d(TAG, "SMS permissions denied!");
            PermTools.requestPermissions(ConversationActivity.this,
                    PERM_SMS,
                    PERM_CONTACT_CODE);
        } /* PermTools.isPermissionsGranted(ConversationActivity.this, PERM_SMS)) */
    }

    private void selectContact() {
        Log.d(TAG, "selectContact()");
        registerReceiver();
        final int SELECT_CONTACT_REQUEST = 1;
        Intent selectContactIntent
                = new Intent(ConversationActivity.this, SelectContactActivity.class);
        startActivityForResult(selectContactIntent, SELECT_CONTACT_REQUEST);
    }

    private void updateMessageListView() {
        Log.d(TAG, "updateMessageListView()");
        conversation.refreshMessages();
        MessageAdapter adapter
                = new MessageAdapter(conversation, ConversationActivity.this);
        msgListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void registerReceiver() {
        receiver = new BroadcastReceiver()  {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateMessageListView();
            }
        };
        registerReceiver(receiver, new IntentFilter("sms.received"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final int SELECT_CONTACT_REQUEST = 1;

        if ( requestCode == SELECT_CONTACT_REQUEST ) {

            if ( resultCode == RESULT_OK
                    && data != null ) {

                String contactName = data.getStringExtra("contactName");
                String contactPhoneNo = data.getStringExtra("contactPhoneNo");

                if ( contactName != null
                        && contactPhoneNo != null ) {

                    String tmp = contactName + " (" + contactPhoneNo + ")";
                    receiverNoTxt.setText(tmp);
                    isContact = true;
                } /* contactName != null && contactPhoneNo != null */
            }  /* resultCode == RESULT_OK && data != null */
        } /* requestCode == SELECT_CONTACT_REQUEST */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch ( requestCode ) {
            case PERM_SMS_CODE: {
                if( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    sendMessage();
                }
                break;
            }
        }
    }

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if( isContact ) {
                isContact = false;
                receiverNoTxt.setText("");
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}

