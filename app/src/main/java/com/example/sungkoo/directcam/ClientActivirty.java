package com.example.sungkoo.directcam;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ClientActivirty extends AppCompatActivity {

    public int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count=0;
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
        Button ChangeButton = (Button)findViewById(R.id.ChangeButton);
        ChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });

        /*
        ImageView Image=(ImageView)findViewById(R.id.imageView);
        Image.setImageResource(R.drawable.icon);
        */


    }

}
