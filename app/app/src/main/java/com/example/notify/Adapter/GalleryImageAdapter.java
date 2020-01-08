package com.example.sjecnotify.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sjecnotify.Interface.IRecyclerViewClickListener;
import com.example.sjecnotify.Interface.ItemClickListener;
import com.example.sjecnotify.R;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ImageViewHolder>
{


    Context context;
    String[] urllist;
    IRecyclerViewClickListener clickListener;

    public GalleryImageAdapter(Context context,String[] urllist,IRecyclerViewClickListener clickListener)
    {
        this.context=context;
        this.urllist=urllist;
        this.clickListener=clickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item,parent,false);

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String currentImage=urllist[position];
        ImageView imageView=holder.imageView;
        final ProgressBar progressBar=holder.progressBar;

        Glide.with(context).load(currentImage)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ImageViewHolder extends  RecyclerView.ViewHolder implements  View.OnClickListener{

        ImageView imageView;
        ProgressBar progressBar;

        public  ImageViewHolder(View itemview)
        {
            super(itemview);
            imageView=(ImageView)itemview.findViewById(R.id.imageView);
            progressBar=(ProgressBar)itemview.findViewById(R.id.progBar);
            itemview.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v,getAdapterPosition());
        }
    }
}
