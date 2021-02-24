package com.example.couplez;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Stack;

public class ChatActivity extends AppCompatActivity {
    private EditText msg_input;
    private ListView messages_view;
    private FirebaseAuth m_auth;
    private String current_uid;
    private Stack<String> messages = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        find_views();
        m_auth = FirebaseAuth.getInstance();
        current_uid = m_auth.getCurrentUser().getUid();
    }

    private void find_views() {
        msg_input = (EditText) findViewById(R.id.msg_input);
        messages_view = (ListView) findViewById(R.id.messages_view);
    }

    private void send_message() {

    }
}