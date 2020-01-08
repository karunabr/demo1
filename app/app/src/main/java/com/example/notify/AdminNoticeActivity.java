package com.example.sjecnotify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sjecnotify.Model.Notice;
import com.example.sjecnotify.ViewHolder.NoticeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class AdminNoticeActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    RecyclerView.LayoutManager layoutManager;
    private ImageView back;
    FloatingActionButton fabGallery,fabNotice;

    private String currentTitle,currentImage;

    Animation FabOpen,FabClose,FabClockwise,FabAntiClockWise;
    boolean isOpen=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notice);
        if(!isConnected(AdminNoticeActivity.this))
            buildDialog(AdminNoticeActivity.this).show();

        else {

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Notice");

            fabGallery = findViewById(R.id.fab_gallery);
            fabNotice = findViewById(R.id.fab_notice);
            mBlogList = findViewById(R.id.admin_notice_recycle);
            mBlogList.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            mBlogList.setLayoutManager(layoutManager);
            FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
            FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
            FabClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
            FabAntiClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
            back = (ImageView) findViewById(R.id.backimage);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AdminNoticeActivity.this, UserActivity.class));
                }
            });

            final FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isOpen) {
                        fabGallery.startAnimation(FabClose);
                        fabNotice.startAnimation(FabClose);
                        fab.startAnimation(FabAntiClockWise);
                        fabGallery.setClickable(false);
                        fabNotice.setClickable(false);
                        isOpen = false;
                    } else {
                        fabGallery.startAnimation(FabOpen);
                        fabNotice.startAnimation(FabOpen);
                        fab.startAnimation(FabClockwise);
                        fabGallery.setClickable(true);
                        fabNotice.setClickable(true);
                        isOpen = true;
                    }
                }


            });

            fabNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AdminNoticeActivity.this, NewNoticeActivity.class));
                }
            });

            fabGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AdminNoticeActivity.this, BusTimeActivity.class));
                }
            });


        }

    }




    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Notice> options=new FirebaseRecyclerOptions.Builder<Notice>().setQuery(mDatabase,Notice.class).build();
        FirebaseRecyclerAdapter<Notice, NoticeViewHolder> adapter=new FirebaseRecyclerAdapter<Notice, NoticeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NoticeViewHolder holder, final int position, @NonNull final Notice model) {

                holder.txtNoticeTitle.setText(model.getTitle());
                holder.txtDate.setText(model.getDate());
                Picasso.get().load(model.getImage()).into(holder.adminImage);

                holder.editNotice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminNoticeActivity.this, EditNoticeActivity.class);
                        intent.putExtra("date", model.getDate());
                        startActivity(intent);
                    }
                });







                holder.deleteNotice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        currentTitle=getItem(position).getTitle();
                        currentImage=getItem(position).getImage();


                        AlertDialog.Builder builder=new AlertDialog.Builder(AdminNoticeActivity.this);
                        builder.setTitle("Delete");
                        builder.setMessage("Are you sure you want to delete this notice ?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Query mquery=mDatabase.orderByChild("title").equalTo(currentTitle);
                                mquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds:dataSnapshot.getChildren())
                                        {
                                            ds.getRef().removeValue();

                                            Toast.makeText(AdminNoticeActivity.this, "Notice Deleted..", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {
                                        Toast.makeText(AdminNoticeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                StorageReference mPictureRef= FirebaseStorage.getInstance().getReferenceFromUrl(currentImage);
                                mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AdminNoticeActivity.this, "Image Deleted..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(AdminNoticeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();


                    }

                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            Intent intent = new Intent(AdminNoticeActivity.this, UserNoticeViewActivity.class);
                       intent.putExtra("date", model.getDate());
                            startActivity(intent);



                    }
                });


            }

            @NonNull
            @Override
            public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_notice_layout, parent, false);
                NoticeViewHolder holder = new NoticeViewHolder(view);
                return holder;


            }


        };
        mBlogList.setAdapter(adapter);
        adapter.startListening();
    }

    private void showDeleteDialog(final String currentTitle, final String currentImage)
    {

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

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(c);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(AdminNoticeActivity.this,UserActivity.class));
    }
}
