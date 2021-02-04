package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_email, edit_password, edit_your_name, edit_your_age, edit_partner_age, edit_partner_name;
    private Button register;
    private FirebaseAuth m_auth;
    private ProgressBar progress_bar;
    private ImageView profile_img;
    private StorageReference storage;
    private Uri image_uri;
    private Bitmap image_bp;
    private HashMap<String, String> ages = new HashMap<>(), names = new HashMap<>();
    private String email, password, profile_image, info;
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
        edit_your_age = (EditText) findViewById(R.id.your_age);
        edit_your_name = (EditText) findViewById(R.id.your_name);
        edit_partner_age = (EditText) findViewById(R.id.partner_age);
        edit_partner_name = (EditText) findViewById(R.id.partner_name);
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
                try {
                    image_bp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                    image_bp = Bitmap.createScaledBitmap(image_bp, image_bp.getWidth() / 4, image_bp.getHeight() / 4, false);
                    profile_img.setImageBitmap(image_bp);
                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public byte[] bitmap_to_bytes() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image_bp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] byteArray = stream.toByteArray();
        return byteArray;
//        bitmap.recycle();
    }

    public void upload_img_to_firebase(Uri image_uri, String uid) {
        byte[] img = bitmap_to_bytes();
        StorageReference file_ref = storage.child("images").child(uid + ".jpg");
        file_ref.putBytes(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

    public String bitmap_to_str(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public void register_user() {
        email = edit_email.getText().toString().trim();
        profile_image = bitmap_to_str(image_bp);
        password = edit_password.getText().toString().trim();
        ages.put("your_age", edit_your_age.getText().toString().trim());
        ages.put("partner_age", edit_partner_age.getText().toString().trim());
        names.put("your_name", edit_your_name.getText().toString().trim());
        names.put("partner_name", edit_partner_name.getText().toString().trim());
        info = "Hiking, Traveling, Restaurant";

        if (names.get("your_name").isEmpty() || names.get("partner_name").isEmpty()) {
            edit_your_name.setError("Full name is required!");
            edit_your_name.requestFocus();
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
        if (ages.get("your_age").isEmpty() || Integer.parseInt(ages.get("your_age")) < 18 || ages.get("partner_age").isEmpty() || Integer.parseInt(ages.get("partner_age")) < 18) {
            edit_your_age.setError("Please provide a valid age!");
            edit_your_age.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edit_email.setError("Please provide a valid email!");
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
                            User user = new User(names, ages, email, profile_image, info);

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












