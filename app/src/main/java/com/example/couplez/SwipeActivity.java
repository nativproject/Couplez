package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class SwipeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_yes, btn_no;
    private TextView user_name, user_age, user_about;
    private ProgressBar progress_bar;
    private ImageView user_image;
    private DatabaseReference all_users;
    private FirebaseAuth m_auth;
    private String current_uid, TAG = "TAG";
    public Stack<String> users_ids = new Stack<String>();
    public Stack<User> users_info = new Stack<User>();
    public User current_user = null;
    public boolean finished = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        find_views();
        init_views();
        Log.d(TAG, "onCreate");
        m_auth = FirebaseAuth.getInstance();
        current_uid = m_auth.getCurrentUser().getUid();
        progress_bar.bringToFront();

        read_data(current_uid, () -> {
            finished = true;
            current_user = users_info.pop();
            init_couples(current_user);
            progress_bar.setVisibility(View.GONE);
        });
    }

    public Bitmap str_to_bitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void find_views() {
        user_image = (ImageView) findViewById(R.id.user_img);
        user_name = (TextView) findViewById(R.id.user_name);
        user_age = (TextView) findViewById(R.id.user_age);
        user_about = (TextView) findViewById(R.id.user_about);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_no = (Button) findViewById(R.id.btn_no);
        progress_bar = (ProgressBar) findViewById(R.id.swipe_progress_bar);
    }

    public void init_views() {
        btn_no.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
    }

    private void read_data(String current_uid, firebase_callback callback) {
        Log.d(TAG, "read_data:                              .");
        all_users = FirebaseDatabase.getInstance().getReference().child("Users");
        all_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (!current_uid.equalsIgnoreCase(child.getKey()) && !child.child("no").getKey().equals(current_uid)) {//Add this late => && child.child("no").getKey().equals(current_uid)
                        users_ids.push(child.getKey());//Create stack with all potential uid's
                        String profile_image = child.child("profile_image").getValue().toString();
                        String your_age = child.child("ages").child("your_age").getValue().toString();
                        String partner_age = child.child("ages").child("partner_age").getValue().toString();
                        String your_name = child.child("names").child("your_name").getValue().toString();
                        String partner_name = child.child("names").child("partner_name").getValue().toString();
                        String email = child.child("email").getValue().toString();
                        String about = child.child("about").getValue().toString();

                        User user = new User(email, your_name, your_age, partner_name, partner_age, profile_image, about);
                        users_info.push(user); //Create stack with all uid's info
                    }
                }
                callback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private interface firebase_callback {
        void onCallback();
    }

    public void init_couples(User current_user) {
        print_stack(users_info);
        Bitmap decode = str_to_bitmap(current_user.profile_image);
        user_image.setImageBitmap(decode);
        user_name.setText(current_user.your_name + " & " + current_user.partner_name);
        user_age.setText(current_user.your_age + " & " + current_user.partner_age);
        user_about.setText(current_user.about);
    }

    // TODO: 14/12/2020 update database with the current user decision
    public void user_selection(String decision, String current_uid) {
        Log.d(TAG, "user_selection: " + decision);
        try {
            update_db(decision, users_ids.pop(), current_uid, () -> {
                if (!users_info.empty()) {
                    current_user = users_info.pop();
                    init_couples(current_user);
                } else
                    show_empty_screen(); // TODO: 04/02/2021 Show screen that indicates that no users are left to show
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void print_stack(Stack<User> users) {
        for (User user : users) {
            Log.d(TAG, "print_stack: " + user.toString());
        }
    }

    public void update_db(String decision, String user_id, String current_uid, firebase_callback callback) {
        all_users = FirebaseDatabase.getInstance().getReference().child("Users");
        all_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                all_users.child(user_id).child("decision").child(decision).child(current_uid).setValue(true);
                callback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void show_empty_screen() {
        user_image.setImageDrawable(getResources().getDrawable(R.drawable.sorry));
        btn_no.setClickable(false);
        btn_yes.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                if (finished)
                    user_selection("yes", current_uid);
                break;
            case R.id.btn_no:
                if (finished)
                    user_selection("no", current_uid);
                break;
            default:
                break;
        }
    }
}