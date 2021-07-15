package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //views
    private AppCompatButton fetchButton;
    private EditText fetchUrl;
    private ProgressBar progressBar;
    private ViewFlipper viewFlipper;
    private TextView progressTextView;
    private AppCompatButton startGameBtn;
    private GridView gridView;
    private SelectImageAdaptor mImageAdaptor;

    private Thread dlThread;
    private boolean downloading = false;

    //config images
    private final int IMAGES_NO = 20;
    private List<String> selectedImgFileNames = new ArrayList<>();
    private Bitmap[] imgBmps = new Bitmap[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testing gridview
        gridView = findViewById(R.id.gridview);
        mImageAdaptor = new SelectImageAdaptor(this, imgBmps);
        gridView.setAdapter(mImageAdaptor);

        fetchUrl = findViewById(R.id.url_input);
        progressBar = findViewById(R.id.progress_bar);
        viewFlipper= findViewById(R.id.view_flipper);
        progressTextView = findViewById(R.id.progress_textview);
        startGameBtn = findViewById(R.id.start_game);

        //below code to parse images
        fetchButton = findViewById(R.id.fetch_url);
        fetchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = fetchUrl.getText().toString();
                if(isURL(url)){
                    clearSettings();    //reset any settings
                    progressTextView.setText(R.string.download_starting);
                    downloading=true;
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.progress_textview)));
                    startDownloadImage(url);
                }else{
                    progressTextView.setText(R.string.invalid_url);
                    progressTextView.setTextColor(Color.RED);
                }
            }
        });

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    public boolean isURL(String url){
        //use Pattern's regex library to check if user url is valid
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public void startDownloadImage(String url){
        //creating a background thread
        dlThread = new Thread(new Runnable() {
            @Override
            public void run(){
                ImageDownloader imgDL = new ImageDownloader();

                String[] imgURLs = new String[0];
                imgURLs = imgDL.fetchImageTags(url);
                if(imgURLs==null){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            //if imagesURLs is null, then reflect back to user on UI thread
                            progressTextView.setText(R.string.cannot_access_url);
                            progressTextView.setTextColor(Color.RED);
                        }
                    });
                }

                int imgBtnNo=0;
                if(imgURLs==null){return;} //if there is no imgtags or invalid url so couldn't fetch tags, return
                for (String imgURL: imgURLs) {
                    if(Thread.interrupted()) { return; }

                    String destFilename = "image" + imgBtnNo;
                    File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File destFile = new File(dir, destFilename);

                    if(imgDL.downloadImage(imgURL,destFile)){
                        int finalImgNo = imgBtnNo;           //declared so i can set progressbar, runOnUIThread cannot access otherwise
                        imgBtnNo++;

                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
                                mImageAdaptor.changeImageBitmap(bitmap, finalImgNo);

                                int progressPercent = (finalImgNo+1)*5;     //each dl is 5%
                                progressBar.setProgress(progressPercent);
                                progressTextView.setText("Downloading " + finalImgNo + " of 20 images");

                                if(progressPercent == 100) {
                                    downloading=false;
                                    setImageOnClickListeners();
                                    progressTextView.setText(R.string.select_6);
                                }
                            }
                        });
                    }else{
                        System.out.println("im interrupted");
                        return;
                    }
                }
            }
        });
        dlThread.start();
    }

    public void setImageOnClickListeners(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                String imageName = "image" + position;
                ImageView itemView = (ImageView) gridView.getAdapter().getView(position, v, parent);

                if(selectedImgFileNames.contains(imageName)){
                    itemView.clearColorFilter();
                    selectedImgFileNames.remove(imageName);
                }else{
                    itemView.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                    selectedImgFileNames.add(imageName);
                }
                checkSelectedImgs();
            }
        });
    }

    public void checkSelectedImgs() {
        if(selectedImgFileNames.size() == 6) {
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.start_game)));
        }
        else if(selectedImgFileNames.size() == 0) {
            progressTextView.setText(R.string.select_6);
        }
        else{
            progressTextView.setText(selectedImgFileNames.size() + " / 6 selected");
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.progress_textview)));
        }
    }

    public void startGame(){
//        Intent intent = new Intent(this, GameActivity.class);

        String[] imgs = new String[selectedImgFileNames.size()];     //convert to string[] so i can pass to activity
        selectedImgFileNames.toArray(imgs);
        System.out.println("Array "+ Arrays.toString(imgs));
        clearSettings();            //clear settings after extracting the imgs to array
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.progress_textview)));
//        intent.putExtra("imgs", imgs);
//        startActivity(intent);
    }

    public void clearSettings() {
        if(downloading)
            dlThread.interrupt();       //interrupt current dlthread

        progressBar.setProgress(0);
        progressTextView.setTextColor(ContextCompat.getColor(this,R.color.prussian));
        progressTextView.setText(R.string.click_fetch);

        Arrays.fill(imgBmps, null);         //clear stored bitmap array
        selectedImgFileNames.clear();

        mImageAdaptor = new SelectImageAdaptor(this, imgBmps);
        gridView.setAdapter(mImageAdaptor);     //reset view
        gridView.setOnItemClickListener(null);  //reset onclickerlistener
    }
}
