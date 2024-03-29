package com.wyebani.bezpiecznawiadomosc.model;


import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @EqualsAndHashCode(callSuper=false)
public class Message extends SugarRecord<Message> implements Serializable {

    private Receiver    receiver;
    private String      msgContent;
    private Boolean     sendByUser;
    private Boolean     isEncrypted;
    private Date        date;

}
