package com.example.memorygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class SelectImageAdaptor extends BaseAdapter {
    private Context mContext;
    private Bitmap[] imgCollection = new Bitmap[20];
    public SelectImageAdaptor(Context context, Bitmap[] imgsBmps){
        mContext = context;
        imgCollection = imgsBmps;
    }

    @Override
    public int getCount(){
        return 20;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(230, 230));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        if (imgCollection[position] == null)
            imageView.setImageResource(R.drawable.pinterest_profile_image);
        else
            imageView.setImageBitmap(imgCollection[position]);  //if there is a downloaded bitmap

        return imageView;
    }

    public void changeImageBitmap (Bitmap bitmap, int position){
        imgCollection[position] = bitmap;
        notifyDataSetChanged();
    }

}
