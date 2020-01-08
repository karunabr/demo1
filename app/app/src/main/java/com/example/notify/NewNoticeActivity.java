package com.example.sjecnotify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sjecnotify.Model.Notice;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class NewNoticeActivity extends AppCompatActivity {

    private EditText noticeTitle,noticeDate,noticeDescription;
    private Button uploadBtn;
    private ImageView imageUpload,back;
    private TextView textvalidate;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private RelativeLayout r1;
    private Context mContext;
    private String saveCurrentDate,saveCurrentTime;

    String tkn;

    private StorageReference ProductImagesRef;
    private DatabaseReference ProductRef;
    private String productRandomKey,downloadImageUrl,title,date,description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notice);
        if(!isConnected(NewNoticeActivity.this))
            buildDialog(NewNoticeActivity.this).show();

        else {
            Calendar calendar=Calendar.getInstance();

            SimpleDateFormat currentDate=new SimpleDateFormat("MM dd, yyyy");
            saveCurrentDate=currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime=currentTime.format(calendar.getTime());

            noticeTitle = findViewById(R.id.notice_title);
            noticeDate = findViewById(R.id.notice_date);
            noticeDescription = findViewById(R.id.notice_description);
            uploadBtn = findViewById(R.id.notice_upload_btn);
            textvalidate = findViewById(R.id.notice_upload_validtion);
            imageUpload = findViewById(R.id.notice_image);
            r1 = findViewById(R.id.l1);

            ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Notice Image");
            ProductRef = FirebaseDatabase.getInstance().getReference().child("Notice");
            back = findViewById(R.id.backimage);
            noticeDate.setEnabled(false);
            tkn= FirebaseInstanceId.getInstance().getToken();


            noticeDate.setText(saveCurrentDate+saveCurrentTime);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(NewNoticeActivity.this, AdminNoticeActivity.class));
                }
            });

            imageUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });

            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadBtn.setEnabled(false);
                    validateNotice();
                }
            });
        }
    }

    private void validateNotice()
    {
       title=noticeTitle.getText().toString();
        date=noticeDate.getText().toString();
        description=noticeDescription.getText().toString();

        if(ImageUri == null)
        {
            textvalidate.setVisibility(View.VISIBLE);
            textvalidate.setText("Image of notice document is mandatory");
            uploadBtn.setEnabled(true);
        }
        else if(TextUtils.isEmpty(title))
        {
            textvalidate.setVisibility(View.VISIBLE);
            textvalidate.setText("Notice title is mandatory");
            uploadBtn.setEnabled(true);
        }
        else if(TextUtils.isEmpty(date))
        {
            textvalidate.setVisibility(View.VISIBLE);
            textvalidate.setText("Notice is mandatory");
            uploadBtn.setEnabled(true);
        }
        else if(TextUtils.isEmpty(description))
        {
            uploadBtn.setEnabled(true);
            textvalidate.setVisibility(View.VISIBLE);
            textvalidate.setText("Notice description is mandatory");
        }
        else
        {
            StoreNoticeInformation();
        }
    }

    private void StoreNoticeInformation()
    {


        final StorageReference filePath=ProductImagesRef.child(ImageUri.getLastPathSegment()+ productRandomKey+".jpg");
        final UploadTask uploadTask=filePath.putFile(ImageUri);

        r1.setVisibility(View.VISIBLE);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message=e.toString();
                Toast.makeText(NewNoticeActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(NewNoticeActivity.this, "Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(NewNoticeActivity.this, "Got the product image url Successfully....", Toast.LENGTH_SHORT).show();

                            SaveNoticeInfoTODatabse();

                        }
                    }
                });
            }
        });
    }

    private void SaveNoticeInfoTODatabse()
    {
        HashMap<String,Object> productMap=new HashMap<>();

        productMap.put("title",title);
        productMap.put("date",date);
        productMap.put("description",description);
        productMap.put("image",downloadImageUrl);

        ProductRef.child(date).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(NewNoticeActivity.this, "Notice uploaded successfully..", Toast.LENGTH_SHORT).show();
                    r1.setVisibility(View.GONE);
                    new Notify().execute();

                    ProductRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children=dataSnapshot.getChildren();

                            for(DataSnapshot child:children)
                            {
                                ShowNotification();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    Intent intent=new Intent(NewNoticeActivity.this, AdminNoticeActivity.class);
                    startActivity(intent);



                }
                else
                {

                    String message=task.getException().toString();
                    Toast.makeText(NewNoticeActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                    r1.setVisibility(View.GONE);
                    Intent intent=new Intent(NewNoticeActivity.this, AdminNoticeActivity.class);
                    startActivity(intent);
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
            imageUpload.setImageURI(ImageUri);
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

    public android.app.AlertDialog.Builder buildDialog(Context c) {

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(c);
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

    public class Notify extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {


            try {

                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization","key=AIzaSyATNSgmZJYYkM0xi1cjbTB3gz_MALcioqI");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", tkn);


                JSONObject info = new JSONObject();
                info.put("title", "TechnoWeb");   // Notification title
                info.put("body", "Hello Test notification"); // Notification body

                json.put("notification", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            }
            catch (Exception e)
            {
                Log.d("Error",""+e);
            }


            return null;
        }
    }

    public void ShowNotification()
    {
        NotificationCompat.Builder nbr=(NotificationCompat.Builder)new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_menu_camera)
                .setContentTitle("SJEC Notify")
                .setContentText("New Notice Has Been Uploaded");
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,nbr.build());

    }

}

