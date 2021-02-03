package com.example.couplez;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Stack;

public class SwipeActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "TAG";
    private Button btn_yes, btn_no;
    private TextView user_name, user_info;
    private ImageView user_image;
    private DatabaseReference users;
    private FirebaseAuth m_auth;
    private StorageReference images;
    private String current_uid;
    private Stack<String> cards = new Stack<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        find_views();
        init_views();

        m_auth = FirebaseAuth.getInstance();
        current_uid = m_auth.getCurrentUser().getUid();

        display_cards(current_uid);

//        arr_adapter = new ArrayAdapter<>(this, R.layout.item, R.hello, cards);

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

    public void display_cards(String current_uid) {
        users = FirebaseDatabase.getInstance().getReference().child("Users");
        users.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    // TODO: 02/02/2021 Add check if this current user exist in other users "no" branch
                    if (!current_uid.equalsIgnoreCase(child.getKey()))
                        cards.push(child.getKey());
                }
                display_couple(cards.pop());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    public void display_couple(String card) {
        Log.d(TAG, "display_couple: " + card);
        images = FirebaseStorage.getInstance().getReference(card + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        images.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        user_image.setImageBitmap(bm);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    // TODO: 14/12/2020 update database with the current user decision
    private void update_db(String decision) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                update_db("yes");
                break;
            case R.id.btn_no:
                update_db("no");
                break;
            default:
                break;
        }
    }


}