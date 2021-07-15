package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class Leaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

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