package com.ticatwolves.experto.admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.activity.RegistrationActivity;
import com.ticatwolves.experto.dataobjects.AddExperts;

import java.util.ArrayList;
import java.util.List;

public class AdminAddExpertActivity extends AppCompatActivity {

    Button add_expert;
    EditText email,password,expertid,name;
    Spinner field;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_expert);
        auth = FirebaseAuth.getInstance();
        setTitle("Add Expert");

        add_expert = (Button)findViewById(R.id.add_expert_btn);
        email = (EditText)findViewById(R.id.expert_email_input);
        password = (EditText)findViewById(R.id.expert_password_input);
        expertid = (EditText)findViewById(R.id.expertid_input);
        field = (Spinner) findViewById(R.id.add_fields);
        name = (EditText) findViewById(R.id.expert_name_input);

        add_expert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Add Expert",Toast.LENGTH_SHORT).show();
                String id = expertid.getText().toString().trim();
                String emai = email.getText().toString().trim();
                String role = field.getSelectedItem().toString();
                String pass = password.getText().toString();
                String nam = name.getText().toString();

                if(addUser(id,emai,role,nam)){
                    Toast.makeText(getApplicationContext(),"user added",Toast.LENGTH_SHORT).show();
                    registerUser(emai,pass);
                }
                else {
                    Toast.makeText(getApplicationContext(),"try later, some insternal error",Toast.LENGTH_SHORT).show();
                }
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Tags").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayAdapter<String> adapter;
                    final List<String> list;
                    list = new ArrayList<String>();
                    Log.e("Bilol i gothtis",dataSnapshot.toString());
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        Log.e("mofo",d.getValue().toString());
                        list.add(d.getValue().toString());
                    }
                    adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    field.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }//onCancelled
        });
    }

    public Boolean addUser(String id,String email,String role,String name){
        try {
            Log.e("adding user",role);
            Log.e("adding user",email);
            AddExperts addExperts = new AddExperts(name,email);
            //FirebaseDatabase.getInstance().getReference("Experts").child(role).child(id).setValue(email);
            FirebaseDatabase.getInstance().getReference("ExpertsRegistered").child(id).setValue(addExperts);
            FirebaseDatabase.getInstance().getReference("ExpertsRegistered").child(id).child("tags").child(role).setValue(true);
            return true;
        }
        catch (Exception e){
            Log.e("error",e.toString());
            return false;
        }
    }

    public Boolean registerUser(final String email, String pass){

        //create user
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(AdminAddExpertActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(AdminAddExpertActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()) {
                            Log.e("Error : ",task.getException().toString());
                            Toast.makeText(AdminAddExpertActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminAddExpertActivity.this, "Expert added Successfully" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Error", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                            //startActivity(new Intent(AdminAddExpertActivity.this, AdminHomeActivity.class));
                            finish();
                        }
                    }
                });

        this.email.setText("");
        this.expertid.setText("");
        this.password.setText("");

        return false;
    }
}
