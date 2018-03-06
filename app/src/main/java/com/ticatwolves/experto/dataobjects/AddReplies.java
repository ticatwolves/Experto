package com.ticatwolves.experto.dataobjects;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ticat on 31/1/18.
 */

public class AddReplies {
    String reply,by,on,photo;

    public AddReplies(String reply,String by,String photo){
        this.reply = reply;
        this.on = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        this.by = by;
        this.photo = photo;
    }
    public AddReplies(){}
    public String getBy(){return by;}
    public String getOn(){return on;}
    public String getReply(){return reply;}
    public String getPhoto(){return photo;}
}
