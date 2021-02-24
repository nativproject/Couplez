package com.example.couplez;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener {
    private Button discover, chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        find_views();
        init_views();
    }

    private void find_views() {
        discover = (Button) findViewById(R.id.discover);
        chat = (Button) findViewById(R.id.chat);
    }

    private void init_views() {
        discover.setOnClickListener(this);
        chat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                startActivity(new Intent(this, SwipeActivity.class));
                break;
            case R.id.chat:
                startActivity(new Intent(this, ChatActivity.class));
                break;
            default:
                break;
        }
    }
}