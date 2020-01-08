package com.example.sjecnotify.ui.gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sjecnotify.Model.Bus;
import com.example.sjecnotify.Model.Notice;
import com.example.sjecnotify.R;
import com.example.sjecnotify.UserNoticeViewActivity;
import com.example.sjecnotify.ViewHolder.BusViewHolder;
import com.example.sjecnotify.ViewHolder.UserNoticeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {


    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private RelativeLayout progrssBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        if(!isConnected(getActivity()))
            buildDialog(getActivity()).show();

        else {


            mBlogList = (RecyclerView) root.findViewById(R.id.recyclerView);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Bus");
            final FragmentActivity c = getActivity();
            progrssBar = (RelativeLayout) root.findViewById(R.id.l1);

            progrssBar.setVisibility(View.VISIBLE);


            mBlogList.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(c);
            mBlogList.setLayoutManager(layoutManager);
            mBlogList.setLayoutManager(layoutManager);

            if (container != null) {
                container.removeAllViews();
            }

            final FirebaseRecyclerOptions<Bus> options=new FirebaseRecyclerOptions.Builder<Bus>().setQuery(mDatabase,Bus.class).build();
            final FirebaseRecyclerAdapter<Bus, BusViewHolder> adapter=new FirebaseRecyclerAdapter<Bus, BusViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final BusViewHolder holder, final int position, @NonNull final Bus model) {

                    progrssBar.setVisibility(View.GONE);

                    holder.busTime.setText("Time : "+model.getTime());
                    holder.busNum.setText("Bus Number : "+model.getBusnumber());
                    holder.busStops.setText(model.getStops());
                    holder.busDriver.setText("Driver Name : "+model.getDriverName());
                    holder.busDriverNum.setText(model.getDriverNumber());


                }

                @NonNull
                @Override
                public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_layout, parent, false);
                    BusViewHolder holder = new BusViewHolder(view);
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

