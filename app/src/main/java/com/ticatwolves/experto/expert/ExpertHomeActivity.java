package com.ticatwolves.experto.expert;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.activity.AnouncementActivity;
import com.ticatwolves.experto.activity.ChooseModeActivity;
import com.ticatwolves.experto.admin.AdminHomeActivity;
import com.ticatwolves.experto.fragment.AdminExpertFragment;
import com.ticatwolves.experto.fragment.AdminHomeFragment;
import com.ticatwolves.experto.fragment.AdminUserFragment;
import com.ticatwolves.experto.fragment.ExpertChatFragment;
import com.ticatwolves.experto.fragment.ExpertForAllFragment;
import com.ticatwolves.experto.fragment.ExpertForMeFragment;
import com.ticatwolves.experto.session.SessionManager;
import com.ticatwolves.experto.users.SettingsActivity;
import com.ticatwolves.experto.users.UserHomeActivity;

public class ExpertHomeActivity extends AppCompatActivity {

    TabLayout expertTabLayout;
    ViewPager expertViewPager;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_home);
        setTitle(R.string.expert_activity_name);
        auth = FirebaseAuth.getInstance();

        expertTabLayout = (TabLayout)findViewById(R.id.expert_tabs);
        expertViewPager = (ViewPager)findViewById(R.id.expert_viewpager);

        expertViewPager.setAdapter(new Pager(getSupportFragmentManager()));
        expertTabLayout.setupWithViewPager(expertViewPager);

        expertTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                expertViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Toast.makeText(this,getIntent().getStringExtra("id"),Toast.LENGTH_LONG).show();

        try {
            FirebaseDatabase.getInstance().getReference().child("ExpertsRegistered").child(getIntent().getStringExtra("id")).child("photourl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        SharedPreferences sh = getSharedPreferences("experto", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sh.edit();
                        editor.putString("photo", dataSnapshot.getValue().toString());
                        editor.commit();
                       // Toast.makeText(ExpertHomeActivity.this,dataSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
                    }

                    //Glide.with(ExpertHomeActivity.this).load(dataSnapshot.getValue().toString()).into(photo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            //Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }


    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(268435456);
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);

        //super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expert_main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.expert_logout:
                auth.signOut();
                SessionManager sessionManager = new SessionManager(this);
                sessionManager.logoutUser();
                SharedPreferences sh = getSharedPreferences("experto", MODE_PRIVATE);
                SharedPreferences.Editor editor = sh.edit();
                editor.putString("photo", "");
                editor.commit();
                startActivity(new Intent(this, ChooseModeActivity.class));
                finish();
                break;
            case R.id.setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.ann:
                startActivity(new Intent(this, AnouncementActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    class Pager extends FragmentPagerAdapter {
        String tabs[] = {"All Problems","My Problems","Chats"};

        public Pager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return new ExpertForAllFragment();
            }
            if(position==1){
                return new ExpertForMeFragment();
            }
            if (position==2){
                return new ExpertChatFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabs.length;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

}
