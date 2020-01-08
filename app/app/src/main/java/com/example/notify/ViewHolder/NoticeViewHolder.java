package com.example.sjecnotify.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sjecnotify.Interface.ItemClickListener;
import com.example.sjecnotify.R;

public class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtNoticeTitle,txtDate;
    public ImageView editNotice,deleteNotice,adminImage;
    public ItemClickListener listner;

    public NoticeViewHolder(@NonNull View itemView)
    {

        super(itemView);

        txtNoticeTitle=(TextView) itemView.findViewById(R.id.admin_notice_title);
        txtDate=(TextView)itemView.findViewById(R.id.admin_notice_date);
        editNotice=(ImageView) itemView.findViewById(R.id.edit_notice);
        deleteNotice=(ImageView)itemView.findViewById(R.id.delete_notice);
        adminImage=(ImageView)itemView.findViewById(R.id.admin_notice_image);


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
