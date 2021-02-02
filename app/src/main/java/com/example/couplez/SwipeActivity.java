package com.example.couplez;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SwipeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_yes, btn_no;
    private TextView user_name, user_info;
    private ImageView user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
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
//        //// TODO: 14/12/2020 FireBase Conneciton
//    }
}