package com.ticatwolves.experto.admin;

import android.annotation.SuppressLint;
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
import com.ticatwolves.experto.R;
import com.ticatwolves.experto.activity.ChooseModeActivity;
import com.ticatwolves.experto.fragment.AdminExpertFragment;
import com.ticatwolves.experto.fragment.AdminHomeFragment;
import com.ticatwolves.experto.fragment.AdminUserFragment;
import com.ticatwolves.experto.session.SessionManager;

public class AdminHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TabLayout adminTabLayout;
    ViewPager adminViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_admin_home);
        setTitle(R.string.admin_activity_name);

        adminTabLayout = (TabLayout)findViewById(R.id.admin_tabs);
        adminViewPager = (ViewPager)findViewById(R.id.admin_viewpager);

        adminViewPager.setAdapter(new Pager(getSupportFragmentManager()));
        adminTabLayout.setupWithViewPager(adminViewPager);

        adminTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adminViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
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
        inflater.inflate(R.menu.admin_main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.admin_logout:
                mAuth.signOut();
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
        String tabs[] = {"Menu","Experts","Users"};

        public Pager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return new AdminHomeFragment();
            }
            if(position==1){
                return new AdminExpertFragment();
            }
            if (position==2){
                return new AdminUserFragment();
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
