package com.example.memorygame;

import android.content.Context;
import android.media.MediaPlayer;

public class MyMediaPlayer {

    private static MyMediaPlayer Instance;
    MediaPlayer mediaPlayer;

    static MyMediaPlayer getMediaPlayerInstance() {
        if (Instance == null) {
            return Instance = new MyMediaPlayer();
        }
        return Instance;
    }

    public void playAudioFile(Context context, int sampleAudio) {
        mediaPlayer = MediaPlayer.create(context, sampleAudio);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });

    }

    public void stopAudioFile() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }}