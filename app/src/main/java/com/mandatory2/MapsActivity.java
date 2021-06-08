package com.mandatory2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.mandatory2.adapter.MyAdapter;
import com.mandatory2.databinding.ActivityMapsBinding;
import com.mandatory2.model.ClusterMarker;
import com.mandatory2.model.Snapinfo;
import com.mandatory2.repo.Repos;
import com.mandatory2.util.MyClusterManagerRenderer;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Updatable {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    List<Snapinfo> snaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Repos.r().setup(this, snaps);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng kbh = new LatLng(55.6413, 12.6504);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kbh,10));
        addMapMarkers();
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterMarker item) {
                Intent intent = new Intent(MapsActivity.this, SnapDetailsActivity.class);
                System.out.println("pressed info");

                intent.putExtra("snapid", item.getSnap().id);
                intent.putExtra("url", item.getSnap().imageURL);
                mClusterManager.removeItem(item);
                mClusterManager.cluster();

                startActivity(intent);
            }
        });



    }
    private void addMapMarkers(){

        if(mMap != null){

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getApplicationContext(),
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for(Snapinfo snapinfo: snaps){



                int avatar = R.drawable.snapchat;  // set the default avatar
                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(snapinfo.imageLoc.getLatitude(), snapinfo.imageLoc.getLongitude()),
                            "SNAP",
                            snapinfo.getImageName(),
                            avatar,
                            snapinfo

                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);



            }
            mClusterManager.cluster();

        }

    }

    @Override
    public void update(Object o) {

    }

}
