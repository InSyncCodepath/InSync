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

import static com.codepath.insync.R.id.isSelected;

public class SimpleCursorRecyclerAdapter extends CursorRecyclerAdapter<SimpleViewHolder> {

    private int mLayout;
    private int[] mFrom;
    private int[] mTo;
    private String[] mOriginalFrom;
    private Context context;
    boolean isSelectedContact [];
    public SimpleCursorRecyclerAdapter (Context context, int layout, Cursor c, String[] from, int[] to) {
        super(c);
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
    public void onBindViewHolder (final SimpleViewHolder holder, Cursor cursor, final int position) {
        if(isSelectedContact == null && cursor.getCount() > 0){
            isSelectedContact = new boolean[cursor.getCount()];
        }
        final int count = mTo.length;
        final int[] from = mFrom;
        holder.isSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    isSelectedContact[position] = true;
                } else {
                    isSelectedContact[position] = false;
                }
            }
        });
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

    /**
     * Create a map from an array of strings to an array of column-id integers in cursor c.
     * If c is null, the array will be discarded.
     *
     * @param c the cursor to find the columns from
     * @param from the Strings naming the columns of interest
     */
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