package com.wyebani.bezpiecznawiadomosc.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.listener.ConversationItemClickListener;
import com.wyebani.bezpiecznawiadomosc.model.Conversation;
import com.wyebani.bezpiecznawiadomosc.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> implements Filterable {

    private List<Conversation>              conversationList;
    private List<Conversation>              conversationListFull;
    private ConversationItemClickListener   clickListener;

    public ConversationAdapter(List<Conversation> conversationList, ConversationItemClickListener clickListener) {
        this.conversationList   = conversationList;
        conversationListFull = new ArrayList<>(conversationList);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_conversation, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(conversationList.get(i), clickListener);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView contactName;
        private TextView lastMessage;
        private TextView dateTxt;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName   = itemView.findViewById(R.id.conversationItem_contactName);
            lastMessage   = itemView.findViewById(R.id.conversationItem_lastMessage);
            dateTxt       = itemView.findViewById(R.id.conversationItem_date);
        }

        void bind(final Conversation conversation, final ConversationItemClickListener listener) {
            String cName = conversation.getReceiver().getName();
            if( cName == null ) {
                cName = conversation.getReceiver().getPhoneNo();
            }
            String snippet = "";
            Date date = null;
            Message msg = conversation.getLastMessage();
            if( msg != null ) {
                if( msg.getMsgContent().length() > 45 ) {
                    snippet = msg.getMsgContent()
                            .substring(0, 45);
                    snippet += "..." ;
                } else {
                    snippet = msg.getMsgContent();
                }
                date = conversation.getLastMessage().getDate();
            }

            String sentTime = "";
            if( date != null ) {
                Date nowDate = new Date();
                String today  = DateFormat.format("dd", nowDate).toString();
                String sentDay = DateFormat.format("dd", date).toString();

                if( today.equals(sentDay) ) {
                    sentTime = DateFormat.format("HH:mm", date).toString();
                } else {
                    sentTime = DateFormat.format("EEEE HH:mm", date).toString();
                }

            }

            contactName.setText(cName);
            lastMessage.setText(snippet);
            dateTxt.setText(sentTime);

            itemView.setOnClickListener((View view) -> {
                listener.onItemClick(conversation);
            });
        }
    }
    @Override
    public Filter getFilter(){
        return contactFilter;
    }

    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Conversation> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(conversationListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Conversation conversation : conversationListFull) {
                    if (conversation.getReceiver().getPhoneNo().contains(filterPattern)) {
                        Log.d("receiverdata", conversation.getReceiver().getPhoneNo());
                        filteredList.add(conversation);
                    }
                   /* if (conversation.getReceiver().getName().toLowerCase().contains(filterPattern)) {
                        Log.i("receiverdata",conversation.getReceiver().getName());
                        filteredList.add(conversation);
                        //TODO (Paweł) ^działa  tylko dla numeru
                    }*/
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            conversationList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
