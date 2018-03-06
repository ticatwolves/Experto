package com.ticatwolves.experto.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.admin.AdminHomeActivity;
import com.ticatwolves.experto.expert.ExpertHomeActivity;
import com.ticatwolves.experto.session.SessionManager;
import com.ticatwolves.experto.users.UserHomeActivity;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText inputId, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin;//, btnReset;
    SessionManager session;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());


        if (auth.getCurrentUser() != null) {
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();
            String type = user.get(SessionManager.KEY_TYPE);
            if(type.equals("admin")){
                startActivity(new Intent(LoginActivity.this,AdminHomeActivity.class));
                finish();
            }
            else if (type.equals("expert")){
                startActivity(new Intent(LoginActivity.this, ExpertHomeActivity.class).putExtra("id",user.get(SessionManager.KEY_UID)));
                finish();
            }
            else {
                startActivity(new Intent(LoginActivity.this, UserHomeActivity.class).putExtra("id",user.get(SessionManager.KEY_UID)));
                finish();
            }
        }

        setContentView(R.layout.activity_login);


        inputId = (EditText) findViewById(R.id.id);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        //spinnerChooseLogin = (Spinner) findViewById(R.id.login_menu);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        /*btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });*/

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = inputId.getText().toString();
                final String password = inputPassword.getText().toString();

                if (id.length() != 10 || !(id.matches("[0-9]+"))) {
                    Toast.makeText(getApplicationContext(), "ID Should be of length 10 and numerical only", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Enter password! greater then length 6", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users").child(id).child("email");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            login(snapshot.getValue().toString(),password,id);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Sorry you Can't Register",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void login(String email, final String password,final String id){
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            FirebaseDatabase.getInstance().getReference("Users").child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        createSession(snapshot.getValue().toString(),null,id,"user");
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"Sorry",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            finish();
                        }
                    }
                });
    }

    public void createSession(String name, String email, String userid, String type){
        session.createLoginSession(name, email, userid,type);
        startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
    }
}
