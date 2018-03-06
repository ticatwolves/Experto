package com.ticatwolves.experto.users;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.adaptor.ChatsAdaptor;
import com.ticatwolves.experto.dataobjects.AddExperts;
import com.ticatwolves.experto.fragment.AdminExpertFragment;
import com.ticatwolves.experto.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserChatActivity extends AppCompatActivity {

    public RecyclerView chatView;
    public ChatsAdaptor chatsAdaptor;
    public DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        setTitle("Chats");
        chatView = (RecyclerView) findViewById(R.id.chats_recycler_view);
        chatView.setLayoutManager(new LinearLayoutManager(this));

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        final String uid = user.get(SessionManager.KEY_UID);
        databaseReference = FirebaseDatabase.getInstance().getReference("ExpertsRegistered");

        final List<AddExperts> data = new ArrayList<>();
        final List<String> ids = new ArrayList<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ids.add(postSnapshot.getKey());
                    AddExperts experts = postSnapshot.getValue(AddExperts.class);
                    data.add(experts);
                }
                chatsAdaptor = new ChatsAdaptor(UserChatActivity.this,data,ids,uid);
                chatView.setAdapter(chatsAdaptor);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
