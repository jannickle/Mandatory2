package com.mandatory2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mandatory2.adapter.MyAdapter;
import com.mandatory2.model.Snapinfo;
import com.mandatory2.repo.Repos;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.graphics.Bitmap.CompressFormat.JPEG;

public class MainActivity extends AppCompatActivity implements Updatable {
ImageView imageView;
Button btOpen;
Button btlist;
EditText editText;
MyAdapter myAdapter;
List<Snapinfo> items = new ArrayList<>();
private FirebaseStorage storage;
private StorageReference storageReference;
private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        btOpen = findViewById(R.id.bt_open);
        btlist = findViewById(R.id.bt_openLists);
        editText = findViewById(R.id.editName);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //Repos.r().setup(this, items);
        btlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SnapsActivity.class));
            }
        });

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },
                    100);
        }
        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
    }
//    public void getTextforImage() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Add text to the image");
//
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//        builder.setPositiveButton("Done", (dialog, which) -> )
//    }
    public Bitmap drawTextToBitmap(Bitmap image, String gText) {
        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        image = image.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// new antialised Paint
        paint.setColor(Color.rgb(161, 161, 161));
        paint.setTextSize((int) (20)); // text size in pixels
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // text shadow
        canvas.drawText(gText, 10, 100, paint);
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100)  {

            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            String text = editText.getText().toString();
            Bitmap manipulated = drawTextToBitmap(captureImage,text);
            imageView.setImageBitmap(manipulated);
            uploadPicture(manipulated, text);
        }

    }
    /** Den er metode er essensiel da den skaber min logik til både min tekst og mit billede hænger sammen,
     * den er lidt bruteforced og ikke så kønt men den virker godt. når jeg uploader billede giver jeg den randomkey
     * som refference som så vil være den sti den kommer til at ligge i firebase. Den bliver genereret
     * ved hjælp af UUID. den måde jeg kæder min tekst og billede sammen er at i Title/note er et felt
     * jeg har kaldt url som når efter jeg har gemt billedet og har dens stig/url så ligger jeg den url ind
     * i Title url felt så jeg senere kan bruge den til at kalde det rigtige billede frem*/
    public void uploadPicture(Bitmap bitmap,String text) {
        System.out.println("uploadBitmap called " + bitmap.getByteCount());
        final String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/" + randomKey);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(JPEG, 100, baos);
        ref.putBytes(baos.toByteArray()).addOnCompleteListener(snap -> {
            System.out.println("OK to upload " + snap.getResult().toString());
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
String temp = "images/" + randomKey;
                Repos.r().addNote(text,temp);

            }
        }).addOnFailureListener(exception -> {
            System.out.println("failure to upload " + exception);
        });}

    @Override
    public void update(Object o) {
myAdapter.notifyDataSetChanged();
    }
}