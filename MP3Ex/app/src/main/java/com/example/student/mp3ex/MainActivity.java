package com.example.student.mp3ex;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    boolean bReadPerm = false; // sd카드 읽기 여부
    boolean bWritePerm = false; // sd카드 쓰기 여부
    Button button_play, button_stop, button_next, button_prev;
    /*MediaPlayer player;*/
    ListView music_listview;

    String mp3; //상세파일명
    int i=0; //리스트[i]
    ArrayList<String> mp3List; //sd카드에서 검색된 mp3리스트
    String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";//경로
    Boolean bStatePlay = false; //재생상태 유뮤


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPermission();

        button_play = (Button)findViewById(R.id.button_play);
        button_stop = (Button)findViewById(R.id.button_stop);
        button_next = findViewById(R.id.button_next);
        button_prev = findViewById(R.id.button_prev);
        music_listview = findViewById(R.id.music_listview);

        button_play.setOnClickListener(new MyButtonListener());
        button_stop.setOnClickListener(new MyButtonListener());
        button_next.setOnClickListener(new MyButtonListener());
        button_prev.setOnClickListener(new MyButtonListener());

        /*player = new MediaPlayer();*/

        //리시버등록(서비스와 통신)
        registerReceiver(receiver, new IntentFilter("com.example.student.mp3ex"));


        File[] listFiles = new File(musicPath).listFiles();
        String fileName, extName;
        mp3List = new ArrayList<String>();

        //sd카드의 mp3파일 리스트에 저장
        for (File file : listFiles) {
            fileName = file.getName();
            extName = fileName.substring(fileName.length() - 3);
            if (extName.equals("mp3")) {
                mp3List.add(fileName);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, mp3List);
        music_listview.setAdapter(adapter);
        music_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        music_listview.setItemChecked(i,true);
        music_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mp3 = mp3List.get(position);
            }
        });

        mp3 = mp3List.get(i);

        // 퍼미션 허가 되었을시
        if(bReadPerm && bWritePerm) {
            String state = Environment.getExternalStorageState();

            if (state.equals(Environment.MEDIA_MOUNTED)) {
                try {
                    Intent intent = new Intent(MainActivity.this, MusicService.class);
                    intent.putExtra("fullPath", musicPath + mp3); //권한 허가시 인텐트로 서비스에 파일 경로 정보 전달
                    startService(intent);

                    /*player.setDataSource(musicPath+mp3);
                    player.prepare();
                    Log.d("PlayMp3", "mp3 file ");*/
                } catch (Exception e) {
                   /* Log.d("PlayMp3", "mp3 file error");*/
                   e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra("state");

            if (state != null) {
                if (state.equals("play")) {
                    bStatePlay = true;
                    button_play.setText("pause");

                } else if (state.equals("pause") || state.equals("stop")) {
                    bStatePlay = false;
                    button_play.setText("play");
                }
            }
        }
    };

    class MyButtonListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            Intent intent = new Intent("com.example.student.mp3ex");

            switch(view.getId()) {
                case R.id.button_play:
                    if (bStatePlay) {
                        intent.putExtra("btn", "pause");
                    } else {
                        intent.putExtra("btn", "play");
                    }
                    /*if(player.isPlaying()) {
                        player.pause();
                        button_play.setText("play");
                    } else {
                        player.start();
                        button_play.setText("pause");
                    }*/
                    break;

                case R.id.button_stop:
                    intent.putExtra("btn", "stop");
                    /*player.stop();
                    button_play.setText("play");
                    try {
                        player.prepare();
                    } catch (Exception e) {
                        Log.d("PlayMp3", "mp3 file error");
                    }*/
                    break;

                case R.id.button_next:
                   /* player.stop();
                    i++;
                    player.reset();
                    if (i > mp3List.size()-1) {
                        i=0;
                    }
                    mp3 = mp3List.get(i).toString();
                    music_listview.setItemChecked(i,true);
                    try {
                        player.setDataSource(musicPath + mp3);
                        player.prepare();
                    } catch (IOException e) {
                    }
                    player.start();*/
                    break;

                case R.id.button_prev :
                   /* player.stop();
                    i--;
                    player.reset();
                    if (i < 0) {
                        i=mp3List.size()-1;
                    }

                    mp3 = mp3List.get(i).toString();
                    music_listview.setItemChecked(i,true);
                    try {
                        player.setDataSource(musicPath + mp3);
                        player.prepare();
                    } catch (IOException e) {

                    }

                    player.start();*/
                    break;
            }
            sendBroadcast(intent);
        }
    }

    private void setPermission() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            bReadPerm = true;
        }

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == 	PackageManager.PERMISSION_GRANTED) {
            bWritePerm = true;
        }

        if(!bReadPerm && !bWritePerm) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 200);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 200 && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bReadPerm = true;
            }
            if(grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                bWritePerm = true;
            }
        }
    }
}