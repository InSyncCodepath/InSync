package com.codepath.insync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    // Pass in the message array into the constructor
    public EDGuestAdapter(Context context, List<User> edImages) {
        mEDImages = edImages;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public EDGuestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom image view layout
        View edImageView = inflater.inflate(R.layout.item_edguest, parent, false);

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

        holder.tvEDGuestName.setText(user.getName());

        holder.ivEDGuestImage.setImageResource(R.drawable.ic_profile);


        if (imageUrl != null) {
            Glide.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_profile)
                    .bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0))
                    .into(holder.ivEDGuestImage);
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
