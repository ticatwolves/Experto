package com.ticatwolves.experto.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ticat on 6/16/2017.
 */

public class AnnouncementDatabase extends SQLiteOpenHelper {

    static final private String DB_Name = "Experto";
    static final private String DB_Table = "announcement";
    static final private int DB_Version = 1;
    Context ctx;
    SQLiteDatabase mydb;

    public AnnouncementDatabase(Context ct) {
        super(ct, DB_Name, null, DB_Version);
        ctx=ct;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+DB_Table+" (id integer primary key autoincrement,task)");
        Log.i("Database","Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+DB_Table);
        onCreate(sqLiteDatabase);
    }

    public void insertdata(String s1){
        mydb = getWritableDatabase();
        mydb.execSQL("insert into "+DB_Table+" (task) values('"+s1+"')");
    }


    public List<String> getdata(){
        mydb=getReadableDatabase();
        Cursor cr = mydb.rawQuery("Select * from "+DB_Table,null);
        List<String> task = new ArrayList<>();
        while (cr.moveToNext()){
            String s1 = cr.getString(1);
            task.add(s1);
        }
        return task;
    }

    public int getCount(){
        try{
            mydb=getReadableDatabase();
            Cursor cr = mydb.rawQuery("Select * from "+DB_Table,null);
            return cr.getCount();
        }
        catch (Exception e){
            return 0;
        }
    }
}
