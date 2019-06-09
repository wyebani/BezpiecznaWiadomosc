package com.wyebani.bezpiecznawiadomosc.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.model.DHKeys;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.model.Message;
import com.wyebani.bezpiecznawiadomosc.model.Receiver;
import com.wyebani.bezpiecznawiadomosc.model.UserPin;
import com.wyebani.bezpiecznawiadomosc.tools.PermTools;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.util.TreeMap;

import com.wyebani.bezpiecznawiadomosc.service.SmsReceiverService;

public class LoadActivity extends BaseActivity {

    private static final String TAG = ToolSet.getTag(LoadActivity.class.toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        dropDb();
        initApp();
        startService(new Intent(this, SmsReceiverService.class));
    }

    private void initApp() {
        if( PermTools.isPermissionsGranted(LoadActivity.this, PERM_CONTACT) ) {
            if( PermTools.isPermissionsGranted(LoadActivity.this, PERM_SMS) ) {

                getContacts(getContentResolver());
                startActivity(
                        new Intent(LoadActivity.this, MainActivity.class)
                );
                finish();
            } else {
                PermTools.requestPermissions(LoadActivity.this,
                        PERM_SMS,
                        PERM_SMS_CODE);
            } /* PermTools.isPermissionsGranted(LoadActivity.this, PERM_SMS) */
        } else {
            PermTools.requestPermissions(LoadActivity.this,
                    PERM_CONTACT,
                    PERM_CONTACT_CODE);
        } /* PermTools.isPermissionsGranted(LoadActivity.this, PERM_CONTACT) */
    }

    private static void getContacts(final ContentResolver contentResolver) {
        Log.d(TAG, "updateContacts()");

        if( sContactMap == null ) {
            sContactMap = new TreeMap<>();
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor phones = contentResolver
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                null,
                                null,
                                null
                        );

                assert phones != null;
                while( phones.moveToNext() ) {
                    String name = phones.getString(
                            phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    );
                    String phoneNo = phones.getString(
                            phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    ).replaceAll("[()\\s-]+", "");

                    phoneNo = ToolSet.getStandardPhoneNo(phoneNo);
                    sContactMap.put(name, phoneNo);
                }
                phones.close();
                return null;
            }
        }.execute();
    }

    private void dropDb() {
        Log.d(TAG, "Cleaning database...");
        DHKeys.deleteAll(DHKeys.class);
        Conversation.deleteAll(Conversation.class);
        Message.deleteAll(Message.class);
        Receiver.deleteAll(Receiver.class);
        UserPin.deleteAll(UserPin.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch( requestCode ) {
            case PERM_CONTACT_CODE:
            case PERM_SMS_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initApp();
                }
                break;
        }
    }

}
