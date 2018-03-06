package com.ticatwolves.experto.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
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
import com.ticatwolves.experto.dataobjects.AddExperts;
import com.ticatwolves.experto.dataobjects.AddUser;
import com.ticatwolves.experto.users.UserHomeActivity;
import com.ticatwolves.experto.validator.EmailValidator;

import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {

    private EditText inputId, inputPassword, inputName, inputFName, inputPhoneNo, inputEmail, inputDoB;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private RadioButton inputMale, inputFemale;
    private int mYear, mMonth, mDay, mHour, mMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle(R.string.register_user);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);

        inputId = (EditText) findViewById(R.id.id);
        inputName = (EditText) findViewById(R.id.name);
        inputFName = (EditText) findViewById(R.id.fname);
        inputPhoneNo = (EditText) findViewById(R.id.phoneno);
        inputPassword = (EditText) findViewById(R.id.password);
        inputEmail = (EditText) findViewById(R.id.email);
        inputDoB = (EditText) findViewById(R.id.dob);

        inputDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datebtn();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        inputMale = (RadioButton) findViewById(R.id.radioMale);
        inputFemale = (RadioButton) findViewById(R.id.radioFemale);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ADD FILTERS HERE
                String id = inputId.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                registration(id, password);
            }
        });

        inputFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inputMale.setChecked(false);
                } else {
                    inputMale.setChecked(true);
                }
            }
        });
        inputMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inputFemale.setChecked(false);
                } else {
                    inputFemale.setChecked(true);
                }
            }
        });
    }

    public void registration(final String id, final String password) {
        if (id.length() != 10 || !(id.matches("[0-9]+"))) {
            Toast.makeText(getApplicationContext(), "ID Should be of length 10 and numerical only", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Enter password! greater then length 6", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userregistration(inputEmail.getText().toString().trim(), password, id);
                    //Toast.makeText(getApplicationContext(), "Can reg", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, You can't register", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void userregistration(final String email, final String password, final String id) {
        //create user
        EmailValidator emailValidator = new EmailValidator();
        if (! emailValidator.validate(email)){
            Toast.makeText(this,"Please enter correct email",Toast.LENGTH_SHORT).show();
            return;
        }
        final String name = inputName.getText().toString();
        final String fname = inputFName.getText().toString();
        final String phone = inputPhoneNo.getText().toString();
        final String dob = inputDoB.getText().toString();

        if ((TextUtils.isEmpty(name) || TextUtils.isEmpty(fname) || TextUtils.isEmpty(dob))) {
            Toast.makeText(this,"please fill all details correctly",Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 10 || !(id.matches("[0-9]+"))) {
            Toast.makeText(getApplicationContext(), "phone should be of length 10 and numerical only", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(RegistrationActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Log.e("Error : ", task.getException().toString());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Error", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            addData(id,name,email,password,fname,dob,phone);
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        }
                    }
                });
    }

    public void addData(String id,String name, String email, String password,String fname,String dob, String phone) {
        String gender;
        if (inputMale.isChecked()) {
            gender = "Male";
        } else gender = "Female";
        //long phonno = (long) (Integer.valueOf(phone));
        String nam = name.substring(0, 1).toUpperCase() + name.substring(1);
        String fna = fname.substring(0, 1).toUpperCase() + fname.substring(1);
        AddUser addUser = new AddUser(nam, fna, email, gender, phone,dob,"");
        FirebaseDatabase.getInstance().getReference("Users").child(id).setValue(addUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void datebtn() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int day = dayOfMonth;
                        int month = monthOfYear;
                        int yearr = year;
                        inputDoB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}