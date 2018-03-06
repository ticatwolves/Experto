package com.ticatwolves.experto.guest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.adaptor.QueryAdaptor;
import com.ticatwolves.experto.dataobjects.AddExperts;
import com.ticatwolves.experto.dataobjects.AddProblem;
import com.ticatwolves.experto.dataobjects.AddReplies;
import com.ticatwolves.experto.fragment.AdminUserFragment;

import java.util.ArrayList;
import java.util.List;

public class GuestActivity extends AppCompatActivity {

    RecyclerView guest_recycler_view;
    QueryAdaptor queriesAdaptor;

    DatabaseReference databaseReference;
    final List<AddProblem> data = new ArrayList<>();
    final List<String> replies = new ArrayList<>();
    final List<String> url = new ArrayList<>();
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        setTitle("Experto Guest Account");

        guest_recycler_view = (RecyclerView)findViewById(R.id.guest_recycler_view);
        guest_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        pd = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Queries");
        pd.setMessage("please wait...");
        pd.setCancelable(false);
        pd.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                replies.clear();
                data.clear();
                url.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot d : postSnapshot.getChildren()){
                        for (DataSnapshot p : d.getChildren()){
                            AddProblem problems = p.getValue(AddProblem.class);
                            try{
                                url.add(""+postSnapshot.getKey()+" "+d.getKey()+" "+p.getKey());
                                Log.e("Total Replies = ",""+p.child("replies").getChildrenCount());
                                replies.add(""+p.child("replies").getChildrenCount());
                            }
                            catch (Exception e){
                                Log.e("Total Replies = ","0");
                                url.add(null);
                                replies.add("0");
                            }
                            data.add(problems);
                        }
                    }
                }
                pd.dismiss();
                queriesAdaptor = new QueryAdaptor(getApplicationContext(),data,replies,url);
                guest_recycler_view.setAdapter(queriesAdaptor);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /*class QueriesAdaptor extends RecyclerView.Adapter<QueriesAdaptor.MyOwnHolder> {

        Context ctx;
        List<AddProblem> data;
        List<String> repliesCount;
        List<String> url;

        public QueriesAdaptor(Context ctx,List<AddProblem> data,List<String> repliescount, List<String> url){
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
        public void onBindViewHolder(MyOwnHolder holder, final int position) {
            final AddProblem p = data.get(position);
            holder.askedBy.setText("Asked By "+p.getBy());
            holder.postedOn.setText("posted On " +p.getTime());
            holder.tag.setText("Tag "+p.getTag());
            holder.problemStatement.setText("Statement "+p.getStatement());
            holder.totalReplies.setText("TotalReplies "+repliesCount.get(position));
            Glide.with(ctx).load(p.getPhoto()).into(holder.photo);
            holder.re.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ctx,GuestQueryActivity.class).putExtra("by",p.getBy()).putExtra("statement",p.getStatement()).putExtra("description",p.getDescribtion()).putExtra("url",url.get(position)));
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
            ImageView photo;
            public MyOwnHolder(View itemView) {
                super(itemView);
                re = (RelativeLayout)itemView.findViewById(R.id.re);
                askedBy = (TextView)itemView.findViewById(R.id.asked_by);
                problemStatement = (TextView)itemView.findViewById(R.id.problem_statement);
                tag = (TextView) itemView.findViewById(R.id.tag);
                totalReplies = (TextView)itemView.findViewById(R.id.total_replies);
                postedOn = (TextView)itemView.findViewById(R.id.posted_on);
                photo = (ImageView) itemView.findViewById(R.id.pimage);
            }
        }
    }*/
}
