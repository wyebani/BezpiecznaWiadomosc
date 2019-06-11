package com.wyebani.bezpiecznawiadomosc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.model.UserPin;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

public class SetPinActivity extends BaseActivity {

    private final static String TAG = ToolSet.getTag(SetPinActivity.class.toString());

    private Pinview                setPinView;
    private Pinview                confirmPinView;
    private AppCompatButton        clearBtn;
    private AppCompatButton        acceptBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        setPinView      = findViewById(R.id.setPinA_pinView);
        confirmPinView  = findViewById(R.id.setPinA_pinViewConfirm);
        clearBtn        = findViewById(R.id.setPinA_clearBtn);
        acceptBtn       = findViewById(R.id.setPinA_acceptBtn);

        setPinView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        setPinView.setPinViewEventListener((Pinview pv, boolean fromUser) -> {
            if( pv.getValue().length() == pv.getPinLength() ) {
                pv.clearFocus();
                confirmPinView.requestFocus();
            }
        });

        confirmPinView.setPinViewEventListener((Pinview pv, boolean fromUser) -> {
            if( pv.getValue().length() == pv.getPinLength() ) {
                pv.clearFocus();
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        clearBtn.setOnClickListener((View view) -> {
            ToolSet.clearPinview(setPinView);
            ToolSet.clearPinview(confirmPinView);

            setPinView.requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        });

        acceptBtn.setOnClickListener((View view) -> {
            String pin        = setPinView.getValue();
            String confirmPin = confirmPinView.getValue();

            if( pin.length() < setPinView.getPinLength()
                && confirmPin.length() < confirmPinView.getPinLength() ) {
                Log.d(TAG, "To short pin entered");
                Toast.makeText(SetPinActivity.this,
                        "Podano za krótki kod PIN",
                        Toast.LENGTH_SHORT
                ).show();

                ToolSet.clearPinview(setPinView);
                ToolSet.clearPinview(confirmPinView);

                setPinView.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }

            if( pin.equals(confirmPin) ) {
                UserPin userPin = new UserPin(pin);
                userPin.save();

                startActivity(
                        new Intent(SetPinActivity.this, EnterPinActivity.class)
                );
                finish();
            } else {
                Log.d(TAG, "Given pin codes not equals!");
                Toast.makeText(SetPinActivity.this,
                        "Podane kody pin różnią się"
                        , Toast.LENGTH_SHORT
                ).show();
                clearBtn.callOnClick();
            }
        });
    }

}
