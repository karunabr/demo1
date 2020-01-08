package com.example.sjecnotify.Interface;

import android.view.View;

public interface ItemClickListener
{
    void onClick(View view, int position, boolean isLongClick);

    void onClick(View v, int adapterPosition);
}
