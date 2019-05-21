package com.wyebani.bezpiecznawiadomosc.model;

import android.support.annotation.NonNull;

import com.orm.SugarRecord;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @EqualsAndHashCode(callSuper=false)
public class DHKeys extends SugarRecord<DHKeys> implements Serializable {

    @NonNull
    private String      receiverPhoneNo;
    private String      receiverPublicKey;
    private String      senderPrivateKey;

}
