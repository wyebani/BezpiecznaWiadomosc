package com.wyebani.bezpiecznawiadomosc.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyebani.bezpiecznawiadomosc.R;
import com.wyebani.bezpiecznawiadomosc.listener.ContactItemClickListener;

import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Map<String, String>         contactMap;
    private String[]                    mapKeys;
    private ContactItemClickListener    clickListener;

    public ContactAdapter(Map<String, String> contactMap, ContactItemClickListener clickListener) {
        this.contactMap             = contactMap;
        this.clickListener          = clickListener;
        this.mapKeys                = contactMap.keySet().toArray(new String[getItemCount()]);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_contact, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mapKeys[i], contactMap.get(mapKeys[i]), clickListener);
    }

    @Override
    public int getItemCount() {
        return contactMap.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView contactName;
        private TextView contactPhone;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName   = itemView.findViewById(R.id.contactItem_contactName);
            contactPhone  = itemView.findViewById(R.id.contactItem_contactPhone);
        }

        void bind(final String cName, final String cPhoneNo, final ContactItemClickListener listener) {
            contactName.setText(cName);
            contactPhone.setText(cPhoneNo);

            itemView.setOnClickListener((View view) -> {
                listener.onItemClick(cName, cPhoneNo);
            });
        }
    }

}
