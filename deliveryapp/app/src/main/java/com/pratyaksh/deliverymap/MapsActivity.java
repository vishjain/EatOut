package com.pratyaksh.deliverymap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.support.annotation.IntegerRes;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.contextmanager.internal.InterestUpdateBatchImpl;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.nearby.messages.EddystoneUid;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private DatabaseReference mDatabase;

    private Long myID = Long.parseLong("3");

    final HashMap<Long, LatLng> vendors = new HashMap<Long, LatLng>();

    final HashMap<Long, Marker> vendorMarkers = new HashMap<Long, Marker>();

    private Long assignedVendor;

    private String assignedVendorName;

    private LatLng currentLL;// = new LatLng(37.768623, -122.495930);

    private Marker currentMarker;

    private LatLng customerLL;

    private Marker customerMarker;

    private String orderText;

    private Long customerId;

    private String myName;

    private int free = 1;

    private ArrayList<Polyline> path = new ArrayList<Polyline>();

    private String orderKey;

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Log.d("here:", "poop");
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();



        mDatabase.child("delivery").child("free").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Long id = Long.parseLong(dataSnapshot1.getKey());
                            if (id.equals(myID)) {
                                myName = (String) dataSnapshot1.child("name").getValue();
                                Double latitude = (Double) dataSnapshot1.child("latitude").getValue();
                                Double longitude = (Double) dataSnapshot1.child("longitude").getValue();
                                currentLL = new LatLng(latitude, longitude);
                                currentMarker = mMap.addMarker(new MarkerOptions().position(currentLL).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("current", 25, 25))));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("databaseError", "getDelivery:onCancelled", databaseError.toException());
                    }
                }
        );
        mDatabase.child("vendors").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
//                        User user = dataSnapshot.getValue(User.class);
                        Log.d("Delivery Guy", "inside data change");
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Long vendorId = Long.parseLong(dataSnapshot1.getKey());
                            String name = (String) dataSnapshot1.child("name").getValue();
                            double latitude = (Double) dataSnapshot1.child("latitude").getValue();
                            double longitude = (Double) dataSnapshot1.child("longitude").getValue();
                            LatLng guy = new LatLng(latitude, longitude);
                            vendors.put(vendorId, guy);
                            vendorMarkers.put(vendorId, mMap.addMarker(new MarkerOptions().position(guy).title(name)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(guy));

                            Log.d("Delivery guy", latitude + " " + longitude);
                        }

                        Log.d("Fetched vendors", "from firebase");
                        Log.d("VendorsLong:", vendors.toString());
                        LatLng l1 = vendors.get(new Long(1)), l2 = vendors.get(new Long(3));
                        Log.d("Latlon1", l1.toString());
                        Log.d("Latlon2", l2.toString());

                        mDatabase.child("orders").addChildEventListener(childEventListener);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("databaseError", "getUser:onCancelled", databaseError.toException());
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

        //Current location stuff...
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);


        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d("Click", marker.getId());
                if (marker != null && customerMarker != null && customerMarker.getId().equals(marker.getId())) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                    alertDialogBuilder
                            .setTitle("Deliver Order?")
                            .setMessage(orderText)
