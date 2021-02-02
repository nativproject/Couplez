package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button reset;
    private EditText edit_email;
    private ProgressBar progress_bar;
    private FirebaseAuth m_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        find_views();
        init_views();
        m_auth = FirebaseAuth.getInstance();
        progress_bar.bringToFront();
    }

    private void find_views() {
        edit_email = (EditText) findViewById(R.id.reset_email);
        reset = (Button) findViewById(R.id.reset);
        progress_bar = (ProgressBar) findViewById(R.id.forgot_progress_bar);
    }

    private void init_views() {
        reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        reset_password();
    }

    public void reset_password() {
        String email = edit_email.getText().toString().trim();

        if (email.isEmpty()) {
            edit_email.setError("Email required!");
            edit_email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edit_email.setError("Please enter a valid email!");
            edit_email.requestFocus();
            return;
        }

        progress_bar.setVisibility(View.VISIBLE);

        m_auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to reset your password! Please try again!", Toast.LENGTH_LONG).show();
                }
                progress_bar.setVisibility(View.GONE);
            }
        });
    }
}