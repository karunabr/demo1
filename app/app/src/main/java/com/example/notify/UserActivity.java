package com.example.sjecnotify;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.sjecnotify.Model.Notice;
import com.example.sjecnotify.ViewHolder.NoticeViewHolder;
import com.example.sjecnotify.ViewHolder.UserNoticeViewHolder;
import com.example.sjecnotify.ui.gallery.GalleryFragment;
import com.example.sjecnotify.ui.home.HomeFragment;
import com.example.sjecnotify.ui.slideshow.SlideshowFragment;
import com.example.sjecnotify.ui.tools.ToolsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;

public class UserActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Notice");

        mDatabase.addValueEventListener(new ValueEventListener() {
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



        mBlogList=(RecyclerView)findViewById(R.id. user_notice_recycle_content);
        mBlogList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mBlogList.setLayoutManager(layoutManager);
        mBlogList.setLayoutManager(layoutManager);


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int itemId=destination.getId();
                Fragment fragment=null;
                switch (itemId)
                {
                    case R.id.nav_home:
                        fragment=new HomeFragment();
                        break;
                    case R.id.nav_gallery:
                        fragment=new GalleryFragment();
                        break;
                    case R.id.nav_slideshow:
                        fragment=new SlideshowFragment();
                        break;
                    case R.id.nav_tools:
                        fragment=new ToolsFragment();
                    break;
                }


            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful())
                        {
                            String token=task.getResult().getToken();
                            Log.d("Token","Fcm Token :"+token);
                        }
                    }
                });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerOptions<Notice> options=new FirebaseRecyclerOptions.Builder<Notice>().setQuery(mDatabase,Notice.class).build();
//        FirebaseRecyclerAdapter<Notice, UserNoticeViewHolder> adapter=new FirebaseRecyclerAdapter<Notice, UserNoticeViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull UserNoticeViewHolder holder, int position, @NonNull final Notice model) {
//
//                holder.txtNoticeTitle.setText(model.getTitle());
//                holder.txtDate.setText(model.getDate());
//                Picasso.get().load(model.getImage()).into(holder.noticeImage);
//
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent intent = new Intent(UserActivity.this, AdminPanelActivity.class);
////                            intent.putExtra("pid", model.getPid());
//                        startActivity(intent);
//
//
//
//                    }
//                });
//
//
//
//
//
//            }
//
//            @NonNull
//            @Override
//            public UserNoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_notice_layout,parent,false);
//                UserNoticeViewHolder holder=new UserNoticeViewHolder(view);
//                return holder;
//            }
//        };
//        mBlogList.setAdapter(adapter);
//        adapter.startListening();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void ShowNotification()
    {
        Intent intent=new Intent(UserActivity.this,SplashScreenActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder nbr=(NotificationCompat.Builder)new NotificationCompat.Builder(this)
                .setContentTitle("SJEC Notify")
                .setContentText("New Notice Has Been Uploaded")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.sjec_full_logo);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,nbr.build());

    }

}
