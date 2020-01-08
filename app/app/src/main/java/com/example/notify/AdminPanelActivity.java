package com.example.sjecnotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AdminPanelActivity extends AppCompatActivity {

    private Button newNoticeBtn,checkNoticeBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        newNoticeBtn=findViewById(R.id.new_notice);
        checkNoticeBtn=findViewById(R.id.change_notice);
        backBtn=findViewById(R.id.backimage);

        newNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminPanelActivity.this, NewNoticeActivity.class);
                startActivity(intent);
            }
        });

    }
}
