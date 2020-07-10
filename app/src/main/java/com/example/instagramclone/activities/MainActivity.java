package com.example.instagramclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.instagramclone.R;
import com.example.instagramclone.fragments.ComposeFragment;
import com.example.instagramclone.fragments.PostsFragment;
import com.example.instagramclone.fragments.ProfileFragment;
import com.example.instagramclone.fragments.SettingsFragment;
import com.example.instagramclone.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.nav_logo_whiteout);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new PostsFragment();
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_settings:
                        fragment = new SettingsFragment();
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }

    // Updates post
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 9 && resultCode == RESULT_OK){
            Post post = Parcels.unwrap(data.getParcelableExtra("post"));
            for(int i = 0; i < PostsFragment.allPosts.size(); i++){
                if(PostsFragment.allPosts.get(i).getObjectId().equals(post.getObjectId())){
                    PostsFragment.allPosts.remove(i);
                    PostsFragment.allPosts.add(i, post);
                    PostsFragment.adapter.notifyItemChanged(i);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




}