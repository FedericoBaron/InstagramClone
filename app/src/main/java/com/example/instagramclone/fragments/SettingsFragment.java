package com.example.instagramclone.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.activities.LoginActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;


import static android.app.Activity.RESULT_OK;

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

    private File photoFile;
    private String photoFileName = "photo.jpg";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final String KEY_PROFILE_PIC = "profilePicture";
    private View view;
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

        this.view = view;

        // Connects frontend to backend
        wireUI();

        // Sets up the profile
        setProfileInfo();

        // Listener for update profile
        updateProfileListener();

        // Listener for password change
        changePasswordListener();

        // Listens for logout button click
        logoutListener();

        profilePicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchCamera();
            }

        });
    }

    private void setProfileInfo(){
        // Gets the person who's logged in
        currentUser = ParseUser.getCurrentUser();

        // Sets info to match that user
        etUsername.setText(currentUser.getUsername());
        etEmail.setText(currentUser.getEmail());
        ParseFile image = currentUser.getParseFile(KEY_PROFILE_PIC);
        if(image != null) {
            Glide.with(getContext())
                    .load(currentUser.getParseFile(KEY_PROFILE_PIC).getUrl())
                    .fitCenter()
                    .circleCrop()
                    .into(profilePicture);
        }

    }

    // Sets variables to views
    private void wireUI(){
        btnLogout = view.findViewById(R.id.btnLogout);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        profilePicture = view.findViewById(R.id.profilePicture);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
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

    // Saves currentUser into backend
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
                setProfileInfo();
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                profilePicture.setImageBitmap(takenImage);
                currentUser.put("profilePicture", new ParseFile(photoFile));
                save();

            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

}
