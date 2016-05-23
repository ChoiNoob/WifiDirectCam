package com.example.sungkoo.directcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientActivirty extends AppCompatActivity {
    Socket socket = null;


    public static String mediapath;

    byte[] picture = null;
    byte[] b1=null;
    BufferedOutputStream bos;
    DataOutputStream dos;
    BufferedInputStream bis;
    FileOutputStream fos;
    DataInputStream dis;
    File pictureFile;
    Thread thread_button;
    Thread thread_save;
    Thread thread;
    Thread thread_capture;

    Button StartButton;
    Button CaptureButton;
    Button SaveButton;
    Button BackButton;

    //client image 소켓 받는 조건
    boolean receive = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;

            options.inSampleSize = 2;
            if (picture != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
                bitmap = imgRotate(bitmap);
                ImageView iv = (ImageView) findViewById(R.id.imageView);
                iv.setImageBitmap(bitmap);
                bitmap = null;
            }

        }

    };

    private Bitmap imgRotate(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;
    }

    private static File getOutputMediaFile(){
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
        String timestamp =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
        Log.i("MyCamera", "Saved at" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));

        System.out.println(mediaFile.getPath());
        mediapath = mediaFile.getPath();
        return mediaFile;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socket = SelectActivity.socket;


        setContentView(R.layout.activity_client_activirty);

        int[] maxTextureSize = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);

        //create시 receive=false;
        receive = false;


        try {
            bos = new BufferedOutputStream(socket.getOutputStream());
            dos = new DataOutputStream(bos);

            bis = new BufferedInputStream(socket.getInputStream());
            dis = new DataInputStream(bis);
        } catch (IOException e) {

        }
        SaveButton = (Button) findViewById(R.id.SaveButton);
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartButton.setVisibility(View.VISIBLE);
                        pictureFile = getOutputMediaFile();
                        try {
                            Log.d("Camerasaving", "Savingbefore");
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(picture);
                            fos.close();

                            Log.d("Camerasaving", "SavingAfter");

                            //thread_save.interrupt();
                        } catch (IOException e) {

                        }
                        //SaveButton.setEnabled(false);
                    SaveButton.setVisibility(View.GONE);
                    }
               //});
               // thread_save.start();

           // }
        });

        CaptureButton = (Button) findViewById(R.id.CaptureButton);
        CaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveButton.setVisibility(View.VISIBLE);
                BackButton.setVisibility(View.VISIBLE);

                try {
                    handler.sendEmptyMessage(1);

                    //client socket 막기
                    receive=false;

                    //server socket 전송 막기
                    dos.writeBoolean(false);
                    dos.flush();

                    thread.interrupt();
                    Log.d("bokyung","intent start");

                }catch (IOException e){}
                CaptureButton.setVisibility(View.GONE);
            }
        });
        StartButton = (Button) findViewById(R.id.Startbutton);
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartButton.setVisibility(View.GONE);
                CaptureButton.setVisibility(View.VISIBLE);
                thread_button = new Thread(new Runnable() {
                    @Override
                    public void run() {
                           /* BufferedOutputStream bos = null;
                            DataOutputStream dos = null;*/
                        //Boolean check=false;

                        try {
                            //socket 전송 받기
                            receive = true;

                            dos.writeBoolean(true);
                            dos.flush();
                            //check=true;
                            Log.d("bokyung", "thread gg");


                            while(true){
                                if(thread != null){
                                    thread.start();
                                    break;
                                }
                            }

                        } catch (IOException e) {

                        }

                    }
                });

                thread_button.start();


                thread = new Thread(new Runnable() {
                    public void run() {


                        byte[] buffer = null;
                        try {


                            while (receive) {

                                int length = dis.readInt();
                                buffer = new byte[length];

                                int mnt = 0;

                                while (mnt < length) {
                                    int len = dis.read(buffer, mnt, length - mnt);
                                    mnt += len;
                                }
                                //Log.d("check read count", "mnt=" + mnt);
                                picture = buffer;
                                b1=picture;
                                handler.sendEmptyMessage(1);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("jmlee", e.toString());
                        }/* finally {
                                try {
                                    bis.close();
                                } catch (IOException e) {
                                }
                            }*/

                    }

                });

                // thread.start();


            }
        });

        BackButton = (Button) findViewById(R.id.BackButton);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackButton.setVisibility(View.GONE);
                CaptureButton.setVisibility(View.VISIBLE);
                thread_button.start();

            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();

        finish();

    }
}
