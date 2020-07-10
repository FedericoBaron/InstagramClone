package com.example.instagramclone.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.instagramclone.R
import com.example.instagramclone.models.Post
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [ComposeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComposeFragment : Fragment() {
    private var etDescription: EditText? = null
    private var btnCaptureImage: Button? = null
    private var ivPostImage: ImageView? = null
    private var btnSubmit: Button? = null
    private var photoFile: File? = null
    private val photoFileName = "photo.jpg"

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etDescription = view.findViewById(R.id.etDescription)
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage)
        ivPostImage = view.findViewById(R.id.ivPostImage)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnCaptureImage?.setOnClickListener(View.OnClickListener { launchCamera() })

        //queryPosts();
        btnSubmit?.setOnClickListener(View.OnClickListener {
            val description = etDescription?.getText().toString()
            if (description.isEmpty()) {
                Toast.makeText(context, "Description cannot be empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (photoFile == null || ivPostImage?.getDrawable() == null) {
                Toast.makeText(context, "There is no image data", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val currentUser = ParseUser.getCurrentUser()
            savePost(description, currentUser, photoFile!!)
        })
    }

    private fun launchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        val fileProvider = FileProvider.getUriForFile(context!!, "com.example.fileprovider", photoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context!!.packageManager) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage!!.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = File(context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    private fun savePost(description: String, currentUser: ParseUser, photoFile: File) {
        val post = Post()
        post.description = description
        post.image = ParseFile(photoFile)
        post.user = currentUser
        post.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show()
            }
            Log.i(TAG, "Post save was successful!")
            etDescription!!.setText("")
            ivPostImage!!.setImageResource(0)
        }
    }

    companion object {
        private const val TAG = "ComposeFragment"
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42
    }
}