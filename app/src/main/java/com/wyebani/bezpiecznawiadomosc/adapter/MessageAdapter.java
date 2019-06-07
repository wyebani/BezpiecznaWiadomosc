package com.wyebani.bezpiecznawiadomosc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;

public class MessageAdapter extends BaseAdapter {

    /* Fields */
    private Conversation        conversation;
    private LayoutInflater      layoutInflater;

    public MessageAdapter(Conversation conversation, Context context) {
        this.conversation = conversation;
        this.layoutInflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return conversation.getMessages().size();
    }

    @Override
    public Object getItem(int position) {
        return conversation.getMessages().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if( view == null ) {
            if ( conversation.getMessages().get(position).getSendByUser() ) {
                //TODO check if it's working
                if(conversation.getMessages().get(position).getIsEncrypted()){
                    view = layoutInflater.inflate(R.layout.list_item_msg_send_encrypted, null);
                }
                else {
                    view = layoutInflater.inflate(R.layout.list_item_msg_send, null);
                }

            } else {
                if(conversation.getMessages().get(position).getIsEncrypted()){
                    view = layoutInflater.inflate(R.layout.list_item_msg_recv_encrypted, null);
                }
                else{
                    view = layoutInflater.inflate(R.layout.list_item_msg_recv, null);
                }
            }

            BubbleTextView txtMessage = view.findViewById(R.id.msg_list_item);
            txtMessage.setText(conversation.getMessages().get(position).getMsgContent());
        }
        return view;
    }
}
