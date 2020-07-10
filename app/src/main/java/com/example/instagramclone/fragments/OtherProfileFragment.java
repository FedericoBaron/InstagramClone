package com.example.instagramclone.fragments;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.instagramclone.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class OtherProfileFragment extends PostsFragment{

    private static final String TAG = "ProfileFragment";
    private int totalPosts = 20;

    ParseUser user;

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        // Unwrap the user passed in via bundle, using its simple name as a key
        user = Parcels.unwrap(getArguments().getParcelable("user"));

        // Only show posts that are from the user
        query.whereEqualTo(Post.KEY_USER, user);

        // Set a limit of 20 posts
        query.setLimit(totalPosts);

        // Sort by created at
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        // Finds the posts asynchronously
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Post post: posts){
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add posts to adapter
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    // Loads more posts when we reach the bottom of TL
    protected void loadMoreData() {
        Log.i(TAG, "Loading more data");
        totalPosts = totalPosts + NEW_POSTS;
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        // Only show posts that are from the user
        //query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Post.KEY_USER, user);

        // Set a limit
        query.setLimit(totalPosts);

        // Sort by created at
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Post post: posts){
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                //Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                // Add posts to adapter
                adapter.setAll(posts);
                adapter.notifyItemRangeInserted(posts.size()-5, posts.size());
            }
        });
    }
}
