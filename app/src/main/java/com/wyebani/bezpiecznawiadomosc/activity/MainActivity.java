package com.wyebani.bezpiecznawiadomosc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;

import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.adapter.ConversationAdapter;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.tools.ToolSet;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final String TAG = ToolSet.getTag(MainActivity.class.toString());

    /* View */
    private RecyclerView            recyclerView;
    private FloatingActionButton    newConversationButton;

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
        adapter.notifyDataSetChanged();

        newConversationButton = findViewById(R.id.mainA_newConversationButton);
        newConversationButton.setOnClickListener(v -> newConversation());
    }

    private void onItemClick(Conversation conversation) {
        Log.d(TAG, "Selected conversation with: " + conversation.getReceiver().getPhoneNo());
        Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
        intent.putExtra("conversation", conversation);
        startActivity(intent);
    }

    private void newConversation(){
        Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
        startActivity(intent);
        Log.d(TAG, "New conversation created");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.conversation_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Conversation> conversationList = Conversation.listAll(Conversation.class);
        adapter = new ConversationAdapter(conversationList, this::onItemClick);
        recyclerView.setAdapter(adapter);
    }
}
