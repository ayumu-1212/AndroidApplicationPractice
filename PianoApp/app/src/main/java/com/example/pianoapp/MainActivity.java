package com.example.pianoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button;
    Button buttonDo;
    Piano pianoInstance;

    int piano[][];
    Button[] buttons = new Button[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano);

        pianoInstance = new Piano(getApplicationContext());

        getViews();
        setOnPianoClicks();
        setOnRecordClicks();
        setOnStopClicks();
        setOnSendClicks();
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

    private void setOnPianoClicks() {
        for (int i = 0; i < 8; i++) {
            final int index = i;
            buttons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pianoInstance.play(index);
                }
            });
        }
    }

    private void setOnRecordClicks(){
        findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                piano = new int[2][];

            }
        });
    }

    private void setOnStopClicks(){
        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setOnSendClicks(){
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
