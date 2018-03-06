package com.ticatwolves.experto.users;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.activity.ChooseModeActivity;
import com.ticatwolves.experto.expert.ExpertHomeActivity;
import com.ticatwolves.experto.session.SessionManager;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnChangeEmail, btnChangePassword, btnChangeProfile;
    FirebaseUser user;
    String id,type;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    private StorageReference mStorageRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://experto-ad599.appspot.com/");
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> usr = sessionManager.getUserDetails();
        id = usr.get(SessionManager.KEY_UID);
        type = usr.get(SessionManager.KEY_TYPE);


        //id = getIntent().getExtras().getString("id");
        //Toast.makeText(this,id,Toast.LENGTH_SHORT).show();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        pd = new ProgressDialog(this);
        pd.setCancelable(false);


        btnChangeEmail = (Button) findViewById(R.id.change_email_button);
        btnChangePassword = (Button) findViewById(R.id.change_password_button);
        btnChangeProfile = (Button) findViewById(R.id.change_profile_button);

        btnChangeEmail.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        btnChangeProfile.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_email_button:
                changeEmail();
                break;
            case R.id.change_password_button:
                changePassword();
                break;
            case R.id.change_profile_button:
                changeProfile();
                break;
            default:
                break;
        }
    }
    public void changeEmail(){
        final EditText newEmail = new EditText(this);
        newEmail.setHint("Enter new email id");

        new AlertDialog.Builder(this)
                .setTitle("Change Email")
                .setMessage("Enter new email")
                .setView(newEmail)
                .setPositiveButton("change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String e = newEmail.getText().toString().trim();
                        if (!e.equals("")) {
                            user.updateEmail(newEmail.getText().toString().trim())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "TAG User email address updated.", Toast.LENGTH_SHORT).show();
                                                if (type.equals("expert")){
                                                    FirebaseDatabase.getInstance().getReference("ExpertsRegistered").child(id).child("email").setValue(e);
                                                }
                                                else {
                                                    FirebaseDatabase.getInstance().getReference("Users").child(id).child("email").setValue(e);
                                                }
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(SettingsActivity.this,ChooseModeActivity.class));
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(SettingsActivity.this,"Please try later",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public void changePassword(){
        final EditText newPassword = new EditText(this);
        newPassword.setHint("Enter new Password");

        new AlertDialog.Builder(this)
                .setTitle("Change password")
                .setMessage("Enter new password")
                .setView(newPassword)
                .setPositiveButton("change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String e = newPassword.getText().toString().trim();
                        if (!(e.equals("")) && e.length()>6) {
                            user.updatePassword(newPassword.getText().toString().trim())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "TAG User Password updated.", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(SettingsActivity.this,ChooseModeActivity.class));
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(SettingsActivity.this,"Please try later",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public void changeProfile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Toast.makeText(this,"pkp",Toast.LENGTH_SHORT).show();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String userid = user.getUid();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            pd.show();
            Long tslong = System.currentTimeMillis()/1000;
            String ts = tslong.toString();
            StorageReference childRef = storageRef.child(userid + "-" + ts + ".png");
            //uploading the image
            final UploadTask uploadTask = childRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Uri url = taskSnapshot.getDownloadUrl();
                    if (type.equals("expert")){
                        FirebaseDatabase.getInstance().getReference("ExpertsRegistered").child(id).child("photourl").setValue(url.toString());
                    }
                    else {
                        FirebaseDatabase.getInstance().getReference("Users").child(id).child("photourl").setValue(url.toString());
                    }
                    //getImageName(userid,url.toString());
                    Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    //adddatabase(userid,url.toString(),"Name Here");
                    Toast.makeText(SettingsActivity.this,url.toString(),Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    pd.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });
        }
    }


}
