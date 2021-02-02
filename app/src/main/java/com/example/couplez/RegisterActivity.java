package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_email, edit_username, edit_password, edit_age;
    private Button register;
    private FirebaseAuth m_auth;
    private ProgressBar progress_bar;
    private ImageView profile_img;
    private StorageReference storage;
    private Uri image_uri;
    private boolean img_uploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        find_views();
        init_views();
        m_auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        progress_bar.bringToFront();
    }

    private void find_views() {
        edit_email = (EditText) findViewById(R.id.email);
        edit_age = (EditText) findViewById(R.id.age);
        edit_username = (EditText) findViewById(R.id.username);
        edit_password = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);
        progress_bar = (ProgressBar) findViewById(R.id.register_progress_bar);
        profile_img = (ImageView) findViewById(R.id.profile_img);
    }

    private void init_views() {
        profile_img.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                register_user();
                break;
            case R.id.profile_img:
                set_profile_img();
                img_uploaded = true;
                break;
            default:
                break;
        }
    }

    public void set_profile_img() {
        //open gallery
        Intent open_gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(open_gallery, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                image_uri = data.getData();
                profile_img.setImageURI(image_uri);
            }
        }
    }

    public void upload_img_to_firebase(Uri image_uri, String uid) {
        StorageReference file_ref = storage.child(uid + ".jpg");
        file_ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegisterActivity.this, "Image uploaded successfully!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Failed to load image! Please try again!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void register_user() {
        String email = edit_email.getText().toString().trim();
        String age = edit_age.getText().toString().trim();
        String username = edit_username.getText().toString().trim();
        String password = edit_password.getText().toString().trim();
        Log.d("asdf", email + " " + password + " " + username);
        if (username.isEmpty()) {
            edit_username.setError("Full name is required!");
            edit_username.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            edit_password.setError("Password is required!");
            edit_password.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edit_email.setError("Email is required!");
            edit_email.requestFocus();
            return;
        }
        if (age.isEmpty() || Integer.parseInt(age) < 18) {
            edit_age.setError("Please provide valid age!");
            edit_age.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edit_email.setError("Please provide valid email!");
            edit_email.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edit_password.setError("Password is too short! Minimum 6 characters");
            edit_password.requestFocus();
            return;
        }

        if (!img_uploaded) {
            Toast.makeText(RegisterActivity.this, "Please select your profile picture!", Toast.LENGTH_LONG).show();
            return;
        }

        progress_bar.setVisibility(View.VISIBLE);
        m_auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(username, age, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        upload_img_to_firebase(image_uri, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        finishAffinity();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to register! Please Try Again!", Toast.LENGTH_LONG).show();
                                    }
                                    progress_bar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to register! Please Try Again!", Toast.LENGTH_LONG).show();
                            progress_bar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}












