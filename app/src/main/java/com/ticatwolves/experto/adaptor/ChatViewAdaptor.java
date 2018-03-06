package com.ticatwolves.experto.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.dataobjects.AddChats;
import com.ticatwolves.experto.dataobjects.AddReplies;

import java.util.List;

/**
 * Created by ticat on 4/2/18.
 */

public class ChatViewAdaptor extends RecyclerView.Adapter<ChatViewAdaptor.MyOwnHolder> {

    Context ctx;
    List<AddChats> data;
    String id;

    public ChatViewAdaptor(Context ctx, List<AddChats> data,String userid){
        this.ctx = ctx;
        this.data = data;
        this.id = userid;
    }

    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myinflat = LayoutInflater.from(ctx);
        View myOwnView = myinflat.inflate(R.layout.chats_view,parent,false);
        return new MyOwnHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(final MyOwnHolder holder, final int position) {
        final AddChats p = data.get(position);
        //if(p.getId().equals(id)){
        //    holder.msg2.setText(p.getMsg());
        //    holder.time.setText(p.getTime());
        //}
        //else {
            holder.name.setText(p.getBy());
            holder.msg.setText(p.getMsg());
            holder.time2.setText(p.getTime());
        //}
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {
        public TextView msg,msg2,time,time2,name;
        public MyOwnHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            msg = (TextView) itemView.findViewById(R.id.rec_msg);
            msg2 = (TextView) itemView.findViewById(R.id.send_msg);
            time = (TextView) itemView.findViewById(R.id.time);
            time2 = (TextView) itemView.findViewById(R.id.time2);
        }
    }
}