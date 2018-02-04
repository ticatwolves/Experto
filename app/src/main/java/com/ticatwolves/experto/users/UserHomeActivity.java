package com.ticatwolves.experto.users;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.dataobjects.AddProblem;
import com.ticatwolves.experto.expert.QuerySolutionActivity;
import com.ticatwolves.experto.guest.GuestActivity;
import com.ticatwolves.experto.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView name,email;
    RecyclerView queries_view;
    DatabaseReference databaseReference;
    final List<AddProblem> data = new ArrayList<>();
    final List<String> replies = new ArrayList<>();
    final List<String> url = new ArrayList<>();
    QueriesAdaptor queriesAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        setTitle("Experto");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomeActivity.this,UserAskQueryActivity.class));
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navview =  navigationView.getHeaderView(0);

        email = (TextView) navview.findViewById(R.id.email);
        name = (TextView) navview.findViewById(R.id.name);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        String na = user.get(SessionManager.KEY_NAME);

        name.setText(na);

        navigationView.setNavigationItemSelectedListener(this);

        queries_view = (RecyclerView) findViewById(R.id.query_recycler_view);
        queries_view.setLayoutManager(new LinearLayoutManager(this));

        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Queries").child(id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                replies.clear();
                data.clear();
                url.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot d : postSnapshot.getChildren()){
                        Log.e("data",d.toString());
                        AddProblem problems = d.getValue(AddProblem.class);
                        url.add(id+" "+postSnapshot.getKey()+" "+d.getKey()+"");

                        try{
                            Log.e("Total Replies = ",""+d.child("replies").getChildrenCount());
                            replies.add(""+d.child("replies").getChildrenCount());
                        }
                        catch (Exception e){
                            Log.e("Total Replies = ","0");
                            replies.add("0");
                        }
                        data.add(problems);
                    }
                }
                queriesAdaptor = new QueriesAdaptor(getApplicationContext(),data,replies,url);
                queries_view.setAdapter(queriesAdaptor);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logoutUser();
            finish();
        }
        else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_admin) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class QueriesAdaptor extends RecyclerView.Adapter<QueriesAdaptor.MyOwnHolder> {

        Context ctx;
        List<AddProblem> data;
        List<String> repliesCount;
        List<String> u;

        public QueriesAdaptor(Context ctx,List<AddProblem> data,List<String> repliescount,List<String> u){
            this.ctx = ctx;
            this.data = data;
            this.repliesCount = repliescount;
            this.u = u;
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
            holder.re.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ctx.startActivity(new Intent(ctx,QuerySolutionActivity.class).putExtra("by",p.getBy()).putExtra("statement",p.getStatement()).putExtra("description",p.getDescribtion()).putExtra("url",u.get(position)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    Toast.makeText(ctx,"I\'ll handle rest", Toast.LENGTH_SHORT).show();
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
}
