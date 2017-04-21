package com.codepath.insync.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.insync.R;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.utils.CommonUtil;
import com.parse.ParseFile;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // view types
    private final int LEFT = 0, RIGHT = 1;

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

    public class ViewHolderLeft extends RecyclerView.ViewHolder {
        public @BindView(R.id.ivProfileLeft) ImageView ivProfileLeft;
        public @BindView(R.id.ivMessageRight) ImageView ivMessageRight;
        public @BindView(R.id.tvBodyRight) TextView tvBodyRight;
        public @BindView(R.id.tvTimeLeft) TextView tvTimeLeft;

        public ViewHolderLeft(final View itemView) {
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

    public class ViewHolderRight extends RecyclerView.ViewHolder {
        public @BindView(R.id.ivProfileRight) ImageView ivProfileRight;
        public @BindView(R.id.ivMessageLeft) ImageView ivMessageLeft;
        public @BindView(R.id.tvBodyLeft) TextView tvBodyLeft;
        public @BindView(R.id.tvTimeRight) TextView tvTimeRight;

        public ViewHolderRight(final View itemView) {
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
    private List<Message> mMessages;
    // Store the context for easy access
    private Context mContext;

    // Pass in the message array into the constructor
    public MessageAdapter(Context context, List<Message> messages) {
        mMessages = messages;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case LEFT:
                View viewLeft = inflater.inflate(R.layout.item_message_left, parent, false);
                viewHolder = new ViewHolderLeft(viewLeft);
                break;
            default:
                View viewRight = inflater.inflate(R.layout.item_message_right, parent, false);
                viewHolder = new ViewHolderRight(viewRight);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get the data model based on position
        Message message = mMessages.get(position);
        ParseFile profileImage = message.getMedia();

        Bitmap mediaBitmap = message.getMediaImageBitmap();
        String messageTime = CommonUtil.getTimeInFormat(message.getCreatedAt());

        // Set item views based on your views and data model
        switch (holder.getItemViewType()) {
            case LEFT:
                ViewHolderLeft viewLeft = (ViewHolderLeft) holder;
                // set the text view
                viewLeft.tvBodyRight.setText(message.getBody());
                viewLeft.tvTimeLeft.setText(messageTime);

                // reset the recycle view to the default profile image
                viewLeft.ivProfileLeft.setImageResource(R.drawable.ic_profile);

                // populate the profile image if it exists
                if (profileImage != null) {
                    Glide.with(mContext)
                            .load(profileImage.getUrl())
                            .placeholder(R.drawable.ic_attach_file_white_48px)
                            .crossFade()
                            .into(viewLeft.ivProfileLeft);
                }

                // populate the message media if it exists
                if (mediaBitmap != null) {
                    viewLeft.ivMessageRight.setVisibility(View.VISIBLE);
                    viewLeft.ivMessageRight.setImageBitmap(mediaBitmap);
                } else {
                    viewLeft.ivMessageRight.setVisibility(View.GONE);
                }

                break;
            default:
                ViewHolderRight viewRight = (ViewHolderRight) holder;
                // set the text view
                viewRight.tvBodyLeft.setText(message.getBody());
                viewRight.tvTimeRight.setText(messageTime);

                // reset the recycle view to the default profile image
                viewRight.ivProfileRight.setImageResource(R.drawable.ic_profile);

                // populate the profile image if it exists
                if (profileImage != null) {
                    Glide.with(mContext)
                            .load(profileImage.getUrl())
                            .placeholder(R.drawable.ic_attach_file_white_48px)
                            .crossFade()
                            .into(viewRight.ivProfileRight);
                }

                // populate the message media if it exists
                if (mediaBitmap != null) {
                    viewRight.ivMessageLeft.setVisibility(View.VISIBLE);
                    viewRight.ivMessageLeft.setImageBitmap(mediaBitmap);
                } else {
                    viewRight.ivMessageLeft.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
    
    public Message getItem(int position) {
        return getItem(getItemCount() - position - 1);
    }

    public void clear() {
        mMessages.clear();
        notifyDataSetChanged();

    }

    public void addAll(List<Message> newMessages) {
        int position = mMessages.size();
        for (int i=0; i < newMessages.size(); i++) {
            mMessages.add(newMessages.get(i));
            notifyItemInserted(position);
            position++;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int newPosition = getItemCount() - position - 1;
        return (newPosition % 2 == 0) ? LEFT : RIGHT;
    }

}
