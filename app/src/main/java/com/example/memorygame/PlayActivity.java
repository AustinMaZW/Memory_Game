package com.example.memorygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {
    String filepath = "game_cards";
    String filename = "image";
    Chronometer timerView;
    TextView movesView;
    int numberOfButtons;
    GameCard[] buttons;
    Bitmap[] buttonGraphics;
    int[] buttonGraphicLocations;
    private GameCard button;
    private GameCard selectedButton1;
    private GameCard selectedButton2;
    private boolean isBusy;
    private int matchesCounter = 0;
    private int totalClicks = 0;
    private Handler handler;
    private boolean startCount = true;
    private  boolean isFalse = false;
    private ProgressBar pgbar;
    private TextView progressInfo;
    private Intent intent;

//    private long secondElapsed;
//
//    private Runnable runnable = null;
//    private Handler handler;
//    private boolean mStarted;

    List<Bitmap> gameCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        timerView = findViewById(R.id.timerView);
        movesView = findViewById(R.id.movesView);
        pgbar = findViewById(R.id.pgbar);
        handler = new Handler();
        progressInfo = findViewById(R.id.progressInfo);
        intent = new Intent(this,MainActivity.class);

        // get images from drawable and save inside app-specific external folder
        // for testing purpose only. will delete after combining with activity1
        saveImages();

        // load images on screen
        showImages();
    }
    private void setStartButton(Button start){

    }

    void saveImages(){
        for(int i = 0; i < 6; i++){
            File file = new File(getExternalFilesDir(null), filepath + "/" + filename + i);
            if(file.exists())
                file.delete();
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException(
                        "Couldn't create dir: " + parent);
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int drawableID = this.getResources().getIdentifier(filename + i, "drawable", getPackageName());
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableID);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    void showImages() {
        GridLayout grid = findViewById(R.id.gridLayout);
        int numberOfC = grid.getColumnCount();
        int numberOfR = grid.getRowCount();
        numberOfButtons = numberOfC * numberOfR;

        buttons = new GameCard[numberOfButtons];
        buttonGraphics = new Bitmap[numberOfButtons / 2];
        loadImages();

        buttonGraphicLocations = new int[numberOfButtons];
        setImageOnView(numberOfR, numberOfC);
    }

    void loadImages(){
        //gameCards = new ArrayList<Bitmap>();
        for(int i = 0; i < 6; i++){
            File file = new File(getExternalFilesDir(null), filepath + "/" + filename + i);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                buttonGraphics[i] = bitmap;
                //gameCards.add(bitmap);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    private void shuffle(){
        Random rand = new Random();
        for(int i = 0; i < numberOfButtons; i++){
            buttonGraphicLocations[i] = i % (numberOfButtons / 2);
        }
        for(int i = 0; i < numberOfButtons; i++){
            int temp = buttonGraphicLocations[i];
            int swap = rand.nextInt(12);
            buttonGraphicLocations[i] = buttonGraphicLocations[swap];
            buttonGraphicLocations[swap] = temp;
        }
    }
    private void setImageOnView(int numberOfR, int numberOfC) {
        shuffle();
        GridLayout grid = findViewById(R.id.gridLayout);
        for(int row = 0; row < numberOfR; row++){
            for(int column = 0; column < numberOfC; column++){
                GameCard tempButton = new GameCard(this, row, column, buttonGraphics[buttonGraphicLocations[row * numberOfC + column]]);
                tempButton.setId(View.generateViewId());
                Log.i("msg", String.valueOf(tempButton.getId()));
                tempButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isFalse)
                            return;
                        flip(view);
                        pgbar.setProgress(matchesCounter);
                        progressInfo.setText(String.format("%s / %s",matchesCounter,6));

                        if(startCount){
                            setTimerView(timerView);
                            startCount = false;
                        }
                    }
                });

                grid.addView(tempButton);

                //saving the reference to button
                buttons[row*numberOfC + column] = tempButton;
            }
        }
    }

    private void flip(View view) {
        button = (GameCard) view;
        if(isBusy){
            return;
        }
        if(selectedButton1 == null){
            selectedButton1 = button;
            selectedButton1.flip();
        }
        if(selectedButton1.getId() == button.getId()){
            return;
        }
        if(selectedButton1.getFrontImageId() == button.getFrontImageId()){
            button.flip();

            button.setMatched(true);

            selectedButton1.setEnabled(false);
            button.setEnabled(false);

            selectedButton1 = null;

            matchesCounter++;
            totalClicks++;
            //updating match progress
            if(matchesCounter == 6){
                movesView.setText("Congrats!");
                timerView.stop();
                startActivity(intent);
            }
            else{
                movesView.setText("Moves: " + totalClicks);
            }
            return;
        }
        else {
            selectedButton2 = button;
            selectedButton2.flip();
            isBusy = true;
            totalClicks++;
            movesView.setText("Moves: " + totalClicks);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedButton1.flip();
                    selectedButton2.flip();
                    selectedButton1 = null;
                    selectedButton2 = null;
                    isBusy = false;
//                    toast.cancel();
                }
            }, 500);
        }
    }

    /*private void showImage() {
        String uri = "drawable/icon";

        // int imageResource = R.drawable.icon;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());

//        ImageView imageView = (ImageView) findViewById(R.id.myImageView);
//        Drawable image = getResources().getDrawable(imageResource);
//        imageView.setImageDrawable(image);

//        int imageResource = R.drawable.icon;
//        Drawable image = getResources().getDrawable(imageResource);

//        ImageView iv = new ImageView(this);
//        int drawableID = this.getResources().getIdentifier("bitmap0", "drawable", getPackageName());
//        iv.setImageResource(drawableID);
    }*/
    private void setTimerView(Chronometer timerView){
        timerView.setBase(SystemClock.elapsedRealtime());
        timerView.setFormat("%s");
        timerView.start();
        timerView.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(SystemClock.elapsedRealtime() - timerView.getBase() >= 30000){
                    timerView.stop();
                    isFalse=true;
                    movesView.setText("You Lose!");
                    matchesCounter =6;
                }
            }
        });
    }
}