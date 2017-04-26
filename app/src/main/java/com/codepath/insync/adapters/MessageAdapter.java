package com.codepath.insync.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.insync.R;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.utils.CommonUtil;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


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
        public @BindView(R.id.ivProfileLeft) CircleImageView ivProfileLeft;
        public @BindView(R.id.ivMessageRight) ImageView ivMessageRight;
        public @BindView(R.id.cvMessageRight) CardView cvMessageRight;
        public @BindView(R.id.tvBodyRight) TextView tvBodyRight;
        public @BindView(R.id.tvCaptionRight) TextView tvCaptionRight;
        public @BindView(R.id.tvFirstName) TextView tvFirstName;
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
        public @BindView(R.id.ivProfileRight) CircleImageView ivProfileRight;
        public @BindView(R.id.ivMessageLeft) ImageView ivMessageLeft;
        public @BindView(R.id.cvMessageLeft) CardView cvMessageLeft;
        public @BindView(R.id.tvBodyLeft) TextView tvBodyLeft;
        public @BindView(R.id.tvCaptionLeft) TextView tvCaptionLeft;
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
        ParseFile profileImage = message.getSender().getProfileImage();
        ParseFile mediaImage = message.getMedia();
        Date createdAt = message.getCreatedAt();
        if (createdAt == null) {
            createdAt = new Date();
        }
        String messageTime = CommonUtil.getTimeInFormat(createdAt);
        String messageBody = message.getBody();
        if (messageBody == null || messageBody.trim().length() == 0) {
            messageBody = "";
        }

        final String resultText = (messageBody + "  " + messageTime).trim();
        final SpannableString styledResultText = new SpannableString(resultText);

        styledResultText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), resultText.length()-messageTime.length(), resultText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        styledResultText.setSpan(new RelativeSizeSpan(.5f), resultText.length()-messageTime.length(), resultText.length(), 0); // set size
        styledResultText.setSpan(new ForegroundColorSpan(Color.GRAY), resultText.length()-messageTime.length(), resultText.length(), 0);// set color

        // Set item views based on your views and data model
        switch (holder.getItemViewType()) {
            case LEFT:
                ViewHolderLeft viewLeft = (ViewHolderLeft) holder;
                //set name
                viewLeft.tvFirstName.setText(message.getSender().getName().split(" ")[0]);
                // set the text view
                if (messageBody.length() == 0 || mediaImage != null) {
                    viewLeft.tvBodyRight.setVisibility(View.INVISIBLE);
                } else {
                    viewLeft.tvBodyRight.setVisibility(View.VISIBLE);
                    viewLeft.tvBodyRight.setText(styledResultText);
                }

                // reset the recycle view to the default profile image
                viewLeft.ivProfileLeft.setImageResource(R.drawable.ic_profile);

                // populate the profile image if it exists
                if (profileImage != null) {
                    Glide.with(mContext)
                            .load(profileImage.getUrl())
                            .placeholder(R.drawable.ic_profile)
                            .bitmapTransform(new RoundedCornersTransformation(mContext, 6, 0))
                            .crossFade()
                            .into(viewLeft.ivProfileLeft);
                }

                if (mediaImage != null) {
                    viewLeft.cvMessageRight.setVisibility(View.VISIBLE);
                    viewLeft.tvCaptionRight.setText(styledResultText);
                    Glide.with(mContext)
                            .load(mediaImage.getUrl())
                            .placeholder(R.drawable.ic_camera_alt_white_48px)
                            .crossFade()
                            .into(viewLeft.ivMessageRight);
                } else {
                    viewLeft.cvMessageRight.setVisibility(View.GONE);
                }

                break;
            default:
                ViewHolderRight viewRight = (ViewHolderRight) holder;
                // set the text view
                if (messageBody.length() == 0 || mediaImage != null) {
                    viewRight.tvBodyLeft.setVisibility(View.GONE);
                } else {
                    viewRight.tvBodyLeft.setVisibility(View.VISIBLE);
                    viewRight.tvBodyLeft.setText(styledResultText);
                }

                // reset the recycle view to the default profile image
                viewRight.ivProfileRight.setImageResource(R.drawable.ic_profile);

                // populate the profile image if it exists
                if (profileImage != null) {
                    viewRight.tvCaptionLeft.setText(styledResultText);
                    Glide.with(mContext)
                            .load(profileImage.getUrl())
                            .placeholder(R.drawable.ic_profile)
                            .bitmapTransform(new RoundedCornersTransformation(mContext, 6, 0))
                            .crossFade()
                            .into(viewRight.ivProfileRight);
                }

                // populate the message media if it exists
                if (mediaImage != null) {
                    viewRight.cvMessageLeft.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(mediaImage.getUrl())
                            .placeholder(R.drawable.ic_camera_alt_white_48px)
                            .crossFade()
                            .into(viewRight.ivMessageLeft);
                } else {
                    viewRight.cvMessageLeft.setVisibility(View.GONE);
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
        User sender = mMessages.get(position).getSender();
        if (sender.getObjectId().equals(User.getCurrentUser().getObjectId())) {
            return RIGHT;
        } else {
            return LEFT;
        }
    }
}
