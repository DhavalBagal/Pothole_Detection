package com.android.citygroom;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


public class NewComplaint extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    Button addimg,go;
    private static final int CAMERA_REQUEST_CODE = 1;
    private TextView lat, lng;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        String str = intent.getStringExtra("category");

        addimg = findViewById(R.id.add_image_btn);
        go = findViewById(R.id.go_btn);

        lat = findViewById(R.id.lat_txtbx);
        lng = findViewById(R.id.long_txtbx);

        TextView category = findViewById(R.id.category_txtbx);
        category.setText(str);

        String potholes = new String("Potholes");
        String roadcracks = new String("Road Cracks");
        String pavements = new String("Pavements");
        String manholes = new String("Manholes");
        String drainage = new String("Drainage");
        String obstructions = new String("Obstructions");
        String guardrails = new String("Guardrails");
        String roadsigns = new String("Road Signs");
        String roadlights = new String("Street Lights");


        if (str!=null && str.equals("Potholes")==true) {
            TextView txt = (TextView) findViewById(R.id.length_txtbx);
            txt.setEnabled(false);
        }

        if (str!=null && str.equals("Road Cracks")) {
            TextView txt = (TextView) findViewById(R.id.width_txtbx);
            txt.setEnabled(false);
        }

        if (str!=null && str.equals("Pavements")) {
            TextView txt = (TextView) findViewById(R.id.length_txtbx);
            txt.setEnabled(false);
            TextView txt2 = (TextView) findViewById(R.id.width_txtbx);
            txt2.setEnabled(false);
        }

        if (str!=null && (str.equals("Manholes") || str.equals("Drainage") || str.equals("Obstructions") || str.equals("Guardrails") || str.equals("Road Signs") || str.equals("Street Lights"))) {
            TextView txt = (TextView) findViewById(R.id.length_txtbx);
            txt.setEnabled(false);
            TextView txt2 = (TextView) findViewById(R.id.width_txtbx);
            txt2.setEnabled(false);
            TextView txt3 = (TextView) findViewById(R.id.area_txtbx);
            txt3.setEnabled(false);
        }

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityCompat.requestPermissions(NewComplaint.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    buildAlertMessageNoGps();

                }
                else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {

                    if (ContextCompat.checkSelfPermission(NewComplaint.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        client = new GoogleApiClient.Builder(NewComplaint.this)
                                .addConnectionCallbacks(NewComplaint.this)
                                .addOnConnectionFailedListener(NewComplaint.this)
                                .addApi(LocationServices.API)
                                .build();

                        client.connect();
                    }
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView imageView = findViewById(R.id.captured_img);
            imageView.setImageBitmap(photo);


            /*ActivityCompat.requestPermissions(NewComplaint.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                buildAlertMessageNoGps();

            }
            else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                getLocation();
            }*/

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        lat.setText(latitude+"");
        lng.setText(longitude+"");

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {

    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /*private void getLocation() {
        if (ActivityCompat.checkSelfPermission(NewComplaint.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(NewComplaint.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(NewComplaint.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {


            LocationListener listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, listener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                String lattitude = String.valueOf(latti);
                String longitude = String.valueOf(longi);

                lat.setText(lattitude);
                lng.setText(longitude);
            } else {

                Toast.makeText(NewComplaint.this, "Unable to trace your location", Toast.LENGTH_SHORT).show();

            }
        }

    }*/

}

