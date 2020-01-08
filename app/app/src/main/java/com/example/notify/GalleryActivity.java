package com.example.sjecnotify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity {

    private TextView validate;
    private ImageView galleryImage;
    private Button upload;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductRef;
    private String downloadImageUrl,productRandomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        validate=findViewById(R.id.gallery_validate);
        galleryImage=findViewById(R.id.gallery_image);
        validate=findViewById(R.id.upload_gallery_image);
        upload=findViewById(R.id.upload_gallery_image);

        loadingBar=new ProgressDialog(this);
        ProductImagesRef= FirebaseStorage.getInstance().getReference().child("Gallery Image");
        ProductRef= FirebaseDatabase.getInstance().getReference().child("Gallery");


        galleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImageUri == null)
                {
                    validate.setVisibility(View.VISIBLE);
                    validate.setText("Select an image.");
                }
                else {
                    storeImageInformation();
                }
            }
        });

    }

    private void storeImageInformation()
    {
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please wait, while we are adding the new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();





        final StorageReference filePath=ProductImagesRef.child(ImageUri.getLastPathSegment()+ productRandomKey+".jpg");
        final UploadTask uploadTask=filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message=e.toString();
                Toast.makeText(GalleryActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(GalleryActivity.this, "Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(GalleryActivity.this, "Got the image url Successfully....", Toast.LENGTH_SHORT).show();

                            SaveGalleryTODatabse();

                        }
                    }
                });
            }
        });
    }

    private void SaveGalleryTODatabse()
    {
        HashMap<String,Object> productMap=new HashMap<>();

        productMap.put("image",downloadImageUrl);

        ProductRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Intent intent=new Intent(GalleryActivity.this, AdminPanelActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(GalleryActivity.this, "Notice uploaded successfully..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GalleryActivity.this,AdminNoticeActivity.class));
                }
                else
                {
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(GalleryActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void openGallery() {

        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            galleryImage.setImageURI(ImageUri);
        }

    }
}
