package com.codepath.insync.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.codepath.insync.R;
import com.codepath.insync.models.Tag;
import com.plumillonforge.android.chipview.ChipViewAdapter;

/**
 * Created by Gauri Gadkari on 4/12/17.
 */

public class MainChipViewAdapter extends ChipViewAdapter {
    public MainChipViewAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutRes(int position) {
        Tag tag = (Tag) getChip(position);
        return R.layout.chip_close;
    }

    @Override
    public int getBackgroundColor(int position) {
        Tag tag = (Tag) getChip(position);
        return getColor(R.color.com_facebook_blue);
    }

    @Override
    public int getBackgroundColorSelected(int position) {
        return 0;
    }

    @Override
    public int getBackgroundRes(int position) {
        return 0;
    }

    @Override
    public void onLayout(View view, int position) {
        Tag tag = (Tag) getChip(position);
        ((TextView) view.findViewById(android.R.id.text1)).setTextColor(getColor(R.color.com_facebook_blue));
    }
}