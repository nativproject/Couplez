package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_email, edit_password;
    private Button login, register, forgot;
    private ProgressBar progress_bar;
    private FirebaseAuth m_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find_views();
        init_views();
        m_auth = FirebaseAuth.getInstance();
        progress_bar.bringToFront();
    }

    private void find_views() {
        edit_email = (EditText) findViewById(R.id.main_email);
        edit_password = (EditText) findViewById(R.id.main_password);
        login = (Button) findViewById(R.id.main_login);
        register = (Button) findViewById(R.id.main_register);
        forgot = (Button) findViewById(R.id.main_forgot);
        progress_bar = (ProgressBar) findViewById(R.id.main_progress_bar);
    }

    private void init_views() {
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        forgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.main_login:
                user_login();
                break;
            case R.id.main_forgot:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    private void user_login() {
        String email = edit_email.getText().toString().trim();
        String password = edit_password.getText().toString().trim();

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
        if (password.isEmpty()) {
            edit_password.setError("Password required!");
            edit_password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            edit_password.setError("Password is too short! Minimum 6 characters");
            edit_password.requestFocus();
            return;
        }

        progress_bar.setVisibility(View.VISIBLE);

        m_auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(MainActivity.this, IntroActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Failed to login! Please try again!", Toast.LENGTH_LONG).show();
                }
                progress_bar.setVisibility(View.GONE);
            }
        });
    }
}