package com.example.sjecnotify.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sjecnotify.Interface.ItemClickListener;
import com.example.sjecnotify.R;

public class BusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView busTime,busNum,busStops,busDriver,busDriverNum;

    public ItemClickListener listner;




    public BusViewHolder(@NonNull View itemView)
    {

        super(itemView);

        busTime=(TextView) itemView.findViewById(R.id.time);
        busNum=(TextView) itemView.findViewById(R.id.bus_number);
        busStops=(TextView) itemView.findViewById(R.id.places);
        busDriver=(TextView) itemView.findViewById(R.id.driver_name);
        busDriverNum=(TextView) itemView.findViewById(R.id.phone);

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
