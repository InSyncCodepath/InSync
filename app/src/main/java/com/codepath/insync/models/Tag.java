package com.codepath.insync.models;

import com.plumillonforge.android.chipview.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gauri Gadkari on 4/12/17.
 */

public class Tag implements Chip {
    private String mName;
    private int mType = 0;

    public Tag(String name, int type) {
        this(name);
        mType = type;
    }

    public Tag(String name) {
        mName = name;
    }

    @Override
    public String getText() {
        return mName;
    }

    public int getType() {
        return mType;
    }

}
