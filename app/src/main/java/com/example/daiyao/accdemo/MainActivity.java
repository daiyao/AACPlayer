package com.example.daiyao.accdemo;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class MainActivity extends AppCompatActivity {

    boolean          isRecord;
    AudioRecordUtil  mAudioRecordUtil;
    AudioPlayerUtil  mAudioPlayerUtil;
    PipedInputStream mPipedInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                1000);
    }

    public void clickStart(View view) {
        if (isRecord) {
            isRecord = false;
            mAudioRecordUtil.stopRecord();
            mAudioPlayerUtil.stopPlay();
        } else {
            isRecord = true;
            startRecord();
        }
    }

    private void startRecord() {
        mPipedInputStream = new PipedInputStream();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mAudioRecordUtil = new AudioRecordUtil(MainActivity.this, mPipedInputStream);
                    mAudioRecordUtil.StartAudioData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                PipedOutputStream pout = new PipedOutputStream();
                mAudioPlayerUtil = new AudioPlayerUtil();
                try {
                    mAudioPlayerUtil.setOutputStream(pout);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mAudioPlayerUtil.startPlayAudio();
                        }
                    }).start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                int size = 0;
                try {
                    while (true) {
                        while (mPipedInputStream.available() > 0) {
                            size = mPipedInputStream.read(buffer);
                            pout.write(buffer, 0, size);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
