package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.activities.LoginActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";

    private Button btnLogout;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private ImageView profilePicture;
    private ParseUser currentUser;
    private Button btnUpdateProfile;
    private Button btnChangePassword;

    private static final String KEY_PROFILE_PIC = "profilePicture";
//    private static final String KEY_USERNAME = "username";
//    private static final String KEY_EMAIL = "email";

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
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        profilePicture = view.findViewById(R.id.profilePicture);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        // Gets the person who's logged in
        currentUser = ParseUser.getCurrentUser();

        // Sets info to match that user
        etUsername.setText(currentUser.getUsername());
        etEmail.setText(currentUser.getEmail());
        ParseFile image = currentUser.getParseFile(KEY_PROFILE_PIC);
        if(image != null) {
            //Glide.with(getContext()).load(currentUser.getParseFile(KEY_PROFILE_PIC).getUrl()).into(profilePicture);
            // Binds image to ViewHolder with rounded corners
            int radius = 360; // corner radius, higher value = more rounded
            int margin = 0; // crop margin, set to 0 for corners with no crop
            Glide.with(getContext())
                    .load(currentUser.getParseFile(KEY_PROFILE_PIC).getUrl())
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(profilePicture);
        }


        // Listener for update profile
        updateProfileListener();

        // Listener for password change
        changePasswordListener();

        // Listens for logout button click
        logoutListener();
    }

    private void updateProfileListener() {
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setUsername(etUsername.getText().toString());
                currentUser.setEmail(etEmail.getText().toString());
                save();
            }
        });
    }

    private void changePasswordListener() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setPassword(etPassword.getText().toString());
                save();
            }
        });
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

    private void save(){
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "update save was successful!");
                Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
