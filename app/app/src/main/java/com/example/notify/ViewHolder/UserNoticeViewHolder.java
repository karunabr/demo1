package com.example.sjecnotify.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sjecnotify.Interface.ItemClickListener;
import com.example.sjecnotify.R;

public class UserNoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtNoticeTitle,txtDate;
    public ImageView noticeImage;

    public ItemClickListener listner;
    private Context mContext;



    public UserNoticeViewHolder(@NonNull View itemView)
    {

        super(itemView);

        txtNoticeTitle=(TextView) itemView.findViewById(R.id.user_notice_title);
        txtDate=(TextView)itemView.findViewById(R.id.user_notice_date);

        noticeImage=(ImageView)itemView.findViewById(R.id.user_image_view);






    }




    public void setItemClickListener(ItemClickListener listner)
    {
        this.listner=listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view,getAdapterPosition(),false);
    }
}
