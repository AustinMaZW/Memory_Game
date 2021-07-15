package com.example.memorygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.GridLayout;

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
        backImage = BitmapFactory.decodeResource(getResources(), R.drawable.logo_transparent);
        int scale = (int) getResources().getDisplayMetrics().density *150;
        backImage = Bitmap.createScaledBitmap(backImage, scale, scale,true);

        //setting default question mark
//        setImageBitmap(backImage);

        //manipulating child elements of the grid layout which is parent
//        GridLayout.LayoutParams tempParams = new GridLayout.LayoutParams(GridLayout.spec(r), GridLayout.spec(c));
        //controlling the dimensions
//        tempParams.width = (int) getResources().getDisplayMetrics().density * 150;
//        tempParams.height = (int) getResources().getDisplayMetrics().density * 150;
//        setLayoutParams(tempParams);
    }

//    public Bitmap getFrontImageId() {
//        return frontImageId;
//    }
//
//    public void setFrontImageId(Bitmap frontImageId) {
//        this.frontImageId = frontImageId;
//    }

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

    //flipping function
//    public void flip(){
//        if(isMatched){
//            return;
//        }
//        if(isFlipped){
//            setImageBitmap(backImage);
//            isFlipped = false;
//        }
//        else {
//            setImageBitmap(frontImage);
//            isFlipped = true;
//        }
//    }
}
