package com.wyebani.bezpiecznawiadomosc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.adapter.ConversationAdapter;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = ToolSet.getTag(MainActivity.class.toString());

    /* View */
    private RecyclerView        recyclerView;

    /* Adapter */
    private ConversationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Conversation> conversationList = Conversation.listAll(Conversation.class);
        adapter = new ConversationAdapter(conversationList, this::onItemClick);

        recyclerView = findViewById(R.id.mainA_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);
    }

    private void onItemClick(Conversation conversation) {
        Log.d(TAG, "Selected conversation with: " + conversation.getReceiver().getPhoneNo());
        Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
        intent.putExtra("conversation", conversation);
        startActivity(intent);
    }
}
