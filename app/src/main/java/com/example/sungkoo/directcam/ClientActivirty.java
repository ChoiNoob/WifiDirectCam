package com.example.sungkoo.directcam;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
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
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientActivirty extends AppCompatActivity {
    Socket socket = null;
    android.os.Handler handler = null;

    public int count;
    public static String mediapath;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count = 0;
        setContentView(R.layout.activity_client_activirty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(getApplicationContext(), "count=" + msg.what, Toast.LENGTH_SHORT).show();
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
        Button ChangeButton = (Button) findViewById(R.id.ChangeButton);
        if (ChangeButton != null) {
            ChangeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Thread thread = new Thread(new Runnable() {
                        public void run() {
                            byte[] buffer = new byte[6000000];
                            BufferedInputStream bis = null;
                            FileOutputStream fos = null;
                            DataInputStream data_size = null;
                            Message msg = new Message();
                            int test=0;
                            try {

                                //Log.d("koo", "count=" + count);
                                int count = 0;

                                // if (!info.isGroupOwner)
                                socket = new Socket("192.168.0.33", 5000);
                                // else
                                //Log.d("socket", "ConnectError");
                                File pictureFile = getOutputMediaFile(test++);
                                Log.d("test","test="+test);
                                //InputStream in = socket.getInputStream();
                                bis = new BufferedInputStream(socket.getInputStream());
                                fos = new FileOutputStream(pictureFile);
                                data_size = new DataInputStream(bis);

                                int lengh=data_size.readInt();
                                // bos = new BufferedOutputStream(fos);

                                // OutputStream output = new FileOutputStream(pictureFile);
                                // ObjectOutputStream oos = new ObjectOutputStream(fos);
                                if(pictureFile == null){
                                    //Toast.makeText(mContext, "Error camera image saving", Toast.LENGTH_SHORT).show();
                                    return;
                                }



                                //Log.d("check read count", "int=" + mnt);
                                Log.d("check data_lengh","lengh="+lengh);

                                int mnt=0;
                                int read=0;
                                //Log.d("check read counttest", "int="+mnt);
                                while (read<=lengh) {
                                    //FileOutputStream fos = new FileOutputStream(pictureFile);
                                    //ObjectOutputStream oos = new ObjectOutputStream(fos);
                                    Log.d("check read count", "int=" + mnt);
                                    Log.d("check data_lengh", "lengh=" + lengh);
                                    //fos.write(buffer, 0, mnt);
                                    //bis.close();
                                    //fos.close();
                                    // bos.close();
                                    mnt=data_size.read(buffer);
                                    read+=mnt;
                                    fos.write(buffer, 0, mnt);

                                    msg.what = count++;
                                    //oos.writeObject(buffer);
                                    //bos.write(buffer,0,mnt);
                                    //bos.flush();
                                    //fos.write(buffer);
                                    //fos.close();
                                    //handler.sendMessage(msg);



                                    Log.i("ymlee", "count=" + count);

                                }
                                fos.write(buffer,0, mnt);
                                fos.flush();

                            }catch (IOException e){
                                Log.d("jmlee", e.toString());
                            }

                            finally {
                                try{bis.close();}catch(IOException e){}
                                try{fos.close();}catch(IOException e){}
                                //    try{bos.close();}catch(IOException e){}
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
        }

        /*
        ImageView Image=(ImageView)findViewById(R.id.imageView);
        Image.setImageResource(R.drawable.icon);
        */


    }


}
