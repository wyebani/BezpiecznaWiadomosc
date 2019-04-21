package com.wyebani.bezpiecznawiadomosc.model;

import android.support.annotation.NonNull;

import com.orm.SugarRecord;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @EqualsAndHashCode(callSuper=false)
public class AesKey extends SugarRecord<AesKey> implements Serializable {

    @NonNull
    private String      receiverPhoneNo;
    @NonNull
    private Byte[]      receiverKey;
    @NonNull
    private Byte[]      senderKey;

}
