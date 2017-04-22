package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.insync.R;
import com.codepath.insync.models.Contact;

import java.util.ArrayList;

/**
 * Created by Gauri Gadkari on 4/10/17.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>  {
    ArrayList<Contact> contacts;
//    ArrayList<Contact> contacts;
    Context context;
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.contact_item, parent, false);

        return new ContactAdapter.ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, final int position) {
        Contact guest = contacts.get(position);
        holder.guestName.setText(guest.getName());

        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contacts.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public ContactAdapter(Context context, ArrayList<Contact> contacts){
        this.contacts = contacts;
        this.context = context;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView guestName;
        ImageView btnClose;
        public ContactViewHolder(View itemView) {
            super(itemView);
            //binding = UpcomingEventItemBinding.bind(itemView);
            guestName = (TextView) itemView.findViewById(R.id.guestName);
            btnClose = (ImageView) itemView.findViewById(R.id.closeButton);
        }
    }
}
