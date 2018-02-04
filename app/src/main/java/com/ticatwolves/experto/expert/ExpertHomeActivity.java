package com.ticatwolves.experto.expert;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.activity.ChooseModeActivity;
import com.ticatwolves.experto.admin.AdminHomeActivity;
import com.ticatwolves.experto.fragment.AdminExpertFragment;
import com.ticatwolves.experto.fragment.AdminHomeFragment;
import com.ticatwolves.experto.fragment.AdminUserFragment;
import com.ticatwolves.experto.fragment.ExpertChatFragment;
import com.ticatwolves.experto.fragment.ExpertForAllFragment;
import com.ticatwolves.experto.fragment.ExpertForMeFragment;
import com.ticatwolves.experto.session.SessionManager;

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
                startActivity(new Intent(this, ChooseModeActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
