package com.codepath.insync.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codepath.insync.R;
import com.codepath.insync.adapters.SimpleCursorRecyclerAdapter;
import com.codepath.insync.databinding.ActivityContactBinding;

public class ContactActivity extends AppCompatActivity {
    SimpleCursorRecyclerAdapter adapter;
    ActivityContactBinding binding;
    RecyclerView contactList;
    public static final int CONTACT_LOADER_ID = 10;

    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projectionFields = new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                    ContactsContract.Contacts.LOOKUP_KEY,};
//                    ContactsContract.Contacts._ID,
//                    ContactsContract.Contacts.DISPLAY_NAME,
//                    ContactsContract.Contacts.PHOTO_URI, ContactsContract.CommonDataKinds.Phone.NUMBER };
            String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '1'";
            // Construct the loader
            CursorLoader cursorLoader = new CursorLoader(ContactActivity.this,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, // URI
                    projectionFields, selection, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            );
            // Return the loader for use
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            adapter.swapCursor(cursor);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact);
        contactList = binding.contactList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contactList.setLayoutManager(layoutManager);
        setupCursorAdapter();
        contactList.setAdapter(adapter);
        getSupportLoaderManager().initLoader(CONTACT_LOADER_ID,
                new Bundle(), contactsLoader);
    }

    public static Intent newIntent(Activity callingActivity) {
        Intent intent = new Intent(callingActivity, ContactActivity.class);
        return intent;
    }

    private void setupCursorAdapter() {
        String[] uiBindFrom = { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI, ContactsContract.CommonDataKinds.Phone.NUMBER };
        int[] uiBindTo = { R.id.contactName, R.id.contactImage, R.id.contactNumber };

        adapter = new SimpleCursorRecyclerAdapter(this, R.layout.contact_item, null, uiBindFrom, uiBindTo);
    }

}
