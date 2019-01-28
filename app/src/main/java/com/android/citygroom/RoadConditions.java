package com.android.citygroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static java.lang.System.currentTimeMillis;

public class RoadConditions extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, SensorEventListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    //private Location lastLocation;
    //private Marker currentLocationMArker;


    private TextView lat, lng;
    Location location;
    private LocationManager locationManager;

    private SensorManager sensorMan;
    private Sensor accelerometer;

    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    long current_time, last_time, dt;
    float depth;
    TextView bumps;
    int bump;

    DatabaseReference rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_conditions);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        current_time = 0;
        last_time = 0;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        bump = 0;

        bumps = findViewById(R.id.no_of_bumps);

        rootref = FirebaseDatabase.getInstance().getReference("BUMPS");



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

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

                rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        Bumps b;
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            b = ds.getValue(Bumps.class);
                            LatLng pos = new LatLng(Double.parseDouble(b.Bump_Latitude), Double.parseDouble(b.Bump_Longitude));
                            mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

    }


    protected synchronized void buildGoogleApiClient()
    {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        mMap.animateCamera(CameraUpdateFactory.zoomBy(30));

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) (Math.sqrt(x * x + y * y + z * z));
            final float delta = Math.abs(mAccelCurrent - mAccelLast);

            if (delta > 10) {
                current_time = currentTimeMillis();
                dt = current_time - last_time;
                depth = (float) ((mAccelCurrent + mAccelLast) * Math.pow(dt, 2)) / 4;
                depth = depth / 1000000;
                if (depth < 0.1)
                {
                    List<Address> addresses;
                    final String ts, location_name, latitude, longitude, user_email, loc_category;
                    bump = bump + 1;
                    location = LocationServices.FusedLocationApi.getLastLocation(client);

                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);


                        user_email = userDetails.getString("UserId_Email","default");
                        ts = sdf.format(timestamp);
                        location_name = addresses.get(0).getAddressLine(0);

                        loc_category = getLocCategory(location_name);
                        DecimalFormat df= new DecimalFormat("#0.00000");

                        latitude = df.format(location.getLatitude())+"";
                        longitude = df.format(location.getLongitude())+"";

                        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                String bumpid = latitude+" "+longitude;
                                bumpid = bumpid.replace(".",",");

                                Bumps bump = new Bumps(bumpid, user_email, depth+"",
                                        delta+"",location_name, latitude, longitude, loc_category, ts);

                                if(!dataSnapshot.child(bumpid).exists())
                                {
                                    rootref.child(bumpid).setValue(bump);
                                }
                                else
                                {
                                    rootref.child(bumpid).removeValue();
                                    rootref.child(bumpid).setValue(bump);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }



                bumps.setText(bump+"");

            } else
                last_time = currentTimeMillis();
        }

    }

    private String getLocCategory(String location_name)
    {
        if(location_name.toLowerCase().contains("school"))
            return "school";
        else if(location_name.toLowerCase().contains("highway"))
            return "highway";
        else if(location_name.toLowerCase().contains("institute"))
            return "institue";
        else if(location_name.toLowerCase().contains("college"))
            return "college";
        else if(location_name.toLowerCase().contains("mall"))
            return "mall";
        else if(location_name.toLowerCase().contains("hospital"))
            return "hospital";
        else if(location_name.toLowerCase().contains("station"))
            return "station";
        else if(location_name.toLowerCase().contains("airport"))
            return "airport";
        else if(location_name.toLowerCase().contains("metro"))
            return "metro";
        else if(location_name.toLowerCase().contains("court"))
            return "court";

        return "";
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);

    }


}
