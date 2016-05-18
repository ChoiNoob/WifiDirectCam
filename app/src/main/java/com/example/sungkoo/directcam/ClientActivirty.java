package com.example.sungkoo.directcam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientActivirty extends AppCompatActivity{
    Socket socket = null;

    public int count;
    public static String mediapath;

    byte[]      picture=null;

    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(picture != null){
                Bitmap  bitmap= BitmapFactory.decodeByteArray(picture, 0, picture.length);
                ImageView   iv=(ImageView)findViewById(R.id.imageView);
                iv.setImageBitmap(bitmap);
            }

        }

    };


    private static File getOutputMediaFile(int test){
        //SD 카드가 마운트 되어있는지 먼저 확인
        // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

        // 없는 경로라면 따로 생성
        if(!mediaStorageDir.exists()){
            if(! mediaStorageDir.mkdirs()){
                Log.d("MyCamera", "failed to create directory");
                return null;
            }
        }

        // 파일명을 적당히 생성, 여기선 시간으로 파일명 중복을 피한다
        String timestamp = "aaa"+test;
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
        Log.i("MyCamera", "Saved at" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        System.out.println(mediaFile.getPath());
        mediapath = mediaFile.getPath();
        return mediaFile;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socket= SelectActivity.socket;

        setContentView(R.layout.activity_client_activirty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button ChangeButton = (Button) findViewById(R.id.ChangeButton);
        ChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        byte[] buffer = new byte[6000000];
                        BufferedInputStream bis = null;
                        FileOutputStream fos = null;
                        DataInputStream dis = null;
                        Message msg = new Message();
                        int test = 0;
                        try {

                            while(true) {
                                bis = new BufferedInputStream(socket.getInputStream());
                                dis = new DataInputStream(bis);

                                int length = (int)dis.readInt();
                                buffer = new byte[length];

                                int mnt=0;
                                while(mnt<length) {
                                    int len=dis.read(buffer, mnt, length-mnt);
                                    mnt += len;
                                }
                                Log.d("check read count", "mnt=" + mnt);
                                picture= buffer;
                                handler.sendEmptyMessage(1);
                            }
                        } catch (IOException e) {
                            Log.d("jmlee", e.toString());
                        } finally {
                            try {
                                bis.close();
                            } catch (IOException e) {
                            }
                        }

                    }

                });

                thread.start();


            }
        });

    }



}
