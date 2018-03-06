package com.ticatwolves.experto.dataobjects;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by ticat on 31/1/18.
 */

public class AddExperts {
    public String name,email,photourl;
    public DataSnapshot fields;

    public AddExperts(String name,String email){
        this.name = name;
        this.email = email;
    }

    public AddExperts(String name, String email, DataSnapshot fields){
        this.name = name;
        this.email = email;
        this.fields = fields;
    }

    public AddExperts(){}

    public String getName(){return name;}
    public String getEmail(){return email;}
    public DataSnapshot getFields(){return fields;}
    public String getPhotourl(){return photourl;}

}
