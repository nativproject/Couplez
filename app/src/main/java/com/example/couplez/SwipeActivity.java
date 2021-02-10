package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    public boolean finished = false, is_child_exist = false, is_equal_current = false; // FLAGS


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        find_views();
        init_views();
        Log.d(TAG, "ON CREATE");
        m_auth = FirebaseAuth.getInstance();
        current_uid = m_auth.getCurrentUser().getUid();
        progress_bar.bringToFront();

        read_data(current_uid, new first_callback() {
            @Override
            public void onCallback() {
                Log.d(TAG, "------------------------------1st CALLBACK------------------------------");
                if (!finished) {
                    print_users();
                    btn_no.setClickable(true);
                    btn_yes.setClickable(true);
                    finished = true;
                    if (!users_info.isEmpty()) {
                        display_couple(users_info.peek());
                        users_info.pop();
                    } else {
                        show_empty_screen();
                    }
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void print_users() {
        int i = 1;
        for (User user : users_info) {
            Log.d(TAG, i + " " + user.toString());
            Log.d(TAG, "UID: " + users_ids.get(i - 1));
            i++;
        }
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

    private void read_data(String current_uid, first_callback callback) {
        btn_no.setClickable(false);
        btn_yes.setClickable(false);

        all_users = FirebaseDatabase.getInstance().getReference().child("Users");
        all_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!finished) {
                    Log.d(TAG, "CURRENT  USER " + current_uid);
                    for (DataSnapshot child : snapshot.getChildren()) {
                        is_child_exist = exist_in_decision(child);
                        is_equal_current = equal_to_current_user(child, current_uid);
                        if (!is_equal_current && !is_child_exist) {
                            users_ids.push(child.getKey()); // Users ID stack
                            String profile_image = child.child("profile_image").getValue().toString();
                            String your_age = child.child("ages").child("your_age").getValue().toString();
                            String partner_age = child.child("ages").child("partner_age").getValue().toString();
                            String your_name = child.child("names").child("your_name").getValue().toString();
                            String partner_name = child.child("names").child("partner_name").getValue().toString();
                            String email = child.child("email").getValue().toString();
                            String about = child.child("about").getValue().toString();

                            User user = new User(email, your_name, your_age, partner_name, partner_age, profile_image, about);
                            users_info.push(user); // Users information Stack
                        }
                        is_child_exist = false;
                    }
                }
                callback.onCallback();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean equal_to_current_user(DataSnapshot child, String current_uid) {
        String c_uid = child.getKey();
        if (c_uid.equals(current_uid))
            return true;
        return false;
    }

    // Check if decision has made
    // and if child exist in decision
    public boolean exist_in_decision(DataSnapshot child) {
        if (child.hasChild("decision"))
            if (child.child("decision").hasChild("dislike") && child.child("decision").child("dislike").hasChild(current_uid))
                return true;
            else if (child.child("decision").hasChild("like") && child.child("decision").child("like").hasChild(current_uid))
                return true;
        return false;
    }

    // simple callback function
    private interface first_callback {
        void onCallback();
    }

    public void display_couple(User current_user) {
        Bitmap decode = str_to_bitmap(current_user.profile_image);
        user_image.setImageBitmap(decode);
        user_name.setText(current_user.your_name + " & " + current_user.partner_name);
        user_age.setText(current_user.your_age + " & " + current_user.partner_age);
        user_about.setText(current_user.about);
    }

    // TODO: 14/12/2020 update database with the current user decision
    public void user_selection(String decision, String current_uid) {
        Log.d(TAG, "------------------------------USER SELECTION: " + decision + "------------------------------");
        try {
            update_db(decision, users_ids.pop(), current_uid);
            Log.d(TAG, "------------------------------END DATABASE UPDATE------------------------------");
            if (!users_info.empty()) {
                btn_no.setClickable(true);
                btn_yes.setClickable(true);
                print_users();
                Log.d(TAG, "------------------------------END 2nd PRINT------------------------------");
                display_couple(users_info.peek());
                users_info.pop();
            } else {
                btn_no.setClickable(false);
                btn_yes.setClickable(false);
                show_empty_screen();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void update_db(String decision, String user_id, String current_uid) {
        btn_no.setClickable(false);
        btn_yes.setClickable(false);
        all_users = FirebaseDatabase.getInstance().getReference().child("Users");
        all_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                all_users.child(user_id).child("decision").child(decision).child(current_uid).setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void show_empty_screen() {
        btn_no.setClickable(false);
        btn_yes.setClickable(false);
        user_image.setImageDrawable(getResources().getDrawable(R.drawable.sorry));
        user_age.setText("");
        user_name.setText("");
        user_about.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                if (finished)
                    user_selection("like", current_uid);
                break;
            case R.id.btn_no:
                if (finished)
                    user_selection("dislike", current_uid);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}