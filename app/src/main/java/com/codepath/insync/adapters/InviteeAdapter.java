package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.insync.R;
import java.util.ArrayList;

/**
 * Created by Gauri Gadkari on 4/10/17.
 */

public class InviteeAdapter extends RecyclerView.Adapter<InviteeAdapter.InviteeViewHolder>  {
    ArrayList<String> invitees;
    Context context;
    @Override
    public InviteeAdapter.InviteeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.chip_layout, parent, false);

        return new InviteeAdapter.InviteeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InviteeAdapter.InviteeViewHolder holder, final int position) {
        String guest = invitees.get(position);
        holder.guestName.setText(guest);

        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitees.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return invitees.size();
    }

    public InviteeAdapter(Context context, ArrayList<String> invitees){
        this.invitees = invitees;
        this.context = context;
    }

    public static class InviteeViewHolder extends RecyclerView.ViewHolder {
        TextView guestName;
        ImageView btnClose;
        public InviteeViewHolder(View itemView) {
            super(itemView);
            //binding = UpcomingEventItemBinding.bind(itemView);
            guestName = (TextView) itemView.findViewById(R.id.guestName);
            btnClose = (ImageView) itemView.findViewById(R.id.closeButton);
        }
    }
}
