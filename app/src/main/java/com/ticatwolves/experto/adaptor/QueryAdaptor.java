package com.ticatwolves.experto.adaptor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ticatwolves.experto.R;
import com.ticatwolves.experto.dataobjects.AddProblem;
import com.ticatwolves.experto.expert.QuerySolutionActivity;
import com.ticatwolves.experto.guest.GuestActivity;
import com.ticatwolves.experto.guest.GuestQueryActivity;

import java.util.List;

/**
 * Created by ticat on 4/2/18.
 */

public class QueryAdaptor extends RecyclerView.Adapter<QueryAdaptor.MyOwnHolder> {

    Context ctx;
    List<AddProblem> data;
    List<String> repliesCount;
    List<String> url;

    public QueryAdaptor(Context ctx,List<AddProblem> data,List<String> repliescount,List<String> url){
        this.ctx = ctx;
        this.data = data;
        this.repliesCount = repliescount;
        this.url = url;
    }

    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myinflat = LayoutInflater.from(ctx);
        View myOwnView = myinflat.inflate(R.layout.guest_query_view,parent,false);
        return new MyOwnHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(final MyOwnHolder holder, final int position) {
        final AddProblem p = data.get(position);
        holder.askedBy.setText("Asked By "+p.getBy());
        holder.postedOn.setText("posted On " +p.getTime());
        holder.tag.setText("Tag "+p.getTag());
        holder.problemStatement.setText("Statement "+p.getStatement());
        holder.totalReplies.setText("TotalReplies "+repliesCount.get(position));
        holder.re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctx.startActivity(new Intent(ctx,QuerySolutionActivity.class).putExtra("by",p.getBy()).putExtra("statement",p.getStatement()).putExtra("description",p.getDescribtion()).putExtra("url",url.get(position)));
                Toast.makeText(ctx,"I\'ll handle rest",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {
        public RelativeLayout re;
        public TextView askedBy,problemStatement,tag,totalReplies,postedOn;
        public MyOwnHolder(View itemView) {
            super(itemView);
            re = (RelativeLayout)itemView.findViewById(R.id.re);
            askedBy = (TextView)itemView.findViewById(R.id.asked_by);
            problemStatement = (TextView)itemView.findViewById(R.id.problem_statement);
            tag = (TextView) itemView.findViewById(R.id.tag);
            totalReplies = (TextView)itemView.findViewById(R.id.total_replies);
            postedOn = (TextView)itemView.findViewById(R.id.posted_on);
        }
    }
}