package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.R;
import com.example.instagramclone.activities.LoginActivity;
import com.example.instagramclone.activities.MainActivity;
import com.parse.ParseUser;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private Button btnLogout;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sets variables to views
        btnLogout = view.findViewById(R.id.btnLogout);

        // Listens for logout button click
        logoutListener();
    }

    private void logoutListener(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

                // Go to login screen
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);

                // Prevent people from going back after logging out
                getActivity().finish();
            }
        });
    }

}
