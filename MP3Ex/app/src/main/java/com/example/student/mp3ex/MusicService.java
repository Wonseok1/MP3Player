package com.example.student.mp3ex;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by jws on 2018-12-19.
 */

public class MusicService extends Service {

    MediaPlayer player; // mp3파일 재생 mediaplayer 객체변수
    String fullPath; // mp3파일의 경로를 저장하는 변수수
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String btn = intent.getStringExtra("btn");

            Intent intent1 = new Intent("com.example.student.mp3ex");

            if (btn != null) {
                if (btn.equals("play") || btn.equals("pause")) {
                    if (player.isPlaying()) {
                        player.pause();
                        intent1.putExtra("state", "pause");
                    } else {
                        player.start();
                        intent1.putExtra("state", "play");
                    }
                } else if (btn.equals("stop")) {
                    player.stop();
                    try {
                        intent1.putExtra("state", "stop");
                        player.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sendBroadcast(intent1);
            }

        }


    };

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer(); //mediaPlayer객체 생성
        registerReceiver(receiver, new IntentFilter("com.example.student.mp3ex"));
        //액티비티와 통신할 리시버 등록

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fullPath = intent.getStringExtra("fullPath");
        if (fullPath != null) {
            try {
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Intent intent = new Intent("com.example.student.mp3ex");
                        intent.putExtra("state", "stop");
                        sendBroadcast(intent);
                        stopSelf();
                    }
                });

                player.setDataSource(fullPath);
                player.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver); //리시버 등록 해제
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
