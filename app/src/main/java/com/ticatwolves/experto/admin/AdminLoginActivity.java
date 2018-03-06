package com.ticatwolves.experto.admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.ticatwolves.experto.activity.LoginActivity;
import com.ticatwolves.experto.activity.RegistrationActivity;
import com.ticatwolves.experto.expert.ExpertHomeActivity;
import com.ticatwolves.experto.session.SessionManager;
import com.ticatwolves.experto.users.UserHomeActivity;

import java.util.HashMap;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnLogin,btnBack;//, btnReset;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getApplicationContext());

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();
            String type = user.get(SessionManager.KEY_TYPE);
            if(type.equals("admin")){
                startActivity(new Intent(AdminLoginActivity.this,AdminHomeActivity.class));
                finish();
            }
            else if (type.equals("expert")){
                startActivity(new Intent(AdminLoginActivity.this, ExpertHomeActivity.class).putExtra("id",user.get(SessionManager.KEY_UID)));
                finish();
            }
            else {
                startActivity(new Intent(AdminLoginActivity.this, UserHomeActivity.class).putExtra("id",user.get(SessionManager.KEY_UID)));
                finish();
            }
        }

        setContentView(R.layout.activity_admin_login);

        inputEmail = (EditText) findViewById(R.id.id);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.admin_login_button);
        btnBack = (Button) findViewById(R.id.back_button);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter id!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                FirebaseDatabase.getInstance().getReference("admin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String adminemail = (String) snapshot.getValue();
                        if(email.equals(adminemail)){
                            signinAdmin(email,password);
                            Toast.makeText(getApplicationContext(),"Hello Admin",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            fetchemailExpert(email,password);
                            Toast.makeText(getApplicationContext(),"Hello Expert",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Connect to internet",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void signinAdmin(String email, final String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(AdminLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(AdminLoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            startActivity(new Intent(AdminLoginActivity.this, AdminHomeActivity.class));
                            createSession("","","","admin");
                            finish();
                        }
                    }
                });
    }

    public void fetchemailExpert(final String id, final String password){
        FirebaseDatabase.getInstance().getReference("ExpertsRegistered").child(id).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String email = (String) snapshot.getValue();
                Log.e("emailid", email);
                siginExpert(email,password,id);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Connect to internet",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void siginExpert(String email, final String password, final String id){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(AdminLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(AdminLoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            FirebaseDatabase.getInstance().getReference("ExpertsRegistered").child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Log.v("done",snapshot.getValue().toString());
                                        createSession(snapshot.getValue().toString(),null,id,"expert");
                                        startActivity(new Intent(AdminLoginActivity.this, ExpertHomeActivity.class).putExtra("id",id));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"Sorry",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
    }

    public void createSession(String name, String email, String userid, String type){
        session.createLoginSession(name, email, userid,type);
        Log.v("done","didit");
    }

}
