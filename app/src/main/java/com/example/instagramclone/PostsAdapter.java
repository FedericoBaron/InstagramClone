package com.example.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.activities.MainActivity;
import com.example.instagramclone.activities.PostDetailsActivity;
import com.example.instagramclone.fragments.OtherProfileFragment;
import com.example.instagramclone.fragments.PostsFragment;
import com.example.instagramclone.fragments.ProfileFragment;
import com.example.instagramclone.models.Post;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private static final String TAG = "PostsAdapter";
    private static final String KEY_PROFILE_PIC = "profilePicture";
    private Context context;
    private List<Post> posts;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public PostsAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public void setAll(List<Post> list){
        clear();
        addAll(list);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView timestamp;
        private ImageView profilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            timestamp = itemView.findViewById(R.id.timestamp);
            profilePic = itemView.findViewById(R.id.profilePic);

            // Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);

            // Listens for click of another user's profile
            profilePicListener();

        }

        // When someone's profile pic gets clicked you get taken to their profile
        private void profilePicListener(){
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"clicked on profile pic");
                    int position = getAdapterPosition();
                    // Make sure the position is valid i.e actually exists in the view
                    if(position != RecyclerView.NO_POSITION) {
                        // Get the post at the position, this won't work if the class is static
                        Post post = posts.get(position);
                        Bundle bundle = new Bundle();
                        ParseUser user = post.getUser();
                        bundle.putParcelable("user", Parcels.wrap(user));
                        Fragment fragment = new OtherProfileFragment();
                        fragment.setArguments(bundle);
                        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContainer, fragment)
                                .commit();
                    }
                }
            });
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if(image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            }
            timestamp.setText(getRelativeTimeAgo(post.getDate()));

            ParseFile profile = post.getUser().getParseFile(KEY_PROFILE_PIC);
            if(profile != null) {
                Glide.with(context)
                        .load(profile.getUrl())
                        .fitCenter()
                        .circleCrop()
                        .into(profilePic);
            }
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "OnClick adapter");
            // Gets item position
            int position = getAdapterPosition();

            // Make sure the position is valid i.e actually exists in the view
            if(position != RecyclerView.NO_POSITION) {
                // Get the post at the position, this won't work if the class is static
                Post post = posts.get(position);

                // Create intent for the new activity
                Intent intent = new Intent(context, PostDetailsActivity.class);

                // Serialize the post using the parceler, use its short name as a key
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));

                // Show the activity
                ((MainActivity) context).startActivityForResult(intent, 9);
            }
        }
    }

    // Gets how long ago something was tweeted in a good format
    public static String getRelativeTimeAgo(Date date) {
        String format = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        sf.setLenient(true);

        String timespan = "";
        long dateMillis = date.getTime();
        timespan = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return timespan;
    }
}
