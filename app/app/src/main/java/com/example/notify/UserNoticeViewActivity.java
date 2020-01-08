package com.example.sjecnotify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserNoticeViewActivity extends AppCompatActivity
{

    private TextView noticetitle,noticeDate,noticeDescription;
    private ImageView noticeImage;
    private DatabaseReference noticeRef;
    private Uri ImageUri;
    private StorageReference ProductImagesRef;
    private String noticeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notice_view);

        noticetitle=(TextView)findViewById(R.id.notice_view_title);
        noticeDate=(TextView)findViewById(R.id.notice_view_date);
        noticeDescription=(TextView)findViewById(R.id.notice_view_description);
        noticeImage=(ImageView) findViewById(R.id.notice_view_image);


        noticeId=getIntent().getStringExtra("date");



        ProductImagesRef= FirebaseStorage.getInstance().getReference().child("Notice Image");
        noticeRef= FirebaseDatabase.getInstance().getReference().child("Notice").child(noticeId);

        displayNoticeInfo();





    }

    private void displayNoticeInfo()
    {
        noticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String nTitle = dataSnapshot.child("title").getValue().toString();
                    String nDescription = dataSnapshot.child("description").getValue().toString();
                    String nImage = dataSnapshot.child("image").getValue().toString();
                    String nDate=dataSnapshot.child("date").getValue().toString();

                    noticetitle.setText(nTitle);
                    noticeDate.setText(nDate);
                    noticeDescription.setText(nDescription);
                    Picasso.get().load(nImage).into(noticeImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
