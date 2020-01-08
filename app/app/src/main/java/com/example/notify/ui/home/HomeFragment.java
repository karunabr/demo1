package com.example.sjecnotify.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.example.sjecnotify.AdminNoticeActivity;
import com.example.sjecnotify.AdminPanelActivity;
import com.example.sjecnotify.Database.DatabaseHelper;
import com.example.sjecnotify.EditNoticeActivity;
import com.example.sjecnotify.MainActivity;
import com.example.sjecnotify.Model.Notice;
import com.example.sjecnotify.R;
import com.example.sjecnotify.UserNoticeViewActivity;
import com.example.sjecnotify.ViewHolder.UserNoticeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    RecyclerView.LayoutManager layoutManager;
    private boolean check=false;
    DatabaseHelper localDb;
    private RelativeLayout progrssBar;
    private Toolbar mActionBarToolbar;


    private String currentTitle,currentImage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        if(!isConnected(getActivity()))
            buildDialog(getActivity()).show();

        else {


            mDatabase = FirebaseDatabase.getInstance().getReference().child("Notice");
            final FragmentActivity c = getActivity();

            localDb = new DatabaseHelper(getActivity());

            progrssBar = (RelativeLayout) root.findViewById(R.id.l1);


            progrssBar.setVisibility(View.VISIBLE);


            mBlogList = (RecyclerView) root.findViewById(R.id.user_notice_recycle);
            mBlogList.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(c);
            mBlogList.setLayoutManager(layoutManager);
            mBlogList.setLayoutManager(layoutManager);

            if (container != null) {
                container.removeAllViews();
            }

            final FirebaseRecyclerOptions<Notice> options=new FirebaseRecyclerOptions.Builder<Notice>().setQuery(mDatabase,Notice.class).build();
            final FirebaseRecyclerAdapter<Notice, UserNoticeViewHolder> adapter=new FirebaseRecyclerAdapter<Notice, UserNoticeViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final UserNoticeViewHolder holder, final int position, @NonNull final Notice model) {

                    progrssBar.setVisibility(View.GONE);

                    holder.txtNoticeTitle.setText(model.getTitle());
                    holder.txtDate.setText(model.getDate());
                    Picasso.get().load(model.getImage()).into(holder.noticeImage);




                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent intent = new Intent(getActivity(), UserNoticeViewActivity.class);
                            intent.putExtra("date", model.getDate());
                            startActivity(intent);



                        }
                    });






                }

                @NonNull
                @Override
                public UserNoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_notice_layout, parent, false);
                    UserNoticeViewHolder holder = new UserNoticeViewHolder(view);
                    return holder;


                }


            };
            mBlogList.setAdapter(adapter);
            adapter.startListening();
        }
        return root;

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

                getActivity().finish();
            }
        });

        return builder;
    }
}