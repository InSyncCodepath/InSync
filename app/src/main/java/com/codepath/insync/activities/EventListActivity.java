package com.codepath.insync.activities;

import android.databinding.DataBindingUtil;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventListBinding;
import com.codepath.insync.fragments.PastEventsFragment;
import com.codepath.insync.fragments.UpcomingEventsFragment;

import static com.codepath.insync.R.id.fab;

public class EventListActivity extends AppCompatActivity {

    private ActivityEventListBinding binding;
    private FragmentPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_list);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        viewPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = binding.viewpager;
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = binding.slidingTabs;
        tabLayout.setupWithViewPager(viewPager);


        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
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

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;
        public UpcomingEventsFragment upcomingFragment;
        public PastEventsFragment pastFragment;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    upcomingFragment = UpcomingEventsFragment.newInstance(1);
                    return upcomingFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    pastFragment = PastEventsFragment.newInstance(2);
                    return pastFragment;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Upcoming Events";
                case 1:
                    return "Past Events";
            }
            return null;
        }
    }
}
