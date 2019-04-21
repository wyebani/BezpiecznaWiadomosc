package com.wyebani.bezpiecznawiadomosc.model;

import android.support.annotation.NonNull;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @EqualsAndHashCode(callSuper=false)
public class Conversation extends SugarRecord<Conversation> implements Serializable {

    @NonNull
    private Receiver            receiver;
    @Ignore
    private List<Message>       messages;

    public static Conversation findByPhoneNo(String phoneNo) {
        Receiver receiver = Receiver.findByPhoneNo(phoneNo);
        if( receiver != null ) {
            List<Conversation> conversationList
                    = Conversation.find(Conversation.class,
                    "receiver = ?",
                    String.valueOf(receiver.getId()));

            if( conversationList.size() == 1 ) {
                return conversationList.get(0);
            }
        } /* receiver != null */

        return null;
    }

    public List<Message> getMessages() {
        if(  messages == null ) {
            messages = Message.find(Message.class, "receiver = ?", String.valueOf(receiver.getId()));
        }
        return messages;
    }

    public void addMessage(Message message) {
        if( messages == null ) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        message.save();
    }

    public void addAllMessages(List<Message> messageList) {
        messages.addAll(messageList);
        for( Message message : messageList ) {
            message.save();
        }
    }

    public Message getLastMessage() {
        if( messages == null ) {
            messages = getMessages();
        }
        return messages.get(messages.size() - 1);
    }

    @Override
    public void save() {
        super.save();
        for( Message message : messages ) {
            message.save();
        }
    }

}
