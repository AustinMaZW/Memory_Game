package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Button startGameBtn = findViewById(R.id.start_game_btn);

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startGameBtn != null){
                    Intent startGame = new Intent(MainPage.this, MainActivity.class);
                    startActivity(startGame);
                }
            }
        });

        Button leaderboardBtn = findViewById(R.id.leaderboard_btn);
        leaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leaderboardBtn != null){
                    Intent viewLeaderboard = new Intent(MainPage.this, Leaderboard.class);
                    startActivity(viewLeaderboard);
                }
            }
        });

        Button creditBtn = findViewById(R.id.credits_btn);
        creditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (creditBtn != null){
                    Intent viewCredits = new Intent(MainPage.this, viewCreditsActivity.class);
                    startActivity(viewCredits);
                }
            }
        });

        Button descriptionBtn = findViewById(R.id.description_btn);
        descriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionBtn != null){
                    Intent viewDescription = new Intent(MainPage.this, viewDescriptionActivity.class);
                    startActivity(viewDescription);
                }

            }
        });
    }
}