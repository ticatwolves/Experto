package com.ticatwolves.experto.dataobjects;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ticat on 31/1/18.
 */

public class AddChats {
    String time,msg,by,id,photourl;//,name;
    public AddChats(String by,String msg,String id,String photourl){//},String name){
        this.by = by;
        this.msg = msg;
        this.time = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        this.id=id;
        this.photourl = photourl;
//        this.name = name;
    }

    public AddChats(){}

    public String getTime(){return time;}
    public String getMsg(){return msg;}
    public String getBy(){return by;}
    public String getId(){return id;}
    public String getPhotourl(){return photourl;}
    //public String getName(){return name;}
}
