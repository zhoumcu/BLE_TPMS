package com.example.sid_fu.blecentral.utils;

/**
 * Created by Administrator on 2016/7/27.
 */
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

import com.example.sid_fu.blecentral.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

public class SoundManager {

    private SoundPool mSoundPool;

    private HashMap<String, Integer> mSoundPoolMap;

    private AudioManager mAudioManager;

    private Context mContext;

    private Handler mHandler = new Handler();

    private Vector<Integer> mKillSoundQueue = new Vector<Integer>();

//    private VoicePhoneNumberUtil util;

    private long delay = 1000;

    private long seperateTime = 700;

    private float rate = 1.0f;

    private String locale;

    static private SoundManager _instance;

    public static String wel = "wel";
    public static String bund = "bund";
    public static String leftF = "leftF";
    public static String rightF = "rightF";
    public static String leftB = "leftB";
    public static String rightB = "rightB";
    /**
     * Requests the instance of the Sound Manager and creates it if it does not
     * exist.
     *
     * @return Returns the single instance of the SoundManager
     */
    static synchronized public SoundManager getInstance() {
        if (_instance == null)
            _instance = new SoundManager();
        return _instance;
    }

    /**
     *
     */
    private SoundManager() {
//        util = VoicePhoneNumberUtil.getInstance();
    }

    /**
     * Initialises the storage for the sounds
     *
     * @param theContext The Application context
     */

    public void initSounds(Context theContext) {
        mContext = theContext;
        mSoundPool = new SoundPool(6,AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap<String, Integer>();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        spMap.put(1, sp.load(this, R.raw.welcome, 1));
//        spMap.put(2, sp.load(this, R.raw.kspd, 1));
//        spMap.put(3, sp.load(this, R.raw.yh, 2));
//        spMap.put(4, sp.load(this, R.raw.yq, 3));
//        spMap.put(5, sp.load(this, R.raw.zh, 4));
//        spMap.put(6, sp.load(this, R.raw.zq, 5));
//        addSound(wel, R.raw.welcome);
//        addSound(bund, R.raw.kspd);
//        addSound(rightB, R.raw.yh);
//        addSound(rightF, R.raw.yq);
//        addSound(leftB, R.raw.zh);
//        addSound(leftF, R.raw.zq);

        mSoundPoolMap.put(wel, mSoundPool.load(mContext, R.raw.welcome, 1));
        mSoundPoolMap.put(bund, mSoundPool.load(mContext, R.raw.kspd, 1));
        mSoundPoolMap.put(rightB, mSoundPool.load(mContext, R.raw.yh, 2));
        mSoundPoolMap.put(rightF, mSoundPool.load(mContext, R.raw.yq, 3));
        mSoundPoolMap.put(leftB, mSoundPool.load(mContext, R.raw.zh, 4));
        mSoundPoolMap.put(leftF, mSoundPool.load(mContext, R.raw.zq, 5));

    }

    /**
     * Add a new Sound to the SoundPool
     *
     * @param key - The Sound Index for Retrieval
     * @param SoundID - The Android ID for the Sound asset.
     */

    public void addSound(String key, int SoundID) {
        mSoundPoolMap.put(key, mSoundPool.load(mContext, SoundID, 1));
    }
    /**
     *
     * @param key the key we need to get the sound later
     * @param afd  the fie store in the asset
     */
    public void addSound(String key, AssetFileDescriptor afd) {
        mSoundPoolMap.put(key, mSoundPool.load(
                afd.getFileDescriptor(),
                afd.getStartOffset(), afd.getLength(), 1));
    }

    /**
     * play the sound loaded to the SoundPool by the key we set
     * @param key  the key in the map
     */
    public void playSound(String key) {

        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume
                / mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int soundId = mSoundPool.play(
                mSoundPoolMap.get(key), streamVolume,
                streamVolume, 1, 0, rate);
        mKillSoundQueue.add(soundId);
        // schedule the current sound to stop after set milliseconds
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!mKillSoundQueue.isEmpty()) {
                    mSoundPool.stop(mKillSoundQueue
                            .firstElement());
                }
            }
        }, delay);

    }

    /**
     *
     * @param key  the key in the map
     */
    public void playLoopedSound(String key) {

        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int soundId = mSoundPool.play(mSoundPoolMap.get(key), streamVolume, streamVolume, 1, -1, rate);

        mKillSoundQueue.add(soundId);
        // schedule the current sound to stop after set milliseconds
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!mKillSoundQueue.isEmpty()) {
                    mSoundPool.stop(mKillSoundQueue
                            .firstElement());
                }
            }
        }, delay);
    }

    /**
     * play the sounds have loaded in SoundPool
     * @param keys the files key stored in the map
     * @throws InterruptedException
     */
    public void playMutilSounds(String keys[])
            throws InterruptedException {
        int streamVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume
                / mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        for (String key : keys) {
            Log.d("playMutilSounds", key);
            if (mSoundPoolMap.containsKey(key)) {
                int soundId = mSoundPool.play(
                        mSoundPoolMap.get(key),
                        streamVolume, streamVolume, 1, 0,
                        rate);
                //sleep for a while for SoundPool play
                Thread.sleep(seperateTime);
                mKillSoundQueue.add(soundId);
            }

        }

        // schedule the current sound to stop after set milliseconds
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!mKillSoundQueue.isEmpty()) {
                    mSoundPool.stop(mKillSoundQueue
                            .firstElement());
                }
            }
        }, delay);

    }

    /**
     * Loads the various sound assets
     * @param locale the load to load audio files
     */

    public void loadSounds(String locale)
            throws SAXException, IOException,
            ParserConfigurationException {
        Log.d("load locale", locale);
        this.locale = locale;
        AssetFileDescriptor afd;
//        Map<String, String> audioFiles = util.getAudioFileConfig(locale, mContext.getAssets());

//        for (String key : audioFiles.keySet()) {
//            afd = mContext.getAssets().openFd(
//                    audioFiles.get(key));
//            addSound(key, afd);
//        }

    }

    /**
     * Stop a Sound
     *
     * @param index - index of the sound to be stopped
     */
    public void stopSound(int index) {
        mSoundPool.stop(mSoundPoolMap.get(index));
    }

    /**
     * Deallocates the resources and Instance of SoundManager
     */
    public void cleanup() {
        mSoundPool.release();
        mSoundPool = null;
        mSoundPoolMap.clear();
        mAudioManager.unloadSoundEffects();
        _instance = null;

    }

    /**
     * unload all resource in the sound pool
     * support for user change VoiceLanguage or Locale or user close the voice function !
     */

    public void unloadAllSoundsIn() {
        if (mSoundPoolMap.size() > 0) {
            for (String key : mSoundPoolMap.keySet()) {
                mSoundPool.unload(mSoundPoolMap.get(key));
            }
        }
        mKillSoundQueue.clear();
        mSoundPoolMap.clear();
    }

    /**
     * set the speed of soundPool
     *
     * @param i i<0 means slow i= 0 means normal i>0 means fast
     */
    public void setVoiceSpeed(int i) {
        if (i > 0) {
            rate = 1.2f;
        }
        else if (i < 0) {
            rate = 0.8f;
        }
        else {
            rate = 1.0f;
        }

    }

    /**
     * set the delay after one number's sound have played
     *
     * @param i i<0 means short i= 0 means normal i>0 means long
     */
    public void setVoiceDelay(int i) {
        if (i > 0) {
            seperateTime = 700;
        }
        else if (i < 0) {
            seperateTime = 400;
        }
        else {
            seperateTime = 500;
        }

    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

}
