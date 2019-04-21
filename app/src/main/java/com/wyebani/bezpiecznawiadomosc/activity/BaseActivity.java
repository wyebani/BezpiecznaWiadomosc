package com.wyebani.bezpiecznawiadomosc.activity;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;

import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity {

    /* Permissions codes & Manifest permissions */
    public static final  int                PERM_SMS_CODE       = 101;
    public static final  int                PERM_EXTERNAL_CODE  = 102;
    public static final  int                PERM_CONTACT_CODE   = 103;

    public static final String[]            PERM_SMS            =
            {
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS
            };
    public static final String[]            PERM_EXTERNAL       =
            {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
    public static final String[]            PERM_CONTACT        =
            {
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS
            };

    public static Map<String, String>       sContactMap;

}
