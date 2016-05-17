package com.example.sungkoo.directcam;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends Activity {
    private static String TAG = "CAMERA";
    private static int count;
    private Context mContext = this;
    private Camera mCamera;
    private CameraPreview mPreview;
    public static String mediapath;

    ServerSocket    serverSocket= null;
    Socket          clientSocket= null;

    BufferedOutputStream bos;// = new BufferedOutputStream(clientSocket.getOutputStream());
    DataOutputStream imagedata;// = new DataOutputStream(bos);

    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mCamera.takePicture(null, null, mPicture);

        }

    };




    Thread  thread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        count=0;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_server);
        mContext = this;

        // 카메라 사용여부 체크
        if(!checkCameraHardware(getApplicationContext())){
            finish();
        }

        // 카메라 인스턴스 생성
        mCamera = getCameraInstance();

        // 프리뷰창을 생성하고 액티비티의 레아이웃으로 지정
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        mCamera.startPreview();

        thread= new Thread(new Runnable(){

            @Override
            public void run() {
                try{
                    Log.d("jmlee", "wait for accept");

                    clientSocket = serverSocket.accept();
                    bos = new BufferedOutputStream(clientSocket.getOutputStream());
                    imagedata = new DataOutputStream(bos);

                    Log.d("jmlee", "after for accept");
                } catch (IOException e) {
                     Log.d("jmlee", "error" + e.toString());
                }
                int ticker=0;

                while(true) {

                    //count= count%5;
                    count++;
                    try {
                        Thread.sleep(45);
                        ticker++;
                        Log.d(TAG,"count="+count);
                    } catch (InterruptedException e) {
                        Log.d("inter",e.toString());
                        break;
                    }
                    //mCamera.startPreview();

                    //mCamera.takePicture(null, null, mPicture);

                    if(ticker%1==0) {
                       handler.sendEmptyMessage(1);

                    }

                }

            }
        });

        WifiManager wifiManager = (WifiManager) getSystemService(Activity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        int ip = wifiInfo.getIpAddress();

        String ipString = String.format(
                "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        Toast.makeText(getApplicationContext(),ipString,Toast.LENGTH_LONG).show();

        try {
            serverSocket = new ServerSocket(5000);
        }catch(IOException e){

        }
        thread.start();


        // 촬영버튼 등록
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread.interrupt();
                /*
                if(count<5)
                count++;
                else
                count=0;
                mCamera.takePicture(null,null,mPicture);
                */
            }
        });

    }

    /**
     * 카메라 사용여부 가능 체크
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.i(TAG, "Number of available camera : " + Camera.getNumberOfCameras());
            return true;
        } else {
            Toast.makeText(context, "No camera found!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 카메라 인스턴스 호출
     * @return
     */
    public Camera getCameraInstance(){
        try{
            // open() 의 매개변수로 int 값을 받을 수 도 있는데, 일반적으로 0이 후면 카메라, 1이 전면 카메라를 의미합니다.
            mCamera = Camera.open();
        }catch(Exception e){
            Log.i(TAG,"Error : Using Camera");
            e.printStackTrace();
        }
        return mCamera;
    }

    /** 이미지를 저장할 파일 객체를 생성 
     * 저장되면 Picture 폴더에 MyCameraApp 폴더안에 저장된다. (MyCameraApp 폴더명은 변경가능)
     */
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
        String timestamp = "a" + count;
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timestamp );
        Log.i("MyCamera", "Saved at" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        System.out.println(mediaFile.getPath());
        mediapath = mediaFile.getPath();
        return mediaFile;
    }
    public String getMediapath(){
        return mediapath;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // JPEG 이미지가 byte[] 형태로 들어옵니다.
            Log.d(TAG, "PictureCallback");

            //clientSocket.getOutputStream();
            try {
                imagedata.writeInt(data.length);
                imagedata.write(data, 0, data.length);
                imagedata.flush();
                Log.d("length","length="+data.length);

            }catch (IOException e){
                Log.d("jmlee", e.toString());
            }
            mCamera.startPreview();
        }
    };
}