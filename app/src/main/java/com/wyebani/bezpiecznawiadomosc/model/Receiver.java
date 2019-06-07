package com.wyebani.bezpiecznawiadomosc.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @EqualsAndHashCode(callSuper=false)
public class Receiver extends SugarRecord<Receiver> implements Serializable {

    @Nullable
    private String      name;
    @NonNull
    private String      phoneNo;
    @Nullable
    private DHKeys      dhKeys;

    public static Receiver findByPhoneNo(String phoneNo) {
        List<Receiver> receiverList
                = Receiver.find(Receiver.class, "phone_no = ?", phoneNo);

        if( receiverList.size() == 1  ) {
            Receiver receiver = receiverList.get(0);
            List<DHKeys> dhList = DHKeys.find(DHKeys.class, "receiver_phone_no = ?", receiver.getPhoneNo());
            if( dhList.size() == 1) {
                receiver.setDhKeys(dhList.get(0));
            }

            return receiver;
        }

        return null;
    }

}
