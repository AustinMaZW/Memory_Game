package com.example.memorygame;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GameAdapter extends BaseAdapter {

    Context mContext;
    private GameCard[] mGameCards;

    public GameAdapter(Context applicationContext, GameCard[] gameCards) {
        mContext = applicationContext;
        mGameCards = gameCards;
    }

    @Override
    public int getCount() {
        return mGameCards.length;
    }

    @Override
    public Object getItem(int i) {
        return mGameCards[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;

        if (view != null) {
            imageView = (ImageView) view;
        }
        else {
            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(0, 0, 0, 0);
        }

        if (!mGameCards[i].isFlipped())
            imageView.setImageBitmap(mGameCards[i].getBackImage());
        else
            imageView.setImageBitmap(mGameCards[i].getFrontImage());

        imageView.setId(i);

        return imageView;
    }

    public void setmGameCards(GameCard[] cards) {
        mGameCards = cards;
    }
}
