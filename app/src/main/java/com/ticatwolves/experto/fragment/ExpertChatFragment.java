package com.ticatwolves.experto.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.adaptor.ChatsAdaptor;
import com.ticatwolves.experto.dataobjects.AddExperts;
import com.ticatwolves.experto.dataobjects.AddUser;
import com.ticatwolves.experto.expert.ChatActivity;
import com.ticatwolves.experto.session.SessionManager;
import com.ticatwolves.experto.users.UserChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ExpertChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpertChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpertChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpertChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpertChatFragment newInstance(String param1, String param2) {
        ExpertChatFragment fragment = new ExpertChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expert_chat, container, false);
    }

    RecyclerView re;
    List<AddUser> data = new ArrayList<>();
    List<String> uid = new ArrayList<>();
    MyChatAdaptor myChatAdaptor;
    String id;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        re = (RecyclerView)getActivity().findViewById(R.id.recy);
        re.setLayoutManager(new LinearLayoutManager(getActivity()));

        SessionManager sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        id = user.get(SessionManager.KEY_UID);

        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uid.clear();
                data.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    uid.add(d.getKey());
                    data.add(d.getValue(AddUser.class));
                }
                myChatAdaptor = new MyChatAdaptor(getActivity(),data,uid,id);
                re.setAdapter(myChatAdaptor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    class MyChatAdaptor extends RecyclerView.Adapter<MyChatAdaptor.Holder>{

        Context ctx;
        List<AddUser> data;
        List<String> uid;
        String id;
        public MyChatAdaptor(Context ctx,List<AddUser> data,List<String> uid,String id){
            this.ctx = ctx;
            this.data = data;
            this.id = id;
            this.uid = uid;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View view = inflater.inflate(R.layout.chats_list_view,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(MyChatAdaptor.Holder holder, final int position) {
            final AddUser p = data.get(position);
            holder.name.setText(p.getName());
            holder.time.setText("");
            try{
                Glide.with(ctx).load(p.getPhotourl()).into(holder.image);
            }catch (Exception e){

            }
            holder.re.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ctx.startActivity(new Intent(ctx, ChatActivity.class).putExtra("name",p.getName()).putExtra("id",id).putExtra("uid",uid.get(position)));
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            public TextView name,time;
            ImageView image;
            public RelativeLayout re;
            public Holder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                time = (TextView) itemView.findViewById(R.id.time);
                image = (ImageView) itemView.findViewById(R.id.image);
                re = (RelativeLayout) itemView.findViewById(R.id.re);
            }
        }
    }
}
