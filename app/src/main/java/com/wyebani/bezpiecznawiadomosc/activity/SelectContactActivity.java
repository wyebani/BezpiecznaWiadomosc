package com.wyebani.bezpiecznawiadomosc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.adapter.ContactAdapter;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

public class SelectContactActivity extends BaseActivity {

    private static final String TAG = ToolSet.getTag(SelectContactActivity.class.toString());

    /* Views */
    private RecyclerView recyclerView;

    /* Adapters */
    private ContactAdapter adapter;

    // TODO(Paweł) - po wysłaniu wiadomosci - wyczyscic
    //    // TODO dorobić odswiezanie aktualnych konwersacji
    //    // TODO problem z wyswietlaniem wiadomosci od odbiorcy
    //    // google.com -> how to filter RecyclerView
    //    // Dodać przycisk (lupę) na górnym pasku i pole do wpisania
    //    // Przykład: https://www.youtube.com/watch?v=sJ-Z9G0SDhc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        adapter = new ContactAdapter(sContactMap, this::onItemClick);

        recyclerView = findViewById(R.id.selectContactA_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectContactActivity.this));
        recyclerView.setAdapter(adapter);
    }

    private void onItemClick(String contactName, String contactPhoneNo) {
        Log.d(TAG, "Selected contact: " + contactName + " (" + contactPhoneNo + ")");

        Intent data = new Intent();
        data.putExtra("contactName", contactName);
        data.putExtra("contactPhoneNo", contactPhoneNo);
        setResult(RESULT_OK, data);
        finish();
    }
}
