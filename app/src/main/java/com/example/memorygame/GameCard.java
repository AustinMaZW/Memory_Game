package com.example.memorygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.widget.AppCompatImageButton;

public class GameCard extends AppCompatImageButton {

    private  int positionOnScreen;
    private boolean isFlipped = false;
    private boolean isMatched = false;

    private Bitmap frontImage;
    private Bitmap backImage;

    public GameCard(Context context, int position, Bitmap bitmap) {
        super(context);

        this.positionOnScreen = position;
        frontImage = bitmap;
        backImage = BitmapFactory.decodeResource(getResources(), R.drawable.pinterest_profile_image);
        int scale = (int) getResources().getDisplayMetrics().density * 150;
        backImage = Bitmap.createScaledBitmap(backImage, scale, scale,true);
    }

    public int getPositionOnScreen() {
        return positionOnScreen;
    }

    public void setPositionOnScreen(int positionOnScreen) {
        this.positionOnScreen = positionOnScreen;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public Bitmap getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(Bitmap frontImage) {
        this.frontImage = frontImage;
    }

    public Bitmap getBackImage() {
        return backImage;
    }

    public void setBackImage(Bitmap backImage) {
        this.backImage = backImage;
    }
}
