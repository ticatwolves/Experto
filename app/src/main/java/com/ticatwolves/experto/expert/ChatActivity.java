package com.ticatwolves.experto.expert;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.activity.ChooseModeActivity;
import com.ticatwolves.experto.activity.VideoActivity;
import com.ticatwolves.experto.adaptor.ChatViewAdaptor;
import com.ticatwolves.experto.dataobjects.AddChats;
import com.ticatwolves.experto.session.SessionManager;

import org.appspot.apprtc.ConnectActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChatActivity extends AppCompatActivity {

    EditText msginput;
    ImageView btn_send;
    RecyclerView chats;
    ChatViewAdaptor chatViewAdaptor;
    String uid,id,name;

    //EmojiconEditText emojiconEditText;
    //EmojiconTextView textView;
    //ImageView emojiImageView;
    //ImageView submitButton;
    //View rootView;
    //EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final Intent i = getIntent();

        name = i.getStringExtra("name");
        setTitle(name);

        msginput = (EditText) findViewById(R.id.msg);
        btn_send = (ImageView) findViewById(R.id.send);
        chats = (RecyclerView) findViewById(R.id.chats);
        chats.setLayoutManager(new LinearLayoutManager(this));

        uid = i.getExtras().getString("uid");
        id = i.getExtras().getString("id");

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.KEY_NAME);
        final String userid = user.get(SessionManager.KEY_UID);

        final List<AddChats> data = new ArrayList<>();

        @SuppressLint("WrongConstant") SharedPreferences sh1 = getSharedPreferences("experto",MODE_APPEND);
        final String photo = sh1.getString("photo","");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msginput.getText().toString();
                if (!(TextUtils.isEmpty(msg))){
                    AddChats a = new AddChats(name,msg,userid,photo);
                    FirebaseDatabase.getInstance().getReference("Chats").child(uid).child(id).child("chat").push().setValue(a);
                    msginput.setText("");
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Chats").child(uid).child(id).child("chat").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AddChats chat = d.getValue(AddChats.class);
                    data.add(chat);
                }
                chatViewAdaptor = new ChatViewAdaptor(getApplication(),data,userid);
                chats.setAdapter(chatViewAdaptor);
                chats.smoothScrollToPosition(data.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*FirebaseDatabase.getInstance().getReference("Chats").child(uid).child(id).child("chat").orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vc:
                startActivity(new Intent(ChatActivity.this, ConnectActivity.class));
                //Toast.makeText(this,"video call",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
