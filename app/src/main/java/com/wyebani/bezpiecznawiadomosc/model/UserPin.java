package com.wyebani.bezpiecznawiadomosc.model;

import android.support.annotation.NonNull;

import com.orm.SugarRecord;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @EqualsAndHashCode(callSuper=false)
public class UserPin extends SugarRecord<UserPin> implements Serializable {

    private Long        id;
    @NonNull
    private String      userPin;

}
