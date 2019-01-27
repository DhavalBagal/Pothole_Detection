package com.android.citygroom;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class IssuesSection extends Fragment {


    public IssuesSection() {
        // Required empty public constructor
    }

    ImageButton pothole, cracks, pavement, manholes, drainage, obstructions, guardrail, signs, lights;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_issues_section, container, false);

        pothole = v.findViewById(R.id.pothole_comp);
        cracks = v.findViewById(R.id.cracks_comp);
        pavement = v.findViewById(R.id.pavements_comp);
        manholes = v.findViewById(R.id.manhole_comp);
        drainage = v.findViewById(R.id.drainage_comp);
        obstructions = v.findViewById(R.id.obstruction_comp);
        guardrail = v.findViewById(R.id.guardrail_comp);
        signs = v.findViewById(R.id.streetsigns_comp);
        lights = v.findViewById(R.id.streetlights_comp);



        pothole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Potholes");
            }
        });

        cracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Road Cracks");
            }
        });

        pavement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Pavements");
            }
        });

        manholes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Manholes");
            }
        });

        drainage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Drainage");
            }
        });

        obstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Obstructions");
            }
        });

        guardrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Guardrails");
            }
        });

        signs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Road Signs");
            }
        });

        lights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent("Street Lights");
            }
        });


        return v;
    }

    private void sendIntent(String str)
    {
        Intent intent = new Intent(getActivity(), NewComplaint.class);
        intent.putExtra("category",str);
        startActivity(intent);
    }

}
