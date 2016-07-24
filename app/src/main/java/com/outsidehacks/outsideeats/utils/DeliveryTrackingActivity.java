package com.outsidehacks.outsideeats.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.outsidehacks.outsideeats.MainActivity;
import com.outsidehacks.outsideeats.R;

public class DeliveryTrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location vendorLocation;
    private Long vendorId;
    private Long deliveryGuyId;
    private Marker vendorMarker;
    private Marker deliveryMarker;
    private DatabaseReference mDatabase;
    private LatLng deliveryLL = new LatLng(0, 0);


    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Log.d("here:", "poop");
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle bundle = getIntent().getExtras();
        vendorLocation = (Location) bundle.get("vendorLocation");
        vendorId = new Long(bundle.getInt("vendorId"));
        deliveryGuyId = new Long(bundle.getLong("deliveryGuyId"));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("DeliveryGuyId", deliveryGuyId.toString());
        mDatabase.child("delivery").child("not_free").child(String.valueOf(deliveryGuyId)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Child Added", "Initial Marker: " + dataSnapshot.toString());
                if (dataSnapshot.getKey().toString().equals("latitude")) {
                    deliveryLL = new LatLng((Double) dataSnapshot.getValue(), deliveryLL.longitude);
                } else if (dataSnapshot.getKey().toString().equals("longitude")) {
                    deliveryLL = new LatLng(deliveryLL.latitude, (Double) dataSnapshot.getValue());
                }
                deliveryMarker.setPosition(deliveryLL);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("Child Changed", dataSnapshot.toString());
                if (dataSnapshot.getKey().toString().equals("latitude")) {
                    deliveryLL = new LatLng((Double) dataSnapshot.getValue(), deliveryLL.longitude);
                } else if (dataSnapshot.getKey().toString().equals("longitude")) {
                    deliveryLL = new LatLng(deliveryLL.latitude, (Double) dataSnapshot.getValue());
                }
                deliveryMarker.setPosition(deliveryLL);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (deliveryMarker.isVisible()) {
                    deliveryMarker.remove();
                }
                Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                goToMainActivity.putExtra("ShowThanks", true);
                startActivity(goToMainActivity);

                //delivery completed Alert
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        // Add a marker in Sydney and move the camera
        LatLng vendorLL = new LatLng(vendorLocation.getLatitude(), vendorLocation.getLongitude());

//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(vendorLL));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        vendorMarker = mMap.addMarker(new MarkerOptions().position(vendorLL).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("food", 125, 125))));
        // add vendor title
        deliveryMarker = mMap.addMarker(new MarkerOptions().position(deliveryLL).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pegman", 125, 125))));
    }

}
