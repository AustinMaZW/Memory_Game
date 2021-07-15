package com.example.memorygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    static final int DOUBLE = 2;
    String[] filenames;
    //TextView timerView;
    //TextView movesView;
    GridView grid;
    Chronometer timerView;
    TextView movesView;
    GameAdapter adapter;
    int numberOfCard;
    // GameCard objects to bind to view using Adapter
    GameCard[] cards;
    // selected images to play
    Bitmap[] images;
    // store which image to be shown at position i (i = index of array)
    int[] imagesLayoutPlan;

    private GameCard prevCard;

    private boolean isBusy;
    private int matchesCounter = 0;
    private int totalClicks = 0;
    private Handler handler;

    MediaPlayer bgm;
    private SoundPool soundPool;
    private int match, miss, win, lose;

    private boolean startCount = true;
    private boolean isFalse = false;
    private ProgressBar pgbar;
    private TextView progressInfo;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent mainActivity = getIntent();
        filenames = mainActivity.getStringArrayExtra("imgs");
        timerView = findViewById(R.id.timerView);
        movesView = findViewById(R.id.movesView);
        pgbar = findViewById(R.id.pgbar);
        progressInfo = findViewById(R.id.progressInfo);
        intent = new Intent(this,MainActivity.class);
        handler = new Handler();

        numberOfCard = filenames.length * DOUBLE;

        cards = new GameCard[numberOfCard];
        adapter = new GameAdapter(this, cards);

        grid = findViewById(R.id.gridView);
        grid.setAdapter(adapter);

        images = new Bitmap[filenames.length];
        loadImages();

        imagesLayoutPlan = new int[numberOfCard];
        shuffleCards();

        setImageOnView();

        bgm = MediaPlayer.create(getApplicationContext(),R.raw.bgm);
        bgm.start();
        bgm.setLooping(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        }
        match = soundPool.load(this, R.raw.match, 1);
        miss = soundPool.load(this, R.raw.miss, 1);
        win = soundPool.load(this, R.raw.win, 1);
        lose = soundPool.load(this, R.raw.lose, 1);
    }

    void loadImages(){
        for(int i = 0; i < filenames.length; i++){
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filenames[i]);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                int scale = (int) getResources().getDisplayMetrics().density *150; // J
                bitmap = Bitmap.createScaledBitmap(bitmap, scale, scale,true); // J
                images[i] = bitmap;
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void shuffleCards(){
        Random rand = new Random();
        for(int i = 0; i < numberOfCard; i++){
            imagesLayoutPlan[i] = i % (numberOfCard / 2);
        }
        for(int i = 0; i < numberOfCard; i++){
            int rndPosition = rand.nextInt(12);
            // swap
            int value = imagesLayoutPlan[i];
            imagesLayoutPlan[i] = imagesLayoutPlan[rndPosition];
            imagesLayoutPlan[rndPosition] = value;
        }
    }

    private void setImageOnView() {
        //GridLayout grid = findViewById(R.id.gridLayout);
        for(int i = 0; i < numberOfCard; i++){
            GameCard gameCard = new GameCard(this, i, images[imagesLayoutPlan[i]]);
            gameCard.setId(View.generateViewId());

//            gameCard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(isFalse)
//                        return;
//                    flip(view);
//                    pgbar.setProgress(matchesCounter);
//                    progressInfo.setText(String.format("%s / %s",matchesCounter,6));
//
//                    if(startCount){
//                        setTimerView(timerView);
//                        startCount = false;
//                    }
//                }
//            });

            //grid.addView(tempButton);

            //saving the reference to button
            cards[i] = gameCard;
        }

        adapter.setmGameCards(cards);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isFalse)
                    return;

                checkCardIsMatched(view, position);

                pgbar.setProgress(matchesCounter);
                progressInfo.setText(String.format("%s / %s",matchesCounter,6));

                if(startCount){
                    setTimerView(timerView);
                    startCount = false;
                }
            }
        });
    }

    private void checkCardIsMatched(View view, int position) {
        ImageView currentView = (ImageView) view;
        GameCard currentCard = cards[position];
        if(isBusy || currentCard.isMatched()) {
            return;
        }
        if(prevCard == null){
            prevCard = cards[position];
            flip(prevCard, currentView);
        }

        ImageView prevView = grid.findViewById(prevCard.getPositionOnScreen());
        if(prevCard.getId() == currentCard.getId()){
            return;
        }
        if(prevCard.getFrontImage() == currentCard.getFrontImage()){
            flip(currentCard, currentView);

            prevCard.setMatched(true);
            currentCard.setMatched(true);
//            currentView.setEnabled(false);
//            prevView.setEnabled(false);

            soundPool.play(match,1,1,0,0,1);

            prevCard = null;

            matchesCounter++;
            totalClicks++;
            //updating match progress
            if(matchesCounter == 6){
                movesView.setText("Congrats!");
                timerView.stop();
                bgm.stop();
                soundPool.play(win,1,1,0,0,1);
                //startActivity(intent);
                //can add timer stop
            }
            else{
                movesView.setText("Moves: " + totalClicks);
            }
            return;
        }
        else {
            flip(currentCard, currentView);
            soundPool.play(miss,1,1,0,0,1);
            isBusy = true;
            totalClicks++;
            movesView.setText("Moves: " + totalClicks);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flip(prevCard, prevView);
                    flip(currentCard, currentView);
                    prevCard = null;
                    isBusy = false;
                }
            }, 500);
        }
    }

    public void flip(GameCard card, ImageView view){
        if(card.isMatched()){
            return;
        }
        if(card.isFlipped()){
            view.setImageBitmap(card.getBackImage());
            card.setFlipped(false);
        }
        else {
            view.setImageBitmap(card.getFrontImage());
            card.setFlipped(true);
        }
    }

    private void setTimerView(Chronometer timerView){
        timerView.setBase(SystemClock.elapsedRealtime() + 30000);
        timerView.setCountDown(true);
        timerView.setFormat("%s");
        timerView.start();

        timerView.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(SystemClock.elapsedRealtime() - timerView.getBase() > 0){
                    timerView.stop();
                    isFalse=true;
                    movesView.setText("You Lose!");
                    bgm.stop();
                    soundPool.play(lose,1,1,0,0,1);
                    matchesCounter =6;
                }
            }
        });
    }
}