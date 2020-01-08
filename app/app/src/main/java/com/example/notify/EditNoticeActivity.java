package com.example.sjecnotify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class EditNoticeActivity extends AppCompatActivity
{

    private EditText editTitle,editDescription;
    private ImageView editNoticeImage,back;
    private String noticeId;
    private DatabaseReference noticeRef;
    private Button updateNotice;
    private TextView validation;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private StorageReference ProductImagesRef;
    private String productRandomKey,downloadImageUrl,checker="";
    private RelativeLayout r1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notice);
        if(!isConnected(EditNoticeActivity.this))
            buildDialog(EditNoticeActivity.this).show();

        else {


            editTitle = (EditText) findViewById(R.id.edit_notice_title);
            editDescription = (EditText) findViewById(R.id.edit_notice_description);
            editNoticeImage = (ImageView) findViewById(R.id.edit_notice_image);
            ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Notice Image");
            updateNotice = (Button) findViewById(R.id.update_notice);
            back = findViewById(R.id.backimage);
            noticeId = getIntent().getStringExtra("date");
            r1 = findViewById(R.id.l1);

            noticeRef = FirebaseDatabase.getInstance().getReference().child("Notice").child(noticeId);

            displayNoticeInfo();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(EditNoticeActivity.this, AdminNoticeActivity.class));
                }
            });

            editNoticeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checker="clicked";
                    openGallery();
                }
            });

            updateNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checker.equals("clicked")) {
                        applyChanges();
                    }
                    else
                    {
                        updateWithoutImage();
                    }


                }
            });
        }


    }

    private void updateWithoutImage()
    {
        String nTitle=editTitle.getText().toString();

        String nDescription=editDescription.getText().toString();




        if(nTitle.equals(""))
        {
            validation.setVisibility(View.GONE);
            validation.setText("Notice title is important");
            updateNotice.setEnabled(true);
        }
        else if(nDescription.equals(""))
        {
            validation.setVisibility(View.GONE);
            validation.setText("Notice description is important");
            updateNotice.setEnabled(true);
        }
        else
        {

            HashMap<String,Object> productMap=new HashMap<>();
            productMap.put("title",nTitle);
            productMap.put("description",nDescription);

            noticeRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        updateNotice.setEnabled(false);
                        Toast.makeText(EditNoticeActivity.this, "Changes Applyed Successfully", Toast.LENGTH_SHORT).show();
                        r1.setVisibility(View.GONE);
                        Intent intent=new Intent(EditNoticeActivity.this,AdminNoticeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }


    private void applyChanges()
    {
        String nTitle=editTitle.getText().toString();

        String nDescription=editDescription.getText().toString();




        if(nTitle.equals(""))
        {
            validation.setVisibility(View.GONE);
            validation.setText("Notice title is important");
            updateNotice.setEnabled(true);
        }
        else if(nDescription.equals(""))
        {
            validation.setVisibility(View.GONE);
            validation.setText("Notice description is important");
            updateNotice.setEnabled(true);
        }
        else
        {

            uploadImage();

        }
    }

    private void displayNoticeInfo()
    {
        r1.setVisibility(View.VISIBLE);

        noticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String nTitle = dataSnapshot.child("title").getValue().toString();
                    String nDescription = dataSnapshot.child("description").getValue().toString();
                    String nImage = dataSnapshot.child("image").getValue().toString();

                    editTitle.setText(nTitle);
                    editDescription.setText(nDescription);
                    Picasso.get().load(nImage).into(editNoticeImage);
                    r1.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                updateNotice.setEnabled(true);

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);

    }

    private void uploadImage()
    {
        r1.setVisibility(View.VISIBLE);
        updateNotice.setEnabled(false);
        final StorageReference filePath=ProductImagesRef.child(ImageUri.getLastPathSegment()+ productRandomKey+".jpg");
        final UploadTask uploadTask=filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message=e.toString();
                Toast.makeText(EditNoticeActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(EditNoticeActivity.this, "Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        downloadImageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {

                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(EditNoticeActivity.this, "Got the notice image url Successfully....", Toast.LENGTH_SHORT).show();
                            uploadDataAndImage();
                        }
                        else
                        {
                            Toast.makeText(EditNoticeActivity.this, "Sorry..Some Error Occurred!", Toast.LENGTH_SHORT).show();
                            updateNotice.setEnabled(true);
                        }
                    }
                });
            }
        });
    }

    private void uploadDataAndImage()
    {
        String nTitle=editTitle.getText().toString();

        String nDescription=editDescription.getText().toString();

        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("title",nTitle);
        productMap.put("description",nDescription);
        productMap.put("image",downloadImageUrl);

        noticeRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    updateNotice.setEnabled(false);
                    Toast.makeText(EditNoticeActivity.this, "Changes Applyed Successfully", Toast.LENGTH_SHORT).show();
                    r1.setVisibility(View.GONE);
                    Intent intent=new Intent(EditNoticeActivity.this,AdminNoticeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            editNoticeImage.setImageURI(ImageUri);
        }

    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }

}
