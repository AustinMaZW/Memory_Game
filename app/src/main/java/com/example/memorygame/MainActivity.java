package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startGameBtn = findViewById(R.id.start_game_btn);
        Button creditBtn = findViewById(R.id.credits);
        Button descriptionBtn = findViewById(R.id.description);

    }
}
