package com.ticatwolves.experto.adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.dataobjects.AddExperts;
import com.ticatwolves.experto.dataobjects.AddReplies;
import com.ticatwolves.experto.expert.ChatActivity;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by ticat on 4/2/18.
 */

public class ChatsAdaptor extends RecyclerView.Adapter<ChatsAdaptor.MyOwnHolder> {

    Context ctx;
    List<AddExperts> data;
    List<String> ids;
    String uid;

    public ChatsAdaptor(Context ctx, List<AddExperts> data, List<String> ids,String uid){
        this.ctx = ctx;
        this.data = data;
        this.ids = ids;
        this.uid = uid;
    }

    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myinflat = LayoutInflater.from(ctx);
        View myOwnView = myinflat.inflate(R.layout.chats_list_view,parent,false);
        return new MyOwnHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(final MyOwnHolder holder, final int position) {
        final AddExperts p = data.get(position);
        holder.name.setText(p.getName());
        holder.time.setText("");
        try{
            Glide.with(ctx).load(p.getPhotourl()).into(holder.image);
        }catch (Exception e){

        }
        holder.re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctx.startActivity(new Intent(ctx, ChatActivity.class).putExtra("name",p.getName()).putExtra("id",ids.get(position)).putExtra("uid",uid));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {
        public TextView name,time;
        ImageView image;
        public RelativeLayout re;
        public MyOwnHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            time = (TextView) itemView.findViewById(R.id.time);
            image = (ImageView) itemView.findViewById(R.id.image);
            re = (RelativeLayout) itemView.findViewById(R.id.re);
        }
    }
}