package com.wyebani.bezpiecznawiadomosc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.model.DHKeys;
import com.wyebani.bezpiecznawiadomosc.model.Message;
import com.wyebani.bezpiecznawiadomosc.model.Receiver;
import com.wyebani.bezpiecznawiadomosc.model.UserPin;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.util.List;

public class EnterPinActivity extends BaseActivity {

    private final static String  TAG = ToolSet.getTag(EnterPinActivity.class.toString());
    private final static Integer ATTEMPT_COUNT = 3;

    private Pinview             pinView;
    private AppCompatButton     acceptButton;
    private AppCompatButton     clearButton;

    private Integer             attemptCounter = 0;
    private UserPin             userPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        pinView         = findViewById(R.id.enterPinA_pinView);
        acceptButton    = findViewById(R.id.enterPinA_acceptBtn);
        clearButton     = findViewById(R.id.enterPinA_clearBtn);

        List<UserPin> listOfPin = UserPin.listAll(UserPin.class);
        if( listOfPin.isEmpty()
            || (listOfPin.size() > 1) ) {
            startActivity(
                    new Intent(EnterPinActivity.this, SetPinActivity.class)
            );
            finish();
        } else {
            userPin = listOfPin.get(0);
        }

        pinView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        acceptButton.setOnClickListener((View view) -> {
            String pin = pinView.getValue();
            if( pin.length() == pinView.getPinLength() ) {
                if( userPin.getUserPin().equals(pin) ) {
                    Log.d(TAG, "Login successful");
                    startActivity(
                            new Intent(EnterPinActivity.this, MainActivity.class)
                    );
                    finish();
                } else {
                    ++attemptCounter;
                    if( attemptCounter < ATTEMPT_COUNT ) {
                        Log.d(TAG, "Given pin is wrong, attempt remained: " + (ATTEMPT_COUNT - attemptCounter));

                        String showMessage = "Błędny kod PIN, pozostało prób: " + (ATTEMPT_COUNT - attemptCounter);
                        Toast.makeText(EnterPinActivity.this,
                                showMessage,
                                Toast.LENGTH_SHORT
                        ).show();

                        ToolSet.clearPinview(pinView);
                        pinView.requestFocus();
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    } else {
                        Log.d(TAG, "No login attempt remaining, drop DB.");
                        Conversation.deleteAll(Conversation.class);
                        DHKeys.deleteAll(DHKeys.class);
                        Message.deleteAll(Message.class);
                        Receiver.deleteAll(Receiver.class);
                        UserPin.deleteAll(UserPin.class);

                        startActivity(
                                new Intent(EnterPinActivity.this, SetPinActivity.class)
                        );
                        finish();
                    }
                }
            } else {
                Log.d(TAG, "Entered pin is too short");
                Toast.makeText(EnterPinActivity.this,
                        "Podany kod PIN jest za krótki",
                        Toast.LENGTH_SHORT
                ).show();
                ToolSet.clearPinview(pinView);
                pinView.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        clearButton.setOnClickListener((View view) -> {
            ToolSet.clearPinview(pinView);
            pinView.requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        });
    }


}
