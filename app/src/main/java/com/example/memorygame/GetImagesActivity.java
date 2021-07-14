package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GetImagesActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView downloadProgressTextView;
    private String website = "https://stocksnap.io/search/";
    private String url;
    private int numDownloadedImages;
    private int numDisplayImages=20;
    private int numSelectedImages;
    private EditText searchImagesText;
    private Button searchBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_images);

        searchImagesText = findViewById(R.id.search_images_text);
        searchBtn = findViewById(R.id.search_images_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
            }
        });

        progressBar = findViewById(R.id.download_progress_bar);
        progressBar.setMax(numDisplayImages);

        downloadProgressTextView = findViewById(R.id.download_progress_textview);
        downloadProgressTextView.setText("Enter your search and press the SEARCH button");
    }


    void getImages(){
        if (searchImagesText.getText().toString().contains("/")){
            url = searchImagesText.getText().toString();
        }
        else{
            url = website+ searchImagesText.getText().toString();
        }


    }
}