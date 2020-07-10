package com.example.instagramclone.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class PostDetailsActivity extends AppCompatActivity {

    private static final String KEY_LIKES = "likes";
    private static final String TAG = "PostDetailsActivity";
    private static final String KEY_PROFILE_PIC = "profilePicture";
    private Post post;
    private TextView tvUsername;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView timestamp;
    private Button like;
    private TextView likeCount;
    private ImageView profilePic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.nav_logo_whiteout);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

        wireUI();


        // Listens for like click
        likeListener();

        // Unwrap the post passed in via intent, using its simple name as a key
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvDescription.setText(post.getDescription());
        tvUsername.setText(post.getUser().getUsername());
        ParseFile image = post.getImage();
        if(image != null) {
            Glide.with(this).load(post.getImage().getUrl()).into(ivImage);
        }
        timestamp.setText(PostsAdapter.getRelativeTimeAgo(post.getDate()));
        ParseFile profile = post.getUser().getParseFile(KEY_PROFILE_PIC);
        if(profile != null) {
            Glide.with(this)
                    .load(profile.getUrl())
                    .fitCenter()
                    .circleCrop()
                    .into(profilePic);
        }
        likeCount.setText(Integer.toString(post.getLikeCount()));

        // Gets the array of likes
        JSONArray likesArray = post.getLikesArray();

        // If the array is null make a new one
        if(likesArray == null){
            post.setLikesArray(new JSONArray());
            likesArray = post.getLikesArray();
        }

        // Sets heart color
        // Go through each post and see whether they were liked by current user
        for(int i = 0; i < likesArray.length(); i++) {
            // gets the user object id at array position i
            String likeObjectId = null;
            try {
                likeObjectId = (String) likesArray.get(i);
                // if the objectId at pos i is the same as currentUser object id
                // then set liked to true unlike post
                if (likeObjectId.equals(ParseUser.getCurrentUser().getObjectId())) {
                    like.setBackgroundResource(R.drawable.ic_vector_heart);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void wireUI(){
        tvUsername = findViewById(R.id.tvUsername);
        ivImage = findViewById(R.id.ivImage);
        tvDescription = findViewById(R.id.tvDescription);
        timestamp = findViewById(R.id.timestamp);
        like = findViewById(R.id.like);
        likeCount = findViewById(R.id.likeCount);
        profilePic = findViewById(R.id.profilePic);
    }

    private void likeListener(){
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Gets the array of likes
                JSONArray likesArray = post.getLikesArray();

                // If the array is null make a new one
                if(likesArray == null){
                    post.setLikesArray(new JSONArray());
                    likesArray = post.getLikesArray();
                }

                // Flag to see if user liked post
                boolean unliked = false;

                // Go through each post and see whether they were liked by current user
                for(int i = 0; i < likesArray.length(); i++){
                    Log.i(TAG, "looping through the likesarray");
                    try {
                        // gets the user object id at array position i
                        String likeObjectId = (String) likesArray.get(i);

                        // if the objectId at pos i is the same as currentUser object id
                        // then set liked to true unlike post
                        if(likeObjectId.equals(ParseUser.getCurrentUser().getObjectId())){
                            Log.i(TAG, "Already liked this post");
                            unliked = true;

                            like.setBackgroundResource(R.drawable.ic_vector_heart_gray);
                            // this unlikes the post by removing the like from the likes array
                            likesArray.remove(i);

                            // Reduces like count
                            post.setLikeCount(post.getLikeCount() - 1);

                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // if the object id isn't already in the array then we proceed to like the post
                if(!unliked){
                    likesArray.put(ParseUser.getCurrentUser().getObjectId());
                    like.setBackgroundResource(R.drawable.ic_vector_heart);

                    // Increases like count
                    post.setLikeCount(post.getLikeCount() + 1);
                }

                // Sets the array to add/remove the like
                post.setLikesArray(likesArray);

                // Saves modified array to backend
                save();
                Log.i(TAG, "liked/unliked this post" + post.getLikesArray());
            }
        });
    }

    // Saves post into backend
    private void save(){
        post.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error while saving", e);
                    //Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                }
                likeCount.setText(Integer.toString(post.getLikesArray().length()));
                Log.i(TAG, "update save was successful!");
                //Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Listener for back button is pressed
    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("post", Parcels.wrap(post));
        setResult(RESULT_OK,i);
        finish();
    }
}
