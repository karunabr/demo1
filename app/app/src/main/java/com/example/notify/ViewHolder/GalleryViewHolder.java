package com.example.sjecnotify.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sjecnotify.Interface.ItemClickListener;
import com.example.sjecnotify.R;

public class GalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public ImageView galleryImage;

    public ItemClickListener listner;
    private Context mContext;

    public GalleryViewHolder(@NonNull View itemView)
    {

        super(itemView);


        galleryImage=(ImageView)itemView.findViewById(R.id.gallery_image_view);






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
