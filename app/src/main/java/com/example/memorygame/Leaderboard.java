package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Leaderboard extends AppCompatActivity {

    TextView score1, score2, score3, score4, score5;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int lastScore;
    int rank1, rank2, rank3, rank4, rank5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        score1 = (TextView) findViewById(R.id.score1);
        score2 = (TextView) findViewById(R.id.score2);
        score3 = (TextView) findViewById(R.id.score3);
        score4 = (TextView) findViewById(R.id.score4);
        score5 = (TextView) findViewById(R.id.score5);

        sharedPreferences = getSharedPreferences("leaderboard", 0);
        lastScore = sharedPreferences.getInt("lastScore", 0);
        rank1 = sharedPreferences.getInt("rank1", 0);
        rank2 = sharedPreferences.getInt("rank2", 0);
        rank3 = sharedPreferences.getInt("rank3", 0);
        rank4 = sharedPreferences.getInt("rank4", 0);
        rank5 = sharedPreferences.getInt("rank5", 0);

        if(lastScore > rank5) {
            rank5 = lastScore;
            editor = sharedPreferences.edit();
            editor.putInt("rank5", rank5);
            editor.apply();
        }
        if(lastScore > rank4) {
            int temp = rank4;
            rank4 = lastScore;
            rank5 = temp;
            editor = sharedPreferences.edit();
            editor.putInt("rank5", rank5);
            editor.putInt("rank4", rank4);
            editor.apply();
        }
        if(lastScore > rank3) {
            int temp = rank3;
            rank3 = lastScore;
            rank4 = temp;
            editor = sharedPreferences.edit();
            editor.putInt("rank4", rank4);
            editor.putInt("rank3", rank3);
            editor.apply();
        }
        if(lastScore > rank2) {
            int temp = rank2;
            rank2 = lastScore;
            rank3 = temp;
            editor = sharedPreferences.edit();
            editor.putInt("rank3", rank3);
            editor.putInt("rank2", rank2);
            editor.apply();
        }
        if(lastScore > rank1) {
            int temp = rank1;
            rank1 = lastScore;
            rank2 = temp;
            editor = sharedPreferences.edit();
            editor.putInt("rank2", rank2);
            editor.putInt("rank1", rank1);
            editor.apply();
        }

        score1.setText("" + rank1);
        score2.setText("" + rank2);
        score3.setText("" + rank3);
        score4.setText("" + rank4);
        score5.setText("" + rank5);

        Button homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeBtn != null){
                    Intent mainPage = new Intent(Leaderboard.this, MainPage.class);
                    startActivity(mainPage);
                }
            }
        });

        Button playAgainBtn = findViewById(R.id.playAgainBtn);
        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playAgainBtn != null){
                    Intent startGame = new Intent(Leaderboard.this, MainActivity.class);
                    startActivity(startGame);
                }
            }
        });

    }
}