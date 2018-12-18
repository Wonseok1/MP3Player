package com.example.student.mp3ex;

import android.Manifest;
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
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    boolean bReadPerm = false;
    boolean bWritePerm = false;
    Button button_play, button_stop, button_next, button_prev;
    MediaPlayer player;

    String mp3;
    int i=0;
    ArrayList<String> mp3List;
    String musicPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermission();

        button_play = (Button)findViewById(R.id.button_play);
        button_stop = (Button)findViewById(R.id.button_stop);
        button_next = findViewById(R.id.button_next);
        button_prev = findViewById(R.id.button_prev);

        button_play.setOnClickListener(new MyButtonListener());
        button_stop.setOnClickListener(new MyButtonListener());
        button_next.setOnClickListener(new MyButtonListener());
        button_prev.setOnClickListener(new MyButtonListener());

        player = new MediaPlayer();

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

        mp3 = mp3List.get(i);

        if(bReadPerm && bWritePerm) {
            String state = Environment.getExternalStorageState();

            if (state.equals(Environment.MEDIA_MOUNTED)) {
                try {
                    player.setDataSource(musicPath+mp3);
                    player.prepare();
                    Log.d("PlayMp3", "mp3 file ");
                } catch (Exception e) {
                    Log.d("PlayMp3", "mp3 file error");
                }
            }
        }
    }

    class MyButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.button_play:
                    if(player.isPlaying()) {
                        player.pause();
                        button_play.setText("play");
                    } else {
                        player.start();
                        button_play.setText("pause");
                    }
                    break;
                case R.id.button_stop:
                    player.stop();
                    try {
                        player.prepare();
                    } catch (Exception e) {
                        Log.d("PlayMp3", "mp3 file error");
                    }
                    break;
                case R.id.button_next:
                    player.stop();
                    i++;
                    player.reset();
                    if (i > mp3List.size()-1) {
                        i=0;
                    }
                    mp3 = mp3List.get(i).toString();
                    try {
                        player.setDataSource(musicPath + mp3);
                        player.prepare();
                    } catch (IOException e) {

                    }

                    player.start();
                    break;

                case R.id.button_prev :
                    player.stop();
                    i--;
                    player.reset();
                    if (i < 0) {
                        i=mp3List.size()-1;
                    }

                    mp3 = mp3List.get(i).toString();
                    try {
                        player.setDataSource(musicPath + mp3);
                        player.prepare();
                    } catch (IOException e) {

                    }

                    player.start();
                    break;
            }
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