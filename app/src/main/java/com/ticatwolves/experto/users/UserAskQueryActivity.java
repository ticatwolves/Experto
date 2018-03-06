package com.ticatwolves.experto.users;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.dataobjects.AddProblem;
import com.ticatwolves.experto.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.content.Context.MODE_APPEND;


public class UserAskQueryActivity extends AppCompatActivity {
    public TextView problem_statement_input, problem_describtion_input;
    public Button problem_submit_button;
    public Spinner tag_spinner, experts_spinner;
    final List<String> id = new ArrayList<>();
    public CheckBox all_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ask_query);
        problem_statement_input = (EditText)findViewById(R.id.problem_statement_input);
        problem_describtion_input = (EditText)findViewById(R.id.problem_describtion_input);
        problem_submit_button = (Button) findViewById(R.id.problem_submit_btn);
        tag_spinner = (Spinner) findViewById(R.id.tags_spinner);
        experts_spinner = (Spinner) findViewById(R.id.expert_spinner);
        all_checkbox = (CheckBox) findViewById(R.id.all);

        //final Map<String, List<String>> map = new HashMap<String, List<String>>();
        //final List<String> data = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Tags").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayAdapter<String> adapter;
                    final List<String> list;
                    list = new ArrayList<String>();
    //                Log.e("Bilol i gothtis",dataSnapshot.toString());
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
  //                      Log.e("mofo",d.getValue().toString());
                        list.add(d.getValue().toString());
                    }
                    adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tag_spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }//onCancelled
        });

        tag_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("bilol id ",tag_spinner.getSelectedItem().toString());
                findExperts(tag_spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        problem_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_problem();
            }
        });
    }

    public void submit_problem(){
        String problem_statement = problem_statement_input.getText().toString();
        String problem_describtion = problem_describtion_input.getText().toString();

        problem_statement = problem_statement.substring(0, 1).toUpperCase() + problem_statement.substring(1);
        problem_describtion = problem_describtion.substring(0, 1).toUpperCase() + problem_describtion.substring(1);

        /*if(problem_statement.toLowerCase().contains(filter.toLowerCase())){
            Log.e("xc","bv");
        }
        else {
            Log.e("asd","asd");
        }*/

        String tag = tag_spinner.getSelectedItem().toString();
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        String name = user.get(SessionManager.KEY_NAME);

        if (TextUtils.isEmpty(problem_statement) || TextUtils.isEmpty(problem_describtion)) {
            Toast.makeText(getApplicationContext(), "Enter Statement and Description", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("WrongConstant") SharedPreferences sh1 = getSharedPreferences("experto",MODE_APPEND);
        String photo = sh1.getString("photo","");

        AddProblem addProblem = new AddProblem(problem_statement,problem_describtion,tag,name,photo);

        try {
            if(all_checkbox.isChecked()){
                FirebaseDatabase.getInstance().getReference("Queries").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("all").push().setValue(addProblem);
            }
            else {
                String expertid = id.get(experts_spinner.getSelectedItemPosition());
                FirebaseDatabase.getInstance().getReference("Queries").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(expertid).push().setValue(addProblem);
            }
        }catch (Exception ex){
            FirebaseDatabase.getInstance().getReference("Queries").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("all").push().setValue(addProblem);
        }



        finish();
    }

    public void findExperts(final String field){
        FirebaseDatabase.getInstance().getReference("ExpertsRegistered").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayAdapter<String> adapter;
                final List<String> list;//,id;
                list = new ArrayList<String>();
                //id = new ArrayList<String>();
                list.clear();
                id.clear();
                for(DataSnapshot d : snapshot.getChildren()) {
                    try {
                        //Log.e("child",d.toString());
                        //Log.e("hey check it",d.child("tags").child(field).getValue().toString());
                        if((Boolean) d.child("tags").child(field).getValue()) {
                            list.add(d.child("name").getValue().toString());
                            id.add(d.getKey().toString());
                        }
                    }
                    catch (Exception e){
                        Log.e("bilol", "No match");
                    }
                }
                adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                experts_spinner.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Connect to internet",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
