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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.dataobjects.AddProblem;
import com.ticatwolves.experto.dataobjects.AddReplies;
import com.ticatwolves.experto.expert.QuerySolutionActivity;

import java.util.List;

/**
 * Created by ticat on 4/2/18.
 */

public class RepliesAdaptor extends RecyclerView.Adapter<RepliesAdaptor.MyOwnHolder> {

    Context ctx;
    List<AddReplies> data;

    public RepliesAdaptor(Context ctx, List<AddReplies> data){
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myinflat = LayoutInflater.from(ctx);
        View myOwnView = myinflat.inflate(R.layout.query_replies_view,parent,false);
        return new MyOwnHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(final MyOwnHolder holder, final int position) {
        final AddReplies p = data.get(position);
        holder.rep.setText(p.getReply());
        holder.by.setText(p.getBy());
        holder.on.setText(p.getOn());
        try {
            Glide.with(ctx).load(p.getPhoto()).into(holder.photo);
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {
        public TextView rep,on,by;
        public ImageView photo;
        public MyOwnHolder(View itemView) {
            super(itemView);
            rep = (TextView) itemView.findViewById(R.id.rep);
            on = (TextView) itemView.findViewById(R.id.time);
            by = (TextView) itemView.findViewById(R.id.by);
            photo = (ImageView) itemView.findViewById(R.id.pimage);
        }
    }
}