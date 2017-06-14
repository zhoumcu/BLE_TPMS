package com.example.sid_fu.blecentral.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Handler;

import com.example.sid_fu.blecentral.R;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * author：Administrator on 2017/6/14 15:57
 * company: xxxx
 * email：1032324589@qq.com
 */

public class SoundPoolUtils {

    private SoundPool sp;
    private HashMap<String, Integer> spMap;
    private HashMap<String, String> hasMap  = new HashMap<String, String>();
    private Handler mHandler = new Handler();
    private Vector<Integer> mKillSoundQueue = new Vector<Integer>();
    private long delay = 6000;
    private long seperateTime = 2000;
    private Context mContext;

    public void initSound(Context context) {
        mContext  = context;
        sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        spMap = new HashMap<String, Integer>();
        spMap.put(SoundManager.wel, sp.load(mContext, R.raw.welcome, 1));
        spMap.put(SoundManager.bund, sp.load(mContext, R.raw.kspd, 1));
        spMap.put(SoundManager.rightB, sp.load(mContext, R.raw.yh, 2));
        spMap.put(SoundManager.rightF, sp.load(mContext, R.raw.yq, 3));
        spMap.put(SoundManager.leftB, sp.load(mContext, R.raw.zh, 4));
        spMap.put(SoundManager.leftF, sp.load(mContext, R.raw.zq, 5));
    }

    public void playSound(final String sound, String key)  {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
//        if(hasMap.get(sound).contains(key))
//        {
//            return;
//        }
//        hasMap.put(sound,key);
//        int soundId = sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, 0, 1f);
//        mKillSoundQueue.add(soundId);
//        if (!mKillSoundQueue.isEmpty()&&mKillSoundQueue.size()>1)
//        {
//            sp.stop(mKillSoundQueue.firstElement());
//            mKillSoundQueue.remove(0);
//        }
//        // schedule the current sound to stop after set milliseconds
//        mHandler.postDelayed(new Runnable() {
//            public void run() {
//                if (!mKillSoundQueue.isEmpty()) {
//                    sp.stop(mKillSoundQueue.firstElement());
//                    hasMap.remove(sound);
//                }
//            }
//        }, delay);
    }
    public void playSound(final String sound)  {
        //AudioTrack for incoming audio to play as below:

        int mMaxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
        new AudioTrack(AudioManager.STREAM_VOICE_CALL,8000,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mMaxJitter, AudioTrack.MODE_STREAM);
//To register broadcast receiver for bluetooth audio routing
//        IntentFilter ifil = new IntentFilter();
//        ifil.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
//        this.registerReceiver(<receiver instance>,ifil);

        //To get AudioManager service
        AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

        //Whenever user select to route audio to Bluetooth
        am.setMode(AudioManager.MODE_IN_CALL);//tried setting with other mode also viz. MODE_NORMAL, MODE_IN_COMMUNICATION but no luck
        am.startBluetoothSco();//after this I get AudioManager.SCO_AUDIO_STATE_CONNECTED state in the receiver
        am.setBluetoothScoOn(true);
        am.setSpeakerphoneOn(false);

        //Whenever user select to route audio to Phone Speaker
        am.setMode(AudioManager.MODE_NORMAL);
        am.stopBluetoothSco();//after this I get      AudioManager.SCO_AUDIO_STATE_DISCONNECTED state in the receiver
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(true);

//        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        changeToSpeaker(am);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_RING);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        if(volumnCurrent<audioMaxVolumn*0.2) volumnRatio = audioMaxVolumn / 3;
//        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) volumnRatio, 1);
        Logger.e("最大音量："+audioMaxVolumn+"当前系统音量："+volumnCurrent+"设定音量："+volumnRatio);
        int soundId = sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, 0, 1f);
//        sp.setVolume(soundId,volumnRatio,volumnRatio);
//        mKillSoundQueue.add(soundId);
//        if (!mKillSoundQueue.isEmpty()&&mKillSoundQueue.size()>1)
//        {
//            sp.stop(mKillSoundQueue.firstElement());
//            mKillSoundQueue.remove(0);
//        }
        // schedule the current sound to stop after set milliseconds
//        mHandler.postDelayed(new Runnable() {
//            public void run() {
//                if (!mKillSoundQueue.isEmpty()) {
//                    sp.stop(mKillSoundQueue.firstElement());
//                }
//            }
//        }, delay);

    }
    public void playSigleSound(final String sound)  {
        int mMaxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
        new AudioTrack(AudioManager.STREAM_VOICE_CALL,8000,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mMaxJitter, AudioTrack.MODE_STREAM);
//To register broadcast receiver for bluetooth audio routing
//        IntentFilter ifil = new IntentFilter();
//        ifil.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
//        this.registerReceiver(<receiver instance>,ifil);

        //To get AudioManager service
        AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

        //Whenever user select to route audio to Bluetooth
        am.setMode(AudioManager.MODE_IN_CALL);//tried setting with other mode also viz. MODE_NORMAL, MODE_IN_COMMUNICATION but no luck
        am.startBluetoothSco();//after this I get AudioManager.SCO_AUDIO_STATE_CONNECTED state in the receiver
        am.setBluetoothScoOn(true);
        am.setSpeakerphoneOn(false);

        //Whenever user select to route audio to Phone Speaker
        am.setMode(AudioManager.MODE_NORMAL);
        am.stopBluetoothSco();//after this I get      AudioManager.SCO_AUDIO_STATE_DISCONNECTED state in the receiver
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(true);

//        changeToSpeaker(am);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_RING);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        if(volumnCurrent<audioMaxVolumn*0.2) volumnRatio = audioMaxVolumn / 3;
//        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) volumnRatio, 1);
        Logger.e("最大音量："+audioMaxVolumn+"当前系统音量："+volumnCurrent+"设定音量："+volumnRatio);
        int soundId = sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, 0, 1f);
//        sp.setVolume(soundId,volumnRatio,volumnRatio);
        mKillSoundQueue.add(soundId);
        // schedule the current sound to stop after set milliseconds
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!mKillSoundQueue.isEmpty()) {
                    sp.stop(mKillSoundQueue.firstElement());
                }
            }
        }, delay);
    }
    /**
     * 切换到外放
     */
    public void changeToSpeaker(AudioManager audioManager){
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }

    /**
     * play the sounds have loaded in SoundPool
     * @param keys the files key stored in the map
     * @throws InterruptedException
     */
    public void playMutilSounds(List<String> keys)
            throws InterruptedException {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        for (String key : keys) {
            Logger.e("playMutilSounds"+keys.size());
            if (spMap.containsKey(key)) {
                int soundId = sp.play(spMap.get(key), volumnCurrent, volumnRatio, 1, 0, 1.0f);
                //sleep for a while for SoundPool play
                Thread.sleep(seperateTime);
                mKillSoundQueue.add(soundId);
                keys.remove(key);
            }
        }

        // schedule the current sound to stop after set milliseconds
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!mKillSoundQueue.isEmpty()) {
                    sp.stop(mKillSoundQueue.firstElement());
                }
            }
        }, delay);

    }
    public void pauseSound(){
        sp.autoPause();
    }

}
