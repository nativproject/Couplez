package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SwipeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_yes, btn_no;
    private TextView user_name, user_info;
    private ImageView user_image;
    private DatabaseReference users;
    private FirebaseAuth m_auth;
    private String current_user;
    private ArrayList<String> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        find_views();
        init_views();
        m_auth = FirebaseAuth.getInstance();
        current_user = m_auth.getCurrentUser().getUid();

        display_couples(current_user);
    }

    private void find_views() {
        user_image = (ImageView) findViewById(R.id.user_img);
        user_name = (TextView) findViewById(R.id.user_name);
        user_info = (TextView) findViewById(R.id.user_info);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_no = (Button) findViewById(R.id.btn_no);
    }

    private void init_views() {
        btn_no.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
    }

    public void display_couples(String current_user) {
        users = FirebaseDatabase.getInstance().getReference().child("Users");
        users.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cards = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    // TODO: 02/02/2021 Add check if this current user exist in other users "no" branch
                    if (!current_user.equalsIgnoreCase(child.getKey()))
                        cards.add(child.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
//                update_db("yes");
                Log.d("aaa", "hello");
                break;
            case R.id.btn_no:
//                update_db("no");
                Log.d("aaa", "bitch");
                break;
            default:
                break;
        }
    }

//    private void update_db(String ans) {
//        //// TODO: 14/12/2020 update database with the current user decision
//    }
}