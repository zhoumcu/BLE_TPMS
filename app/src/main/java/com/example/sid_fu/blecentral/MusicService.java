package com.example.sid_fu.blecentral;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by sid-fu on 2016/7/12.
 */
public class MusicService extends Service {

    private AudioManager mAm;
    private boolean isPlaymusic;
    private String url;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mAm = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPlaymusic = bundle.getBoolean("isPlay", true);
                url = bundle.getString("url");
                if (isPlaymusic)
                    play();
                else
                    stop();
            }
        }

    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                resume();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // mAm.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                mAm.abandonAudioFocus(afChangeListener);
                // Stop playback
                stop();
            }

        }
    };

    private boolean requestFocus() {
        // Request audio focus for playback
        int result = mAm.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer player) {
            if(!player.isLooping()){
                mAm.abandonAudioFocus(afChangeListener);
            }
        }
    };
    private void play() {
        if (requestFocus()) {
            if (mediaPlayer == null) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnCompletionListener(completionListener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}

