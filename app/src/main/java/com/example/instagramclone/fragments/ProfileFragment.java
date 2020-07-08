package com.example.instagramclone.fragments;

import android.util.Log;

import com.example.instagramclone.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment{

    private static final String TAG = "ProfileFragment";

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        // Only show posts that are from the user
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

        // Set a limit of 20 posts
        query.setLimit(20);

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

                // Add posts to adapter
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
