package com.android.citygroom;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class StreetrideSection extends Fragment {

    Button start;
    LocationManager locationManager;
    public StreetrideSection() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_streetride_section, container, false);
        start = v.findViewById(R.id.start_trip);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    Toast.makeText(getActivity(), "Please turn on your location!", Toast.LENGTH_LONG).show();

                }
                else startActivity(new Intent(getActivity(), RoadConditions.class));

            }
        });
        return v;
    }


}
