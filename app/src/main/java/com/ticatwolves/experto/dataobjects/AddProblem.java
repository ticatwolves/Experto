package com.ticatwolves.experto.dataobjects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ticat on 31/1/18.
 */

public class AddProblem {
    String statement,describtion,time,by,tag;
    //DataSnapshot replies;

    public AddProblem(String statement,String describtion,String tag,String name){
        this.statement = statement;
        this.describtion = describtion;
        this.time = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        this.tag = tag;
        this.by = name;
    }

    public AddProblem(){}

    public String getStatement(){return statement;}
    public String getDescribtion(){return describtion;}
    public String getTag(){return tag;}
    public String getTime(){return time;}
    public String getBy(){return by;}
    //public DataSnapshot getReplies(){return replies;}
}
