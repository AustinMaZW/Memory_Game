package com.example.memorygame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloader {
    protected String[] fetchImageTags(String url){
        try {
            Document document = Jsoup.connect(url).get();

            String[] imgUrls = new String[20];
            for (int i = 0; i<20; i++){
                String imgUrl = document.select("img[src^=https]")
                        .eq(i)
                        .attr("src");

                imgUrls[i]=imgUrl;
            }
            return imgUrls;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    protected boolean downloadImage(String imgURL, File destFile){
        try{
            URL url = new URL(imgURL);
            URLConnection conn = url.openConnection();

            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];         //set buffer for max bytes to read each time
            int bytesRead = -1;
            while ((bytesRead=in.read(buffer))!=-1){    //in.read(buffer) reads the input up to buffer.length bytes of data into buffer
                out.write(buffer, 0, bytesRead);    //writer bytesRead (len) of data from buffer, starting 0th position (offset)
            }

            System.out.println(destFile.getAbsolutePath());     //for debug purpose, delete later
            out.close();
            in.close();
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
