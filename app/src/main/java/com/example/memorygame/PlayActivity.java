package com.example.memorygame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    static final int DOUBLE = 2;

    String[] filenames;
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
    private AlertDialog.Builder dlg;
    private AlertDialog.Builder failDlg;


    int score;
    long timeLeft;
    AlertDialog alertDialog;
    EditText name;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int rank5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
//        setDialog();

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

    void loadImages() {
        for(int i = 0; i < filenames.length; i++) {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filenames[i]);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
//                int scale = (int) getResources().getDisplayMetrics().density * 150;
//                bitmap = Bitmap.createScaledBitmap(bitmap, scale, scale,true);
                images[i] = bitmap;
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void shuffleCards() {
        Random rand = new Random();
        for(int i = 0; i < numberOfCard; i++) {
            imagesLayoutPlan[i] = i % (numberOfCard / 2);
        }
        for(int i = 0; i < numberOfCard; i++) {
            int rndPosition = rand.nextInt(12);
            // swap
            int value = imagesLayoutPlan[i];
            imagesLayoutPlan[i] = imagesLayoutPlan[rndPosition];
            imagesLayoutPlan[rndPosition] = value;
        }
    }

    private void setImageOnView() {
        for(int i = 0; i < numberOfCard; i++) {
            GameCard gameCard = new GameCard(this, i, images[imagesLayoutPlan[i]]);
            gameCard.setId(View.generateViewId());

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
        else {
            ImageView prevView = grid.findViewById(prevCard.getPositionOnScreen());
            if (prevCard.getId() == currentCard.getId()) {
                return;
            }
            if (prevCard.getFrontImage() == currentCard.getFrontImage()) {
                flip(currentCard, currentView);
                soundPool.play(match,0.2f,0.2f,0,0,1);

                prevCard.setMatched(true);
                currentCard.setMatched(true);

                prevCard = null;

                matchesCounter++;
                totalClicks++;
                //updating match progress
                if(matchesCounter == 6){
                    movesView.setText("Congrats!");
                    soundPool.play(win,0.3f,0.3f,0,0,1);
                    timeLeft = (SystemClock.elapsedRealtime() - timerView.getBase())*-1;
                    System.out.println("Time left:" + timeLeft);
                    timerView.stop();
                    calculateScore();
                    bgm.stop();
    //                startActivity(intent);
                    enterHighScore();
//                    dlg.show();
                    //can add timer stop
                }
                else{
                    movesView.setText("Moves: " + totalClicks);
                }

            }
            else {
                flip(currentCard, currentView);
                isBusy = true;
                totalClicks++;
                movesView.setText("Moves: " + totalClicks);
                soundPool.play(miss,0.2f,0.2f,0,0,1);

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
    }

    public void flip(GameCard card, ImageView view) {
        if(card.isMatched()) {
            return;
        }
        if(card.isFlipped()) {
            view.setImageBitmap(card.getBackImage());
            card.setFlipped(false);
        }
        else {
            view.setImageBitmap(card.getFrontImage());
            card.setFlipped(true);
        }
    }

    private void calculateScore() {
        score = (int)timeLeft*10 + (int)1000/totalClicks;
        System.out.println("Score:" + score);
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
                    movesView.setText(R.string.lost);
                    soundPool.play(lose,0.3f,0.3f,0,0,1);
                    bgm.stop();
                    matchesCounter =6;
                    score = -1;
//                    failDlg.show();
                    enterHighScore();
                }
            }
        });
    }

    private void enterHighScore() {
        LayoutInflater inflater = LayoutInflater.from(PlayActivity.this);
        final View view = inflater.inflate(R.layout.dialog_save_score, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(PlayActivity.this);
        dialog.setView(view);
        dialog.setCancelable(false);
        alertDialog = dialog.create();

        TextView dialogGreeting = view.findViewById(R.id.dialogGreeting);
        TextView dialogResult = view.findViewById(R.id.dialogResult);

        final Button leaderboardBtn = view.findViewById(R.id.leaderboardBtn);
        leaderboardBtn.setOnClickListener(this);

        Button playAgainBtn = view.findViewById(R.id.playAgainBtn);
        playAgainBtn.setOnClickListener(this);

        //check high score
        sharedPreferences = getSharedPreferences("leaderboard", 0);
        rank5 = sharedPreferences.getInt("rank5", 0);

        if(score > rank5) {
            //new high score
            dialogGreeting.setText("Congratulations!");
            dialogResult.setText("New high score!");
            TextView scoreView = (TextView) view.findViewById(R.id.score);
            scoreView.setText("You scored " + score + " points!");
        } else if(score > 0) {
            //you won
            dialogGreeting.setText("Congratulations!");
            TextView scoreView = (TextView) view.findViewById(R.id.score);
            scoreView.setText("You scored " + score + " points!");
        } else if(score < 0) {
            //you lost
            dialogGreeting.setText("Aww :(");
            dialogResult.setText("You ran out of time.");
        }

        alertDialog.show();
    }

    public void onClick(View view) {
        if(view.getId() == R.id.leaderboardBtn) {
            saveScore(score);
            alertDialog.dismiss();
            Intent intent = new Intent(this, Leaderboard.class);
            startActivity(intent);
            finish();
        }
        if(view.getId() == R.id.playAgainBtn) {
            saveScore(score);
            alertDialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    void saveScore(int score){
        sharedPreferences = getSharedPreferences("leaderboard",0);
        editor = sharedPreferences.edit();
        editor.putInt("lastScore", score);
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), Leaderboard.class);
        startActivity(intent);
        finish();
    }

//    private void setDialog(){
//        this.dlg = new AlertDialog.Builder(this)
//                .setTitle("Congratulations!")
//                .setMessage("You win!\nDo you want to play again?")
//                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                    }
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent mpIntent  = new Intent(PlayActivity.this, MainPage.class);
//                        startActivity(mpIntent);
//                        //recreate();
//                    }
//                }).setIcon(R.drawable.ic_launcher_foreground);
//        this.failDlg = new AlertDialog.Builder(this)
//                .setTitle("Awwww :(")
//                .setMessage("You lost!\nDo you want to play again?")
//                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                    }
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent mpIntent  = new Intent(PlayActivity.this, MainPage.class);
//                        startActivity(mpIntent);
//                        //recreate();;
//                    }
//                }).setIcon(R.drawable.ic_launcher_foreground);
//    }
}