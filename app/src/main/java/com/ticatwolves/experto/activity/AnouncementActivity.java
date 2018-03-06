package com.ticatwolves.experto.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ticatwolves.experto.R;
import com.ticatwolves.experto.database.AnnouncementDatabase;

import java.util.List;

public class AnouncementActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anouncement);
        setTitle("Announcement");

        AnnouncementDatabase announcementDatabase = new AnnouncementDatabase(this);

        listView = (ListView) findViewById(R.id.list);

        List<String> data;
        data = announcementDatabase.getdata();

        //Toast.makeText(this,data.toString(),Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, data);

        listView.setAdapter(adapter);
    }
}
