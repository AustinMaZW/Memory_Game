package com.example.memorygame;

import android.content.Context;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameViewAdaptor extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Icon> icons = null;
    private Map<Integer, Icon> answer = new HashMap<>();
    public static final String DERP = "https://i.scdn.co/image/ab67616d0000b2730ef79e0bae0c61a52e68b238";
    private Map<Integer, ImageView> imageViewMap;


    public GameViewAdaptor(Context context, List<Icon> icons) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.icons = icons;
        this.imageViewMap = new HashMap<>();
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int position) {
        return this.imageViewMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = this.mLayoutInflater.inflate(R.layout.layout_grid, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_grid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(DERP).into(holder.imageView);
        this.imageViewMap.put(position, holder.imageView);
        this.answer.put(position, this.icons.get(position));
        return convertView;
    }

    public class ViewHolder {
        public ImageView imageView;

    }
}
