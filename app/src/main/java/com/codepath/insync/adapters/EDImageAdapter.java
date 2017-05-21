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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
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
        @BindView(R.id.ivEDImage) ImageView ivEDImage;
        @BindView(R.id.ivEDImagePL) ImageView ivEDImagePL;
        @BindView(R.id.tvShare) TextView tvShare;

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

    @Override
    public EDImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom image view layout
        View edImageView = inflater.inflate(resource, parent, false);

        // Return a new holder instance
        return new ViewHolder(edImageView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the data model based on position
        String imageUrl = mEDImages.get(position);
        holder.ivEDImage.setVisibility(View.INVISIBLE);
        holder.ivEDImagePL.setVisibility(View.VISIBLE);
        holder.ivEDImage.setImageResource(R.drawable.ic_camera_alt_white_48px);
        int corner_radius = resource == R.layout.item_edimage? 4 : 0;
        if (imageUrl != null) {
            Glide.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_camera_alt_white_48px)
                    .crossFade()
                    .bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), corner_radius, 0))
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
}
