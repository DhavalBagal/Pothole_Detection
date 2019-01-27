package com.android.citygroom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AfterloginSection extends AppCompatActivity {

    Fragment issuesFragment, complaintsFragment, streetrideFragment, settingsFragment, newsFragment;
    NewsSection newsSection;
    DatabaseReference newsref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogin_section);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_email");

        final TextView section_name = (TextView) findViewById(R.id.section_name);
        section_name.setText("ISSUES");

        issuesFragment = new IssuesSection();
        complaintsFragment = new ComplaintsSection();
        streetrideFragment = new StreetrideSection();
        settingsFragment = new SettingsSection();
        newsFragment = new NewsSection();

        loadFragment(issuesFragment);

        TabLayout tab = findViewById(R.id.tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition())
                {
                    case 0 : section_name.setText("ISSUES"); loadFragment(issuesFragment); break;
                    case 1 : section_name.setText("YOUR COMPLAINTS"); loadFragment(complaintsFragment); break;
                    case 2 : section_name.setText("STREET RIDE"); loadFragment(streetrideFragment); break;
                    case 3 : section_name.setText("SETTINGS"); loadFragment(settingsFragment); break;
                    case 4 : section_name.setText("NEWS "); loadFragment(newsFragment); break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition())
                {
                    case 0: section_name.setText(null); removeFragment(issuesFragment); break;
                    case 1: section_name.setText(null); removeFragment(complaintsFragment); break;
                    case 2: section_name.setText(null); removeFragment(streetrideFragment); break;
                    case 3: section_name.setText(null); removeFragment(settingsFragment); break;
                    case 4: section_name.setText(null); removeFragment(newsFragment); break;

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        SharedPreferences sharedpreferences = getSharedPreferences(SigninSection.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }

    private boolean loadFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        return true;
    }

    private boolean removeFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        return true;
    }
}
