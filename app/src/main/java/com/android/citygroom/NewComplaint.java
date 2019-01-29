package com.android.citygroom;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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
    RequestQueue queue;
    JsonObjectRequest req;
    JSONObject postparams;
    EditText area_txtbx;
    EditText width_txtbx;
    String url, mCurrentPhotoPath, imageFileName, str;
    File image;
    Bitmap cameraBitmap;
    private String latstring;
    private String longstring;
    List<Address> addresses;
    private String location_name;
    private String loc_category, user_email, ts, comp_category, status, length, width, area, severity, descrip;
    String loc1_category, loc2_category, loc3_category,loc4_category,loc5_category, loc6_category;
    String loc7_category, loc8_category, loc9_category, loc10_category, loc11_category, loc12_category;
    String loc13_category, loc14_category, loc15_category;
    private String loc1_name, loc2_name, loc3_name, loc4_name, loc5_name, loc6_name, loc7_name, loc8_name;
    private String loc9_name, loc10_name, loc11_name, loc12_name, loc13_name, loc14_name, loc15_name;

    DatabaseReference rootref;
    private EditText descrip_txtbx, length_txtbx, severity_txtbx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);


        //this is the url where you want to send the request
        url = "http://192.168.0.101:5000/getparams";

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        str = intent.getStringExtra("category");

        addimg = findViewById(R.id.add_image_btn);
        go = findViewById(R.id.go_btn);

        lat = findViewById(R.id.lat_txtbx);
        descrip_txtbx = findViewById(R.id.descrip_txtbx);
        lng = findViewById(R.id.long_txtbx);
        area_txtbx = findViewById(R.id.area_txtbx);
        width_txtbx = findViewById(R.id.width_txtbx);
        length_txtbx = findViewById(R.id.length_txtbx);
        severity_txtbx = findViewById(R.id.severity_txtbx);

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


        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        user_email = userDetails.getString("UserId_Email","default");

        comp_category = str;
        status = "Lodged";

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                rootref = FirebaseDatabase.getInstance().getReference("COMPLAINTS");

                rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        ts = sdf.format(timestamp);

                        length = String.valueOf(length_txtbx.getText());
                        descrip = String.valueOf(descrip_txtbx.getText());

                        width = String.valueOf(width_txtbx.getText());
                        area = String.valueOf(area_txtbx.getText());



                        String id = latstring+" "+longstring+" "+user_email;
                        id = id.replace(".",",");

                        if(!dataSnapshot.child(id).exists())
                        {

                            Map<String, String> map = new HashMap<>();

                            map.put("Location-1 : ",loc1_name);
                            map.put("Location-2 : ",loc2_name);
                            map.put("Location-3 : ",loc3_name);
                            map.put("Location-4 : ",loc4_name);
                            map.put("Location-5 : ",loc5_name);
                            map.put("Location-6 : ",loc6_name);
                            map.put("Location-7 : ",loc7_name);
                            map.put("Location-8 : ",loc8_name);
                            map.put("Location-9 : ",loc9_name);
                            map.put("Location-10 : ",loc10_name);
                            map.put("Location-11 : ",loc11_name);
                            map.put("Location-12 : ",loc12_name);
                            map.put("Location-13 : ",loc13_name);
                            map.put("Location-14 : ",loc14_name);
                            map.put("Location-15 : ",loc15_name);

                            Complaint comp = new Complaint(user_email, comp_category, ts, "0", status, length, width, area,
                                    "0", severity, descrip, location_name, latstring, longstring, loc_category, map);

                            rootref.child(id).setValue(comp);

                            //rootref.child(id).child("Nearby_Locations").setValue(map);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(NewComplaint.this, "Complaint registered successfully!!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(NewComplaint.this, AfterloginSection.class));

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

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    imageFileName = "JPEG_" + timeStamp + "_";
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    try {
                        image = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );
                        mCurrentPhotoPath = image.getAbsolutePath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (image != null) {
                        Uri photoURI = FileProvider.getUriForFile(NewComplaint.this,
                                "com.android.citygroom.fileprovider",
                                image);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    }
                }

            }
        });

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(NewComplaint.this);

        postparams = new JSONObject();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Bitmap cameraBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            Bitmap.createBitmap(cameraBitmap);

            ImageView imageView = findViewById(R.id.captured_img);
            imageView.setImageBitmap(cameraBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteFormat = stream.toByteArray();
            String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

            try {
                postparams.put("image", imgString);
                postparams.put("image_name", imageFileName+".jpg");
                req = new JsonObjectRequest(url, postparams,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response)
                            {

                                try {
                                    area = response.getString("area");
                                    width = response.getString("width");

                                    if(Double.parseDouble(area) < 900 &&  Double.parseDouble(area) > 0)
                                    {
                                        severity = "1";
                                    }
                                    else if(Double.parseDouble(area) < 3600 && Double.parseDouble(area) > 900)
                                    {
                                        severity = "2";
                                    }
                                    else if(Double.parseDouble(area) < 8100 && Double.parseDouble(area) > 3600)
                                    {
                                        severity = "3";
                                    }
                                    else if(Double.parseDouble(area) < 10000 && Double.parseDouble(area) > 8100)
                                    {
                                        severity = "4";
                                    }
                                    else if(Double.parseDouble(area) > 1000)
                                    {
                                        severity = "5";
                                    }

                                    area_txtbx.setText(area);
                                    width_txtbx.setText(width);
                                    severity_txtbx.setText(severity);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

                if (str.equals("Potholes"))
                {
                    queue.add(req);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng loc1 = new LatLng(latitude+0.005, longitude);
        LatLng loc2 = new LatLng(latitude, longitude+0.005);
        LatLng loc3 = new LatLng(latitude+0.005, longitude+0.005);

        LatLng loc4 = new LatLng(latitude+0.004, longitude);
        LatLng loc5 = new LatLng(latitude, longitude+0.004);
        LatLng loc6 = new LatLng(latitude+0.004, longitude+0.004);

        LatLng loc7 = new LatLng(latitude+0.003, longitude);
        LatLng loc8 = new LatLng(latitude, longitude+0.003);
        LatLng loc9 = new LatLng(latitude+0.003, longitude+0.003);

        LatLng loc10 = new LatLng(latitude+0.002, longitude);
        LatLng loc11= new LatLng(latitude, longitude+0.002);
        LatLng loc12 = new LatLng(latitude+0.002, longitude+0.002);

        LatLng loc13 = new LatLng(latitude+0.001, longitude);
        LatLng loc14 = new LatLng(latitude, longitude+0.001);
        LatLng loc15 = new LatLng(latitude+0.001, longitude+0.001);

        DecimalFormat df= new DecimalFormat("#0.00000");
        latstring = df.format(latitude)+"";
        longstring = df.format(longitude)+"";

        lat.setText(latstring);
        lng.setText(longstring);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            location_name = addresses.get(0).getAddressLine(0);
            loc_category = getLocCategory(location_name);

            loc1_name = geocoder.getFromLocation(loc1.latitude, loc1.longitude,1).get(0).getAddressLine(0);
            loc2_name = geocoder.getFromLocation(loc2.latitude, loc2.longitude,1).get(0).getAddressLine(0);
            loc3_name = geocoder.getFromLocation(loc3.latitude, loc3.longitude,1).get(0).getAddressLine(0);

            loc4_name = geocoder.getFromLocation(loc4.latitude, loc4.longitude,1).get(0).getAddressLine(0);
            loc5_name = geocoder.getFromLocation(loc5.latitude, loc5.longitude,1).get(0).getAddressLine(0);
            loc6_name = geocoder.getFromLocation(loc6.latitude, loc6.longitude,1).get(0).getAddressLine(0);

            loc7_name = geocoder.getFromLocation(loc7.latitude, loc7.longitude,1).get(0).getAddressLine(0);
            loc8_name = geocoder.getFromLocation(loc8.latitude, loc8.longitude,1).get(0).getAddressLine(0);
            loc9_name = geocoder.getFromLocation(loc9.latitude, loc9.longitude,1).get(0).getAddressLine(0);

            loc10_name = geocoder.getFromLocation(loc10.latitude, loc10.longitude,1).get(0).getAddressLine(0);
            loc11_name = geocoder.getFromLocation(loc11.latitude, loc11.longitude,1).get(0).getAddressLine(0);
            loc12_name = geocoder.getFromLocation(loc12.latitude, loc12.longitude,1).get(0).getAddressLine(0);

            loc13_name = geocoder.getFromLocation(loc13.latitude, loc13.longitude,1).get(0).getAddressLine(0);
            loc14_name = geocoder.getFromLocation(loc14.latitude, loc14.longitude,1).get(0).getAddressLine(0);
            loc15_name = geocoder.getFromLocation(loc15.latitude, loc15.longitude,1).get(0).getAddressLine(0);

            /*loc1_category = getLocCategory(loc1_name);
            loc2_category = getLocCategory(loc2_name);
            loc3_category = getLocCategory(loc3_name);

            loc4_category = getLocCategory(loc4_name);
            loc5_category = getLocCategory(loc5_name);
            loc6_category = getLocCategory(loc6_name);

            loc7_category = getLocCategory(loc7_name);
            loc8_category = getLocCategory(loc8_name);
            loc9_category = getLocCategory(loc9_name);

            loc10_category = getLocCategory(loc10_name);
            loc11_category = getLocCategory(loc11_name);
            loc12_category = getLocCategory(loc12_name);

            loc13_category = getLocCategory(loc13_name);
            loc14_category = getLocCategory(loc14_name);
            loc15_category = getLocCategory(loc15_name);*/

        } catch (IOException e) {
            e.printStackTrace();
        }



        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
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



}

