package com.example.sungkoo.directcam;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientActivirty extends AppCompatActivity {
    Socket socket = null;
    android.os.Handler handler = null;

    public int count;
    public static String mediapath;

    private static File getOutputMediaFile(int test){
        //SD ī�尡 ����Ʈ �Ǿ��ִ��� ���� Ȯ��
        // Environment.getExternalStorageState() �� ����Ʈ ���� Ȯ�� �����մϴ�
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

        // ���� ��ζ�� ���� ����
        if(!mediaStorageDir.exists()){
            if(! mediaStorageDir.mkdirs()){
                Log.d("MyCamera", "failed to create directory");
                return null;
            }
        }

        // ���ϸ��� ������ ����, ���⼱ �ð����� ���ϸ� �ߺ��� ���Ѵ�
        String timestamp = "aaa"+test;
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
        Log.i("MyCamera", "Saved at" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        System.out.println(mediaFile.getPath());
        mediapath = mediaFile.getPath();
        return mediaFile;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count=0;
        setContentView(R.layout.activity_client_activirty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        handler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(getApplicationContext(),"count="+msg.what, Toast.LENGTH_SHORT).show();
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Button ChangeButton = (Button)findViewById(R.id.ChangeButton);
        ChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread= new Thread(new Runnable() {
                    public void run() {
                         byte[] buffer = new byte[5000000];
                        Message msg= new Message();
                        try  {
                            int count = 0;
                            socket = new Socket("192.168.1.51", 5000);
                            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                            while (true) {

                                bis.read(buffer);
                                msg.what= count++;
                                //handler.sendMessage(msg);
                                Log.i("ymlee", "count="+count);
                            }
                        }

                        catch(
                        IOException e
                        )

                        {
                            Log.d("jmlee", e.toString());
                        }
                }
                });

                thread.start();







  /*
                if (count < 5)
                    count++;
                else
                    count = 0;


                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;

                Bitmap myBitmap = BitmapFactory.decodeFile(mediaStorageDir.getPath() + File.separator + "IMG_a"+count+".jpg", options);
                ImageView Image = (ImageView) findViewById(R.id.imageView);
                Image.setImageBitmap((myBitmap));

                Toast toast = Toast.makeText(getApplicationContext(), "IMG_a"+count+".jpg",Toast.LENGTH_SHORT);
                toast.show();
                */
            }
        });

        /*
        ImageView Image=(ImageView)findViewById(R.id.imageView);
        Image.setImageResource(R.drawable.icon);
        */


    }

}
