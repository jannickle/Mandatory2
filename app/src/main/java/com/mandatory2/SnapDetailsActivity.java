package com.mandatory2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.mandatory2.model.Snapinfo;
import com.mandatory2.repo.Repos;

public class SnapDetailsActivity extends AppCompatActivity implements TaskListener {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snap_details);
        imageView = findViewById(R.id.imageView2);

        String imageUrl = getIntent().getStringExtra("url");
        Repos.r().getimage(imageUrl,this::receive);
        Repos.r().deleteimage(getIntent().getStringExtra("url"));
        Repos.r().deleteNote(getIntent().getStringExtra("snapid"));
    }

    @Override
    public void receive(byte[] bytes) {
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        imageView.setImageBitmap(bmp);
    }




}