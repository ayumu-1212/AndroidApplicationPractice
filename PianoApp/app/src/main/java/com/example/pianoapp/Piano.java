package com.example.pianoapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Piano {
    private SoundPool soundPool;

    private int soundId11Do;
    private int soundId12Re;
    private int soundId13Mi;
    private int soundId14Fa;
    private int soundId15So;
    private int soundId16Ra;
    private int soundId17Si;
    private int soundId21Do;

    public Piano(Context context) {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        soundId11Do = soundPool.load(context, R.raw.piano1_1do, 1);
        soundId12Re = soundPool.load(context, R.raw.piano1_2re, 1);
        soundId13Mi = soundPool.load(context, R.raw.piano1_3mi, 1);
        soundId14Fa = soundPool.load(context, R.raw.piano1_4fa, 1);
        soundId15So = soundPool.load(context, R.raw.piano1_5so, 1);
        soundId16Ra = soundPool.load(context, R.raw.piano1_6ra, 1);
        soundId17Si = soundPool.load(context, R.raw.piano1_7si, 1);
        soundId21Do = soundPool.load(context, R.raw.piano2_1do, 1);
    }

    public void play(int key) {
        switch (key) {
            case 0:
                soundPool.play(soundId11Do, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case 1:
                soundPool.play(soundId12Re, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case 2:
                soundPool.play(soundId13Mi, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case 3:
                soundPool.play(soundId14Fa, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case 4:
                soundPool.play(soundId15So, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case 5:
                soundPool.play(soundId16Ra, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case 6:
                soundPool.play(soundId17Si, 1.0f, 1.0f, 0, 0, 1.0f);
                break;
            case 7:
                soundPool.play(soundId21Do, 1.0f, 1.0f, 0, 0, 1.0f);
                break;

        }
    }
}
