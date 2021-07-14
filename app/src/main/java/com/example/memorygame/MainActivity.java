package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
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
    private Thread dlThread;
    private ProgressBar progressBar;
    private TextView progressTextView;
    private GridView gridView;
    private SelectImageAdaptor mImageAdaptor;

    //config images
    private final int IMAGES_NO = 20;
    private List<String> selectedImgFileNames = new ArrayList<>();
    private Bitmap[] imgBmps = new Bitmap[20];
//    private String[] imgUrls = new String[20]; //used array and not list to allow execute for DownloadImg

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testing gridview
        gridView = findViewById(R.id.gridview);
        mImageAdaptor = new SelectImageAdaptor(this, imgBmps);
        gridView.setAdapter(mImageAdaptor);

        progressBar = findViewById(R.id.progress_bar);
        progressTextView = findViewById(R.id.progress_textview);

        //below code to parse images
        fetchButton = findViewById(R.id.fetch_url);
        fetchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearSettings(); //add more logic later
                fetchUrl = findViewById(R.id.url_input);
                String url = fetchUrl.getText().toString();
                startDownloadImage(url);
            }
        });
    }

    public void startDownloadImage(String url) {
        //creating a background thread
        dlThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ImageDownloader imgDL = new ImageDownloader();
                String[] imgURLs = imgDL.fetchImageTags(url);
                int imgBtnNo=0;
                if(imgURLs==null){return;} //if there is no imgtags or invalid url so couldn't fetch tags, return
                for (String imgURL: imgURLs) {
                    if(Thread.interrupted()) {return;}

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
                                    setImageOnClickListeners();
                                    progressTextView.setText("Select 6 images to start playing");
                                }
                            }
                        });
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
            System.out.println("all 6 chosen, time to add logic for start game!");
            startGame();
        }
        else if(selectedImgFileNames.size() == 0)
            progressTextView.setText("Select 6 images to start playing");
        else
            progressTextView.setText(selectedImgFileNames.size() + " / 6 selected");
    }

    public void startGame(){
//        Intent intent = new Intent(this, GameActivity.class);

        String[] imgs = new String[selectedImgFileNames.size()];     //convert to string[] so i can pass to activity
        selectedImgFileNames.toArray(imgs);
        System.out.println("Array "+ Arrays.toString(imgs));
        clearSettings();    //clear settings after extracting the imgs to array
//        intent.putExtra("imgs", imgs);
//        startActivity(intent);
    }

    public void clearSettings() {
        if(dlThread!=null)
            dlThread.interrupt();       //interrupt current thread

        progressBar.setProgress(0);
        progressTextView.setText("Click 'FETCH' to download images");

        Arrays.fill(imgBmps, null);         //clear stored bitmap array
        selectedImgFileNames.clear();

        mImageAdaptor = new SelectImageAdaptor(this, imgBmps);
        gridView.setAdapter(mImageAdaptor);     //reset view
        gridView.setOnItemClickListener(null);  //reset onclickerlistener
    }
    //old code for 2 threads, but problem was race condition for setting file name since using imgBtnNo++
//    public void fetchImages() {
//        fetchUrl = findViewById(R.id.url_input);
//        String url = fetchUrl.getText().toString();
//        new Thread(new Runnable(){
//            @Override
//            public void run(){
//                try {
//                    Document document = Jsoup.connect(url).get();
//
//                    for (int i = 0; i<20; i++){
//                        String imgUrl = document.select("img[src^=https]")
//                                .eq(i)
//                                .attr("src");
//
//                        startDownloadImage(imgUrl);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    //old codes using AsyncTask
//    public void fetchImages() {
//        fetchUrl = findViewById(R.id.url_input);
//        ImgSrc imgSrc = new ImgSrc();
//        imgSrc.execute(fetchUrl.getText().toString());      //input is one url only
//
//        DownloadImg downloadImg = new DownloadImg(this);
//        downloadImg.execute(imgUrls);
//    }

//    private class ImgSrc extends AsyncTask<String, Void, Void> {
//        @Override
//        protected void onPreExecute(){
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(String... urls){
//
//            try {
//                String url = urls[0];   //input is one url, so just grab the first index
//                //String url = "https://stocksnap.io/"; //hard coding here for testing purpose, delete later
//                Document document = Jsoup.connect(url).get();
//
//                for (int i = 0; i<20; i++){
//                    String imgUrl = document.select("img[src^=https]")
//                            .eq(i)
//                            .attr("src");
//
//                    imgUrls[i]=imgUrl;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        // delete below if not needed later
//        @Override
//        protected void onPostExecute(Void aVoid) {
//
//        }
//    }
//    public void downloadComplete(Bitmap bitmap){
//        // add logic to get images after send by downloadImg
//        ImageButton imageButton = findViewById(getResources()
//                .getIdentifier("imageButton" + imgBtnNo, "id", getPackageName()));
//
//        // didn't add setTag here, can be used for onClick
//        imageButton.setImageBitmap(bitmap);
//        imageButton.setAdjustViewBounds(true);
//        imgBtnNo++;
//    }
}
