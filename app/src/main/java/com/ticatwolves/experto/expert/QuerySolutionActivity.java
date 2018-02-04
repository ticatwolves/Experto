package com.ticatwolves.experto.expert;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.activity.ChooseModeActivity;
import com.ticatwolves.experto.adaptor.RepliesAdaptor;
import com.ticatwolves.experto.dataobjects.AddReplies;
import com.ticatwolves.experto.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuerySolutionActivity extends AppCompatActivity {

    TextView by,statement,description;
    FloatingActionButton ans;
    RecyclerView replies;
    List<AddReplies> addRepliesList = new ArrayList<>();
    RepliesAdaptor repliesAdaptor;

    SessionManager sessionManager;

    HashMap<String, String> user;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_solution);
        Intent i = getIntent();

        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        type = user.get(SessionManager.KEY_TYPE);

        by = (TextView) findViewById(R.id.by);
        statement = (TextView)findViewById(R.id.statement);
        description = (TextView)findViewById(R.id.description);
        replies = (RecyclerView) findViewById(R.id.replies);
        ans = (FloatingActionButton)findViewById(R.id.ans);
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
        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        final String name = user.get(SessionManager.KEY_NAME);

        FirebaseDatabase.getInstance().getReference("Queries").child(path[0]).child(path[1]).child(path[2]).child("replies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addRepliesList.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    AddReplies addReplies = d.getValue(AddReplies.class);
                    addRepliesList.add(addReplies);
                }
                repliesAdaptor = new RepliesAdaptor(QuerySolutionActivity.this,addRepliesList);
                replies.setAdapter(repliesAdaptor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        FirebaseDatabase.getInstance().getReference("Queries").child(path[0]).child(path[1]).child(path[2]).child("replies").setValue("adbcx");
        ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(QuerySolutionActivity.this);
                dialog.setContentView(R.layout.expert_replyfield_dailog);
                dialog.setTitle("Answer");
                dialog.show();

                Button declineButton = (Button) dialog.findViewById(R.id.close_action);
                Button confirmButton = (Button) dialog.findViewById(R.id.confirm_action);
                final EditText field_input = (EditText) dialog.findViewById(R.id.expert_answer_field);
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String f = field_input.getText().toString();
                        AddReplies addReplies = new AddReplies(f,name);
                        FirebaseDatabase.getInstance().getReference("Queries").child(path[0]).child(path[1]).child(path[2]).child("replies").push().setValue(addReplies);
//                        Toast.makeText(getApplicationContext(),"try later, some insternal error",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if(type.equals("expert")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.expert_query_menu, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refer:
                Toast.makeText(this,"Ill share it",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
