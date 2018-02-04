package com.ticatwolves.experto.guest;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.adaptor.RepliesAdaptor;
import com.ticatwolves.experto.dataobjects.AddReplies;
import com.ticatwolves.experto.expert.QuerySolutionActivity;
import com.ticatwolves.experto.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuestQueryActivity extends AppCompatActivity {

    TextView by,statement,description,url;
    RecyclerView replies;
    List<AddReplies> addRepliesList = new ArrayList<>();
    RepliesAdaptor repliesAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_query);

        Intent i = getIntent();

        by = (TextView) findViewById(R.id.by);
        statement = (TextView)findViewById(R.id.statement);
        description = (TextView)findViewById(R.id.description);
        replies = (RecyclerView) findViewById(R.id.replies);
        replies.setLayoutManager(new LinearLayoutManager(this));

        String bby = i.getExtras().getString("by");
        String sstatement = i.getExtras().getString("statement");
        String ddescription = i.getExtras().getString("description");
        Log.e("url",i.getExtras().getString("url"));

        by.setText("Asked By " +bby);
        statement.setText("Statement :- "+sstatement);
        description.setText("Description :- "+ddescription);

        String str = i.getExtras().getString("url");
        final String[] path = str.split("\\s+");

        FirebaseDatabase.getInstance().getReference("Queries").child(path[0]).child(path[1]).child(path[2]).child("replies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addRepliesList.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    AddReplies addReplies = d.getValue(AddReplies.class);
                    addRepliesList.add(addReplies);
                }
                repliesAdaptor = new RepliesAdaptor(GuestQueryActivity.this,addRepliesList);
                replies.setAdapter(repliesAdaptor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
