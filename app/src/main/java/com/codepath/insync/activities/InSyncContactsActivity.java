package com.codepath.insync.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityInSyncContactsBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InSyncContactsActivity extends AppCompatActivity {
    ActivityInSyncContactsBinding binding;
    MultiAutoCompleteTextView searchContacts;
    ParseQuery<ParseUser> query = ParseUser.getQuery();
    ArrayList<String> users = new ArrayList<>();
    ArrayList<String> invitees = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_in_sync_contacts);
        searchContacts = binding.searchContact;
        Toolbar toolbar = binding.toolbarInSyncContacts;
        setSupportActionBar(toolbar);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        users.add(objects.get(i).getUsername());
                    }
                }
            }
        });
        Log.d("USERS",users.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,users);
        searchContacts.setAdapter(adapter);
        searchContacts.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Log.d("Debug", searchContacts.getListSelection()+"");
        searchContacts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            addGuests();
            return true;
        }
//        if (id == R.id.action_search) {
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void addGuests() {
        String contacts = searchContacts.getText().toString();
        List<String> list = new ArrayList<String>(Arrays.asList(contacts.split(", ")));
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", (Serializable) list);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        return;
    }

    public static Intent newIntent(Activity callingActivity) {
        Intent intent = new Intent(callingActivity, InSyncContactsActivity.class);
        return intent;
    }
}
