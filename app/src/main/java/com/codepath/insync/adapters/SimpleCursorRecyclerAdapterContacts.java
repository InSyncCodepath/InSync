package com.codepath.insync.adapters;

/**
 * Created by Gauri Gadkari on 4/9/17.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.insync.R;

import java.util.ArrayList;

public class SimpleCursorRecyclerAdapterContacts extends CursorRecyclerAdapter<SimpleViewHolder> {
    SimpleCursorAdapterInterface listener;
    private int mLayout;
    private int[] mFrom;
    private int[] mTo;
    private String[] mOriginalFrom;
    private Context context;
    boolean isSelectedContact [];
    ArrayList<String> invitees;
    public SimpleCursorRecyclerAdapterContacts(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(c);
        //this.listener = listener;
        this.context = context;
        mLayout = layout;
        mTo = to;
        mOriginalFrom = from;
        findColumns(c, from);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false);
        return new SimpleViewHolder(v, mTo);
    }

    @Override
    public void onBindViewHolder (final SimpleViewHolder holder, final Cursor cursor, final int position) {
        if(isSelectedContact == null && cursor.getCount() > 0){
            isSelectedContact = new boolean[cursor.getCount()];
        }
        if(invitees == null) {
            invitees = new ArrayList<>();
        }
        final int count = mTo.length;
        final int[] from = mFrom;

        holder.isSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    isSelectedContact[position] = true;
                    cursor.moveToPosition(position);
                    if(!(invitees.contains(cursor.getString(cursor.getColumnIndex("display_name"))))){
                        invitees.add(cursor.getString(cursor.getColumnIndex("display_name")));
                    }
                } else {
                    isSelectedContact[position] = false;
                    cursor.moveToPosition(position);
                    invitees.remove(cursor.getString(cursor.getColumnIndex("display_name")));
                }
            }
        });
        Log.d("Debug", invitees.toString());
//        for (int i = 0; i < count; i++) {
//            holder.views[i].setText(cursor.getString(from[i]));
//        }
        holder.contactName.setText(cursor.getString(from[0]));
        Glide.with(context).load(cursor.getString(from[1])).into(holder.imageView);
        holder.contactNumber.setText(cursor.getString(from[2]));
        if(isSelectedContact[position]) {
            holder.isSelected.setChecked(true);
        } else {
            holder.isSelected.setChecked(false);
        }
    }
    public ArrayList<String> showInvitees(){
        return invitees;
    }
    public void setAdapterListener(SimpleCursorAdapterInterface listener) {
        this.listener = listener;
    }

    private void findColumns(Cursor c, String[] from) {
        if (c != null) {
            int i;
            int count = from.length;
            if (mFrom == null || mFrom.length != count) {
                mFrom = new int[count];
            }
            for (i = 0; i < count; i++) {
                mFrom[i] = c.getColumnIndexOrThrow(from[i]);
            }
        } else {
            mFrom = null;
        }
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        findColumns(c, mOriginalFrom);
        return super.swapCursor(c);
    }

    public interface SimpleCursorAdapterInterface {
        public void showInvitees();
    }
}

class SimpleViewHolder extends RecyclerView.ViewHolder
{
    public TextView contactName, contactNumber;
    public ImageView imageView;
    public CheckBox isSelected;

    public SimpleViewHolder (View itemView, int[] to)
    {
        super(itemView);
//        views = new TextView[to.length];
//        for(int i = 0 ; i < to.length ; i++) {
//        }
        contactName = (TextView) itemView.findViewById(to[0]);
        imageView = (ImageView) itemView.findViewById(to[1]);
        contactNumber = (TextView) itemView.findViewById(to[2]);
        isSelected = (CheckBox) itemView.findViewById(R.id.isSelected);
    }

}