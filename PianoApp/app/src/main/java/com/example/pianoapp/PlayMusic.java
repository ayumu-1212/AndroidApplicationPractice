package com.example.pianoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlayMusic extends AppCompatActivity implements Runnable,View.OnClickListener{

    private final Handler handler = new Handler();
    private TextView timerText;
    private long startTime, endTime;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss.SSS",Locale.JAPAN);
    private volatile boolean stopRun = false;

    Piano pianoInstance;
    Button[] buttons = new Button[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano);

        timerText = findViewById(R.id.timer);
        timerText.setText(dataFormat.format(0));

        pianoInstance = new Piano(getApplicationContext());

        findViewById(R.id.return_to_top).setOnClickListener(this);

        findViewById(R.id.record_button).setOnClickListener(this);

        findViewById(R.id.stop_button).setOnClickListener(this);

        findViewById(R.id.send_button).setOnClickListener(this);

        getViews();

        for (int i = 0; i < 8; i++){
            buttons[i].setOnClickListener(this);
        }
//        setOnPianoClicks();
//        setOnRecordClicks();
//        setOnStopClicks();
//        setOnSendClicks();
    }

    private void getViews() {
        buttons[0] = (Button) findViewById(R.id.piano1_1do);
        buttons[1] = (Button) findViewById(R.id.piano1_2re);
        buttons[2] = (Button) findViewById(R.id.piano1_3mi);
        buttons[3] = (Button) findViewById(R.id.piano1_4fa);
        buttons[4] = (Button) findViewById(R.id.piano1_5so);
        buttons[5] = (Button) findViewById(R.id.piano1_6ra);
        buttons[6] = (Button) findViewById(R.id.piano1_7si);
        buttons[7] = (Button) findViewById(R.id.piano2_1do);

    }

    @Override
    public void onClick(View view) {
        Thread thread;

        for (int i = 0; i < 8; i++) {
            final int index = i;
            if (view == buttons[i]){
                pianoInstance.play(index);
            }
        }

        switch (view.getId()) {
            case R.id.return_to_top:
                Intent intent = new Intent(getApplication(), Top.class);
                startActivity(intent);
                break;

            case R.id.record_button:
                stopRun = false;
                timerText.setText(dataFormat.format(0));
                thread = new Thread(this);
                thread.start();
                startTime = System.currentTimeMillis();
                break;

            case R.id.stop_button:
                stopRun = true;
                break;

            case R.id.send_button:
                timerText.setText(dataFormat.format(0));
                break;

            default:
                break;
        }
    }

//    private void setOnPianoClicks() {
//        for (int i = 0; i < 8; i++) {
//            final int index = i;
//            buttons[index].setOnClickListener(this);
//            @Override
//            public void onClick(View v) {
//                pianoInstance.play(index);
//            }
//        }
//    }

//    private void setOnRecordClicks(){
//        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Thread thread;
//                stopRun = false;
//                timerText.setText(dataFormat.format(0));
//                thread = new Thread(this);
//                thread.start();
//                startTime = System.currentTimeMillis();
//            }
//        });
//    }
//
//    private void setOnStopClicks(){
//        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Thread thread;
//                stopRun = true;
//            }
//        });
//    }

//    private void setOnSendClicks(){
//        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                timerText.setText(dataFormat.format(0));
//            }
//        });
//    }

    @Override
    public void run() {
        // 10 msec order
        int period = 10;

        while (!stopRun) {
            // sleep: period msec
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopRun = true;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long endTime = System.currentTimeMillis();
                    // カウント時間 = 経過時間 - 開始時間
                    long diffTime = (endTime - startTime);

                    timerText.setText(dataFormat.format(diffTime));

                }
            });
        }
    }
}
