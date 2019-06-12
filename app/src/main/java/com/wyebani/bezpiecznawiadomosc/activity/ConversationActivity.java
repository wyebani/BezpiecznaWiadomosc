package com.wyebani.bezpiecznawiadomosc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.adapter.MessageAdapter;
import com.wyebani.bezpiecznawiadomosc.crypto.AES;
import com.wyebani.bezpiecznawiadomosc.crypto.DiffieHellman;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.model.DHKeys;
import com.wyebani.bezpiecznawiadomosc.model.Message;
import com.wyebani.bezpiecznawiadomosc.model.Receiver;
import com.wyebani.bezpiecznawiadomosc.sms.SmsBase;
import com.wyebani.bezpiecznawiadomosc.sms.smsSender.SmsSender;
import com.wyebani.bezpiecznawiadomosc.tools.PermTools;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.security.KeyPair;
import java.util.Date;

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
    private Boolean                   encryptingEnable;

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
        encryptingEnable = false;
        if( getIntent().hasExtra("conversation") ) {
            conversation =
                    (Conversation) getIntent().getSerializableExtra("conversation");
            // This is need because of field id which is not serializable by SugarORM
            Receiver receiver = Receiver.findByPhoneNo(conversation.getReceiver().getPhoneNo());
            conversation.setReceiver(receiver);

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

                    conversation = ToolSet.getConversationByPhoneNo(rcvNo);
                    Receiver receiver = conversation.getReceiver();

                    if( conversation.getReceiver().getPhoneNo().equals(rcvNo) ) {
                        String showMsg = msg;
                        if( encryptingEnable ) {
                            if( conversation.getReceiver().getDhKeys() != null ) {
                                String myPrivKey = conversation.getReceiver().getDhKeys().getMyPrivateKey();
                                String rcvPubKey = conversation.getReceiver().getDhKeys().getReceiverPubKey();
                                String secretKey = DiffieHellman.generateCommonSecretKey(myPrivKey, rcvPubKey);
                                if( secretKey == null ) {
                                    secretKey = "hBg9Om7XVRKcsQA17xyGcw==\n";
                                }
                                AES aesKey = new AES(secretKey);

                                String encryptedMsg = aesKey.encrypt(msg);
                                msg = SmsBase.createEncryptedMessage(encryptedMsg);
                            } else {
                                Log.d(TAG, "Cannot encrypt message! Key exchange needed");
                                Toast.makeText(ConversationActivity.this,
                                        "Konieczna wymiana kluczy",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        }
                        Message message = new Message(
                                receiver,
                                showMsg,
                                true,
                                encryptingEnable,
                                new Date()
                        );

                        SmsSender.sendSms(receiver, msg);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_key_exchange, menu);

        MenuItem keyExchangeButton = menu.findItem(R.id.action_keyExchange);
        keyExchangeButton.setOnMenuItemClickListener( (MenuItem item) -> {
            if( !receiverNoTxt.getText().toString().isEmpty() ) {

                String phoneNo = receiverNoTxt.getText()
                        .toString()
                        .replaceAll(".*\\(|\\).*", "");
                phoneNo = ToolSet.getStandardPhoneNo(phoneNo);
                conversation = ToolSet.getConversationByPhoneNo(phoneNo);
                
                if( conversation != null ) {
                    KeyPair keyPair = DiffieHellman.generateKeys();
                    String myPrivateKey = ToolSet.hexToString(keyPair.getPrivate().getEncoded());
                    String myPublicKey = ToolSet.hexToString(keyPair.getPublic().getEncoded());
                    if( conversation.getReceiver().getDhKeys() == null ) {
                        DHKeys dhk = new DHKeys();
                        dhk.setReceiverPhoneNo(phoneNo);
                        dhk.setMyPrivateKey(myPrivateKey);
                        conversation.getReceiver().setDhKeys(dhk);
                        conversation.getReceiver().save();
                        dhk.save();
                    }
                    conversation.getReceiver().getDhKeys().setMyPrivateKey(myPrivateKey);
                    conversation.getReceiver().getDhKeys().save();
                    conversation.getReceiver().save();
                    conversation.save();
                    String msg = SmsBase.createKeyExchangeRequest(myPublicKey);
                    SmsSender.sendSms(conversation.getReceiver(), msg);
                    Message message = new Message(conversation.getReceiver(),
                                                  myPublicKey,
                                                  true,
                                                  true,
                                                  new Date()
                    );
                    conversation.addMessage(message);
                    conversation.save();
                    updateMessageListView();
                }
            } else {
                Toast.makeText(ConversationActivity.this,
                        "Wpisz numer telefonu odbiorcy!",
                        Toast.LENGTH_SHORT
                ).show();
                Log.d(TAG, "Receiver phone number is empty!");
            }
            return true;
        });

        MenuItem encryptingEnableCheckBox = menu.findItem(R.id.encryptingEnable);
        //encryptingEnableCheckBox.setCheckable(true);
        encryptingEnableCheckBox.setOnMenuItemClickListener((MenuItem menuItem) -> {
            if(!encryptingEnableCheckBox.isChecked()) {
                Log.d(TAG, "Encrypting enabled");
                encryptingEnableCheckBox.setChecked(true);
                encryptingEnable = true;
            } else {
                Log.d(TAG, "Encrypting disabled");
                encryptingEnableCheckBox.setChecked(false);
                encryptingEnable = false;
            }

            return true;
        });
        return true;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

