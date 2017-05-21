package com.codepath.insync.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventListBinding;
import com.codepath.insync.fragments.EventsFragment;
import com.codepath.insync.listeners.OnEventClickListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.eftimoff.viewpagertransformers.DefaultTransformer;

import java.util.Date;

import static com.codepath.insync.utils.CommonUtil.createSnackbar;


public class EventListActivity extends AppCompatActivity implements OnEventClickListener {

    private ActivityEventListBinding binding;
    private ViewPager viewPager;
    final int REQUEST_CODE = 1001;
    final int EVENT_DETAIL_RQ = 1002;
    public EventsFragment upcomingFragment;
    public EventsFragment pastFragment;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_list);

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        FragmentPagerAdapter viewPagerAdapter = new EventPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = binding.viewpager;
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(true, new DefaultTransformer());
        TabLayout tabLayout = binding.slidingTabs;
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventCreationIntent = EventCreationActivity.newIntent(EventListActivity.this);
                EventListActivity.this.startActivityForResult(eventCreationIntent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EVENT_DETAIL_RQ) {
            if (!data.getBooleanExtra("hasEnded", false)) {
                return;
            }
            viewPager.setCurrentItem(1);

            Event event = new Event();
            event.setObjectId(data.getStringExtra("eventId"));
            event.setName(data.getStringExtra("eventName"));
            event.setAddress(data.getStringExtra("eventAddress"));
            event.setStartDate(new Date(data.getLongExtra("eventDate", -1)));
            event.setEndDate(new Date(data.getLongExtra("eventDate", -1)));

            event.put("imageUrl", data.getStringExtra("eventImage"));
            upcomingFragment.removeEvent(event);
            pastFragment.addEvent(event);
            createSnackbar(
                    binding.mainContent,
                    this,
                    "Your event \""+data.getStringExtra("eventName")+"\" has successfully ended.");
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

        if (id == R.id.action_logout) {
            User.logOut();
            User.setCurrentUser(null);

            Bundle animationBundle =
                    ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
            Intent login = new Intent(EventListActivity.this, LoginActivity.class);
            startActivity(login, animationBundle);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(String eventId, boolean isCurrent, boolean canTrack, ImageView sharedImageView) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, sharedImageView, eventId);

        if (isCurrent) {
            Intent eventDetailIntent = new Intent(EventListActivity.this, EventDetailChatActivity.class);
            eventDetailIntent.putExtra("eventId", eventId);
            eventDetailIntent.putExtra("canTrack", canTrack);
            eventDetailIntent.putExtra("transition_name", eventId);
            startActivityForResult(eventDetailIntent, EVENT_DETAIL_RQ, options.toBundle());
        } else {
            Intent eventDetailIntent = new Intent(EventListActivity.this, EventDetailPastActivity.class);
            eventDetailIntent.putExtra("eventId", eventId);
            eventDetailIntent.putExtra("transition_name", eventId);
            startActivity(eventDetailIntent, options.toBundle());
        }
    }


    private class EventPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;
        EventPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    upcomingFragment = EventsFragment.newInstance(true);
                    return upcomingFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    pastFragment = EventsFragment.newInstance(false);
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
                    return "Upcoming";
                case 1:
                    return "Past";
            }
            return null;
        }
    }
}
