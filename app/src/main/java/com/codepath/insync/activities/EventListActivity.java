package com.codepath.insync.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.insync.Manifest;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventListBinding;
import com.codepath.insync.fragments.PastEventsFragment;
import com.codepath.insync.fragments.UpcomingEventsFragment;
import com.codepath.insync.listeners.OnEventClickListener;
import com.codepath.insync.utils.LocationService;

import static com.codepath.insync.R.id.ivEventImage;


public class EventListActivity extends AppCompatActivity implements OnEventClickListener {

    private ActivityEventListBinding binding;
    private FragmentPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    final int REQUEST_CODE = 1001;
    public UpcomingEventsFragment upcomingFragment;
    public PastEventsFragment pastFragment;
    private static final int LOCATION_ACCESS_PERMISSION = 20;

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
                Intent startEventCreationIntent = EventCreationActivityNoAnim.newIntent(EventListActivity.this);
                EventListActivity.this.startActivityForResult(startEventCreationIntent, REQUEST_CODE);
            }
        });
        startLocationService();

    }

    private void startLocationService() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_ACCESS_PERMISSION
            );
        } else {
            Intent lsIntent = new Intent(this, LocationService.class);
            // Start the service
            startService(lsIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_ACCESS_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent lsIntent = new Intent(this, LocationService.class);
                    // Start the service
                    startService(lsIntent);
                }
                break;
            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            upcomingFragment.reloadList();
            Toast.makeText(this, "New Event Added", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onItemClick(String eventId, boolean isCurrent, boolean canTrack, ImageView sharedImageView) {
        Bundle animationBundle =
                ActivityOptions.makeCustomAnimation(this, R.anim.slide_from_left, R.anim.slide_to_left).toBundle();
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, sharedImageView, eventId);
//        Pair<View, String> p1 = Pair.create((View) findViewById(R.id.ivEventImage), "profile");

        Intent eventDetailIntent = new Intent(EventListActivity.this, EventDetailActivity.class);
        eventDetailIntent.putExtra("eventId", eventId);
        eventDetailIntent.putExtra("isCurrent", isCurrent);
        eventDetailIntent.putExtra("canTrack", canTrack);
        eventDetailIntent.putExtra("transition_name", eventId);
        //startActivity(eventDetailIntent);
        startActivity(eventDetailIntent, options.toBundle());

        //startActivity(eventDetailIntent, animationBundle);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;
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
