package com.mandatory2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mandatory2.adapter.MyAdapter;
import com.mandatory2.model.Snapinfo;
import com.mandatory2.repo.Repos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



import static android.graphics.Bitmap.CompressFormat.JPEG;

public class MainActivity extends AppCompatActivity implements Updatable {
ImageView imageView;
Button btOpen;
Button btlist;
EditText editText;
TextView fbName;
MyAdapter myAdapter;
Button btOpenMaps;
LoginButton loginButton;
LocationManager locationManager;
FusedLocationProviderClient fusedLocationProviderClient;
List<Snapinfo> items = new ArrayList<>();
GeoPoint temp;
double longitude;
double latitude;
private FirebaseStorage storage;
private StorageReference storageReference;
private DatabaseReference databaseReference;
private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        imageView = findViewById(R.id.image_view);
        btOpen = findViewById(R.id.bt_open);
        btlist = findViewById(R.id.bt_openLists);
        fbName = findViewById(R.id.fbName);
        editText = findViewById(R.id.editName);
        loginButton = findViewById(R.id.login_button);
        btOpenMaps = findViewById(R.id.bt_openMaps);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        callbackManager = CallbackManager.Factory.create();

loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
    @Override
    public void onSuccess(LoginResult loginResult) {
        System.out.println("logged in");
        Log.d("demo", "onSuccess: ");

    }

    @Override
    public void onCancel() {
        System.out.println("canceled");
    }

    @Override
    public void onError(FacebookException error) {
        Log.d("demo", "onError: ");
        System.out.println("error on logging in");
    }
});

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        });


        btlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SnapsActivity.class));
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
       btOpenMaps.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,MapsActivity.class));
           }
       });
    }
    public Bitmap drawTextToBitmap(Bitmap image, String gText) {
        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }

        image = image.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(161, 161, 161));
        paint.setTextSize((int) (20)); // text size in pixels
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        canvas.drawText(gText, 10, 100, paint);
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100)  {

            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            String text = editText.getText().toString();
            String fbname = (String) fbName.getText();
            Bitmap manipulated = drawTextToBitmap(captureImage,text);
            imageView.setImageBitmap(manipulated);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Want to send this snap?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            uploadPicture(manipulated,fbname);
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            imageView.setImageDrawable(null);
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


        } else{
callbackManager.onActivityResult(requestCode,resultCode,data);
GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {
        try {
            fbName.setText(object.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
});
Bundle bundle = new Bundle();
bundle.putString("fields","name");
graphRequest.setParameters(bundle);
graphRequest.executeAsync();
        }

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
if (currentAccessToken== null){
    fbName.setText("Anonymous user");
}
            }
        };

    }

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
             GeoPoint tempgeo = new GeoPoint(latitude,longitude);
String temp = "images/" + randomKey;
                Repos.r().addNote(text,temp,tempgeo);

            }
        }).addOnFailureListener(exception -> {
            System.out.println("failure to upload " + exception);
        });}

    @Override
    public void update(Object o) {
myAdapter.notifyDataSetChanged();
    }


}