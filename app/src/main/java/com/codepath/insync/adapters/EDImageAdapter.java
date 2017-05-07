package com.codepath.insync.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.insync.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.R.attr.resource;
import static com.codepath.insync.R.id.map;
import static com.facebook.FacebookSdk.getApplicationContext;


public class EDImageAdapter extends RecyclerView.Adapter<EDImageAdapter.ViewHolder> {


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
        public @BindView(R.id.ivEDImage) ImageView ivEDImage;
        public @BindView(R.id.ivEDImagePL) ImageView ivEDImagePL;
        public @BindView(R.id.tvShare) TextView tvShare;

        public ViewHolder(final View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                ButterKnife.bind(this, itemView);
                if (width > 0) {
                    ivEDImage.getLayoutParams().width = width;
                    ivEDImage.getLayoutParams().height = width;
                }


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

            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(tvShare, position);
                        }
                    }
                }
            });


        }
    }

    // Store a member variable for the messages
    private List<String> mEDImages;
    // Store the context for easy access
    private Context mContext;
    private int resource;
    private int width;

    // Pass in the message array into the constructor
    public EDImageAdapter(Context context, List<String> edImages, int layout_resource, int window_width) {
        mEDImages = edImages;
        mContext = context;
        resource = layout_resource;
        if (window_width > 0) {
            width = (window_width - (8*4))/3;
        } else {
            width = 0;
        }

    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public EDImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom image view layout
        View edImageView = inflater.inflate(resource, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(edImageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the data model based on position
        String imageUrl = mEDImages.get(position);
        holder.ivEDImage.setVisibility(View.INVISIBLE);
        holder.ivEDImagePL.setVisibility(View.VISIBLE);
        holder.ivEDImage.setImageResource(R.drawable.ic_camera_alt_white_48px);
        if (imageUrl != null) {
            Glide.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_camera_alt_white_48px)
                    .crossFade()
                    .bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 4, 0))
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.ivEDImagePL.setVisibility(View.GONE);
                            holder.ivEDImage.setVisibility(View.VISIBLE);
                            holder.ivEDImage.setImageDrawable(resource);
                        }
                    });
        }


    }

    @Override
    public int getItemCount() {
        return mEDImages.size();
    }
    
    public String getItem(int position) {
        return getItem(position);
    }

    public void clear() {
        mEDImages.clear();
        notifyDataSetChanged();

    }

    public void addAll(List<String> newEDImages) {
        int position = mEDImages.size();
        for (int i=0; i < newEDImages.size(); i++) {
            mEDImages.add(newEDImages.get(i));
            notifyItemInserted(position);
            position++;
        }
    }

}