//                            .setCancelable(false)
                            .setPositiveButton("Yeah, deliver!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //TODO: check distance of delivery guy from vendor?

                                    customerMarker.remove();
                                    customerMarker = null;
                                    customerLL = null;

                                    //delete path to customer
                                    Iterator it = path.iterator();

                                    while (it.hasNext()) {
                                        Polyline p = (Polyline) it.next();
                                        p.remove();
                                        it.remove();
                                    }

                                    //set the delivery guy free
                                    free = 1;
                                    Map<String, Object> deleteUpdates = new HashMap<String, Object>();
                                    deleteUpdates.put("/" + myID.toString() + "/name", null);
                                    deleteUpdates.put("/" + myID.toString() + "/" + "latitude", null);
                                    deleteUpdates.put("/" + myID.toString() + "/longitude", null);
                                    mDatabase.child("delivery").child("not_free").updateChildren(deleteUpdates);

                                    Map<String, Object> childUpdates = new HashMap<String, Object>();
                                    childUpdates.put("/" + myID.toString() + "/" + "name", myName);
                                    childUpdates.put("/" + myID.toString() + "/" + "latitude", currentLL.latitude);
                                    childUpdates.put("/" + myID.toString() + "/" + "longitude", currentLL.longitude);
                                    mDatabase.child("delivery").child("free").updateChildren(childUpdates);

                                    //set the delivery completed
                                    mDatabase.child("orders").child(orderKey).child("delivered").setValue(true);
                                }
                            })
                            .setNegativeButton(
                                    "No, not yet", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    }
                            );

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else if (marker != null && assignedVendor != null && vendorMarkers.get(assignedVendor).getId().equals(marker.getId())) {
                    Log.d("CorrectClick", marker.getId());
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                    alertDialogBuilder
                            .setTitle("Pickup Order?")
                            .setMessage(orderText)
//                            .setCancelable(false)
                            .setPositiveButton("Yeah, pick up!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //TODO: check distance of delivery guy from vendor?

                                    customerMarker = mMap.addMarker(new MarkerOptions().title(customerId.toString()).snippet(orderText).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).position(customerLL));
                                    Marker m = vendorMarkers.get(assignedVendor);
                                    m.setIcon(BitmapDescriptorFactory.defaultMarker());
                                    m.setTitle(assignedVendorName);
                                    m.setSnippet(null);
                                    m.hideInfoWindow();
                                    assignedVendor = null;
                                    assignedVendorName = null;

                                    //delete path to vendor
                                    Iterator it = path.iterator();

                                    while (it.hasNext()) {
                                        Polyline p = (Polyline) it.next();
                                        p.remove();
                                        it.remove();
                                    }

                                    //Route to customer
                                    GoogleDirection.withServerKey("AIzaSyCwZFsxHCiUy-XHiVW6143CMYi73_Ok70E")
                                            .from(currentLL)
                                            .to(customerLL)
//                                .from(new LatLng(37.7681994, -122.444538))
//                                .to(new LatLng(37.7749003,-122.4034934))
//                                .avoid(AvoidType.FERRIES)
//                                .avoid(AvoidType.HIGHWAYS)
                                            .transportMode(TransportMode.WALKING)
                                            .execute(new DirectionCallback() {
                                                @Override
                                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                                    Log.d("Directions", direction.toString());
                                                    if (direction.isOK()) {


                                                        Route route = direction.getRouteList().get(0);
                                                        Leg leg = route.getLegList().get(0);
                                                        ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.BLUE);
                                                        path.add(mMap.addPolyline(polylineOptions));
                                                        Log.d("Directions OK", route.toString());


                                                    } else {
//                                            Route route = direction.getRouteList().get(0);
//                                            Leg leg = route.getLegList().get(0);
//                                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
//                                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED);
//                                            mMap.addPolyline(polylineOptions);
                                                        Log.d("Directions NOT OK", "1");
                                                        // Do something
                                                    }
                                                }

                                                @Override
                                                public void onDirectionFailure(Throwable t) {
                                                    // Do something
                                                }
                                            });


                                }
                            })
                            .setNegativeButton(
                                    "No, not yet", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    }
                            );

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            }
        });

    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//            try {
            if (dataSnapshot.hasChild("deliveryGuyId")) {


                Long deliveryGuy = (Long) dataSnapshot.child("deliveryGuyId").getValue();
                Log.d("DeliverGuy:", deliveryGuy.toString());
                Boolean delivered = (Boolean) dataSnapshot.child("delivered").getValue();
                if (deliveryGuy.equals(myID) && !delivered) {
                    orderKey = (String) dataSnapshot.getKey();
                    free = 0;
                    // move delivery guy from free to not free
                    Map<String, Object> deleteUpdates = new HashMap<String, Object>();
                    deleteUpdates.put("/" + myID.toString() + "/name", null);
                    deleteUpdates.put("/" + myID.toString() + "/" + "latitude", null);
                    deleteUpdates.put("/" + myID.toString() + "/longitude", null);
                    mDatabase.child("delivery").child("free").updateChildren(deleteUpdates);

                    Map<String, Object> childUpdates = new HashMap<String, Object>();
                    childUpdates.put("/" + myID.toString() + "/" + "name", myName);
                    childUpdates.put("/" + myID.toString() + "/" + "latitude", currentLL.latitude);
                    childUpdates.put("/" + myID.toString() + "/" + "longitude", currentLL.longitude);
                    mDatabase.child("delivery").child("not_free").updateChildren(childUpdates);

                    Long vendorId = (Long) dataSnapshot.child("vendorId").getValue();
                    LatLng vendor = vendors.get(vendorId);
                    Log.d("Vendor: ", Long.toString(vendorId));
                    Double latitude = (Double) dataSnapshot.child("latitude").getValue();
                    Double longitude = (Double) dataSnapshot.child("longitude").getValue();
                    customerLL = new LatLng(latitude, longitude);
                    customerId = (Long) dataSnapshot.child("userId").getValue();
                    assignedVendor = vendorId;
                    Marker marker = vendorMarkers.get(assignedVendor);
//                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker", 100, 100)));

                    // Filling the infowindow
                    HashMap<String, Long> hm = (HashMap<String, Long>) dataSnapshot.child("items").getValue();
                    Iterator it = hm.entrySet().iterator();
                    String markerText = "";
                    assignedVendorName = marker.getTitle();

                    while (it.hasNext()) {
                        HashMap.Entry pair = (HashMap.Entry) it.next();
                        String key = (String) pair.getKey();
                        Long value = (Long) pair.getValue();
                        String qty = value.toString();
                        markerText = markerText + key + ": " + qty + "\n";
                    }
                    orderText = markerText;
                    marker.setSnippet(markerText);
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            LinearLayout info = new LinearLayout(getBaseContext());
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(getBaseContext());
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(getBaseContext());
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });
//                    marker

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                    alertDialogBuilder
                            .setMessage("Order Pickup from " + assignedVendorName)
                            .setCancelable(false)
                            .setPositiveButton("Cool!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
//                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker.png", 20, 20)));


                    // Start directions to vendor...
                    GoogleDirection.withServerKey("AIzaSyCwZFsxHCiUy-XHiVW6143CMYi73_Ok70E")
                            .from(currentLL)
                            .to(vendor)
//                                .from(new LatLng(37.7681994, -122.444538))
//                                .to(new LatLng(37.7749003,-122.4034934))
//                                .avoid(AvoidType.FERRIES)
//                                .avoid(AvoidType.HIGHWAYS)
                            .transportMode(TransportMode.WALKING)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                    Log.d("Directions", direction.toString());
                                    if (direction.isOK()) {


                                        Route route = direction.getRouteList().get(0);
                                        Leg leg = route.getLegList().get(0);
                                        ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED);
                                        path.add(mMap.addPolyline(polylineOptions));
                                        Log.d("Directions OK", route.toString());


                                    } else {
//                                            Route route = direction.getRouteList().get(0);
//                                            Leg leg = route.getLegList().get(0);
//                                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
//                                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED);
//                                            mMap.addPolyline(polylineOptions);
                                        Log.d("Directions NOT OK", "1");
                                        // Do something
                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Do something
                                }
                            });

                    // create popup on touch
                }
            }
//            } catch (Exception e) {
//                Log.d("OrderID Exception", e.toString());
//                Log.d("OrderID:", dataSnapshot.getKey().toString());
//            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d("DATABASE ERROR", databaseError.getMessage() + "  " + databaseError.getDetails());
        }
    };


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
