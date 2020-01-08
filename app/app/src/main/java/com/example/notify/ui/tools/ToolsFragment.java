package com.example.sjecnotify.ui.tools;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sjecnotify.MainActivity;
import com.example.sjecnotify.R;

public class ToolsFragment extends Fragment {

    private ImageView facebook,youtube,twitter,admin;
    private TextView design;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        facebook=(ImageView)root.findViewById(R.id.facebook);
        youtube=(ImageView)root.findViewById(R.id.youtube);

        twitter=(ImageView)root.findViewById(R.id.twitter);

        admin=(ImageView)root.findViewById(R.id.admin);




        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/SJECMangaluru/"));
                startActivity(browserIntent);
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCiDmIMhMloUuaJ2cK0pBnDg"));
                startActivity(browserIntent);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/sjecmangalore?lang=en"));
                startActivity(browserIntent);
            }
        });

        admin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                return true;
            }
        });


        return root;
    }
}