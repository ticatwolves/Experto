package com.ticatwolves.experto.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.admin.AdminHomeActivity;
import com.ticatwolves.experto.admin.AdminLoginActivity;
import com.ticatwolves.experto.expert.ExpertHomeActivity;
import com.ticatwolves.experto.guest.GuestActivity;
import com.ticatwolves.experto.session.SessionManager;
import com.ticatwolves.experto.users.UserHomeActivity;

import java.util.HashMap;

public class ChooseModeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_guest,btn_admin,btn_user;
    SessionManager session;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        session = new SessionManager(getApplicationContext());

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();
            String type = user.get(SessionManager.KEY_TYPE);
            if(type.equals("admin")){
                startActivity(new Intent(ChooseModeActivity.this,AdminHomeActivity.class));
                finish();
            }
            else if (type.equals("expert")){
                startActivity(new Intent(ChooseModeActivity.this, ExpertHomeActivity.class).putExtra("id",user.get(SessionManager.KEY_UID)));
                finish();
            }
            else {
                startActivity(new Intent(ChooseModeActivity.this, UserHomeActivity.class).putExtra("id",user.get(SessionManager.KEY_UID)));
                finish();
            }
        }

        btn_admin = (Button) findViewById(R.id.btn_admin);
        btn_user = (Button) findViewById(R.id.btn_user);
        btn_guest = (Button) findViewById(R.id.btn_guest);
        btn_admin.setOnClickListener(this);
        btn_user.setOnClickListener(this);
        btn_guest.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_admin:
                startActivity(new Intent(ChooseModeActivity.this, AdminLoginActivity.class));
                break;
            case R.id.btn_user:
                startActivity(new Intent(ChooseModeActivity.this, LoginActivity.class));
                break;
            case R.id.btn_guest:
                startActivity(new Intent(ChooseModeActivity.this, GuestActivity.class));
                break;
        }
    }
}
