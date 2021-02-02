package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_email, edit_username, edit_password, edit_age;
    private Button register;
    private FirebaseAuth m_auth;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        find_views();
        init_views();
        m_auth = FirebaseAuth.getInstance();
        progress_bar.bringToFront();
    }

    private void find_views() {
        edit_email = (EditText) findViewById(R.id.email);
        edit_age = (EditText) findViewById(R.id.age);
        edit_username = (EditText) findViewById(R.id.username);
        edit_password = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);
        progress_bar = (ProgressBar) findViewById(R.id.register_progress_bar);
    }

    private void init_views() {
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                register_user();
                break;
            default:
                break;
        }
    }

    private void register_user() {
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












