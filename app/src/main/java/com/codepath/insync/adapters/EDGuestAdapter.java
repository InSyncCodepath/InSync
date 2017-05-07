package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.insync.R;
import com.codepath.insync.models.parse.User;
import com.parse.ParseFile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.R.attr.resource;


public class EDGuestAdapter extends RecyclerView.Adapter<EDGuestAdapter.ViewHolder> {


    // Define listener member variable
    private OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public @BindView(R.id.ivEDGuestImage) CircleImageView ivEDGuestImage;
        public @BindView(R.id.ivEDGuestImagePL) ImageView ivEDGuestImagePL;
        public @BindView(R.id.tvEDGuestName) TextView tvEDGuestName;
        public @BindView(R.id.ivEDRSVPImage) ImageView ivEDRSVPImage;

        public ViewHolder(final View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    // Triggers click upwards to the adapter on click
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(itemView, position);
                            }
                        }
                    }
                });
        }
    }

    // Store a member variable for the messages
    private List<User> mEDImages;
    // Store the context for easy access
    private Context mContext;
    private int mLayoutResource;
    private boolean isCurrent;

    // Pass in the message array into the constructor
    public EDGuestAdapter(Context context, List<User> edImages, int layoutResource, boolean isPresent) {
        mEDImages = edImages;
        mContext = context;
        mLayoutResource = layoutResource;
        isCurrent = isPresent;

    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public EDGuestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom image view layout
        View edImageView = inflater.inflate(mLayoutResource, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(edImageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the data model based on position
        User user = mEDImages.get(position);
        ParseFile imgFile = user.getProfileImage();
        String imageUrl = null;
        if (imgFile != null) {
            imageUrl = imgFile.getUrl();
        }

        if (isCurrent) {
            holder.tvEDGuestName.setText(user.getName());
        } else {
            holder.tvEDGuestName.setText(user.getName().split(" ")[0]);
        }

        holder.ivEDGuestImage.setVisibility(View.VISIBLE);
        holder.ivEDGuestImage.setImageResource(R.mipmap.ic_profile_placeholder);

        if (imageUrl != null) {
            holder.ivEDGuestImagePL.setVisibility(View.VISIBLE);
            holder.ivEDGuestImage.setVisibility(View.INVISIBLE);

            Glide.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_profile_placeholder)
                    .bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0))
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.ivEDGuestImage.setImageDrawable(resource);
                            holder.ivEDGuestImagePL.setVisibility(View.INVISIBLE);
                            holder.ivEDGuestImage.setVisibility(View.VISIBLE);
                        }
                    });
        }

        if (user.getInt("rsvpStatus") == 0) {
            holder.ivEDRSVPImage.setImageResource(R.mipmap.ic_rsvp_attending);
        } else if (user.getInt("rsvpStatus") == 1) {
            holder.ivEDRSVPImage.setImageResource(R.mipmap.ic_rsvp_decline);
        } else {
            holder.ivEDRSVPImage.setImageResource(R.mipmap.ic_rsvp_waiting);
        }


    }

    @Override
    public int getItemCount() {
        return mEDImages.size();
    }
    
    public ParseFile getItem(int position) {
        return getItem(position);
    }

    public void clear() {
        mEDImages.clear();
        notifyDataSetChanged();

    }

    public void addAll(List<User> newEDImages) {
        int position = mEDImages.size();
        for (int i=0; i < newEDImages.size(); i++) {
            mEDImages.add(newEDImages.get(i));
            notifyItemInserted(position);
            position++;
        }
    }

}
