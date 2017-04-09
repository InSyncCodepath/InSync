package com.codepath.insync.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepath.insync.R;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


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
    private List<ParseFile> mEDImages;
    // Store the context for easy access
    private Context mContext;

    // Pass in the message array into the constructor
    public EDImageAdapter(Context context, List<ParseFile> edImages) {
        mEDImages = edImages;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public EDImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom image view layout
        View edImageView = inflater.inflate(R.layout.item_edimage, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(edImageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the data model based on position
        ParseFile edImage = mEDImages.get(position);
        Bitmap imageBitmap;
        holder.ivEDImage.setImageResource(R.drawable.ic_camera_alt_white_48px);

        try {
            imageBitmap = BitmapFactory.decodeStream(edImage.getDataStream());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        holder.ivEDImage.setImageBitmap(imageBitmap);



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

    public void addAll(List<ParseFile> newEDImages) {
        int position = mEDImages.size();
        for (int i=0; i < newEDImages.size(); i++) {
            mEDImages.add(newEDImages.get(i));
            notifyItemInserted(position);
            position++;
        }
    }

}
