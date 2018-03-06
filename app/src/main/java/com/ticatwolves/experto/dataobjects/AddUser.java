package com.ticatwolves.experto.dataobjects;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by ticat on 31/1/18.
 */

public class AddUser {
    public String name,email,fname,gender,phone,dob,photourl;
    public AddUser(String name,String fname,String email,String gender,String phone,String dob,String photo){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.fname = fname;
        this.gender = gender;
        this.dob = dob;
        this.photourl = photo;
    }

    public AddUser(){}

    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getFname(){return fname;}
    public String getGender(){return gender;}
    public String getPhone(){return phone;}
    public String getDob(){return dob;}
    public String getPhotourl(){return photourl;}
}
