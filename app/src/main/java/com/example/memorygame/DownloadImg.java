package com.example.memorygame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Patterns;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

// old class using Async method to download
public class DownloadImg extends AsyncTask<String, Bitmap, Void> {
    private IDownloadImg downloadImg;
    public interface IDownloadImg{
        public void downloadComplete(Bitmap bitmap);
    }

    //below constructor to reference the caller
    public DownloadImg (IDownloadImg downloadImg) {
        this.downloadImg = downloadImg;
    }
    @Override
    protected Void doInBackground(String... urls){
        //add logic for downloading using inputstream and produce bitmap
        try {
            for (String x : urls) {
                if (Patterns.WEB_URL.matcher(x).matches()) {        //check if url matches WEB_URL pattern
                    URL url = new URL(x);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    if(connection == null){
                        return null;
                    }

                    connection.setDoInput(true);
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    publishProgress(bitmap);

                    connection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... bitmaps) {
        if(this.downloadImg == null){
            return;
        }
        this.downloadImg.downloadComplete(bitmaps[0]);
    }
}
