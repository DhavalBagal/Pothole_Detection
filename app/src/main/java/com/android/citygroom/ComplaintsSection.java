package com.android.citygroom;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComplaintsSection extends Fragment implements ValueEventListener{


    RecyclerView recyclerView;
    MyComplaintAdapter adapter;
    DatabaseReference rootref;
    ArrayList<Complaint> complaintList = new ArrayList<>(), templist = new ArrayList<>();
    Complaint comp;
    private String user_email;

    public ComplaintsSection() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_complaints_section, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        adapter = new MyComplaintAdapter(getActivity(), complaintList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        user_email = getActivity().getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE)
                .getString("UserId_Email","default");

        rootref = FirebaseDatabase.getInstance().getReference("COMPLAINTS");

        rootref.addValueEventListener(this);

        return view;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        templist.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            comp = ds.getValue(Complaint.class);

            if(comp.User_Email.endsWith(user_email))
            {
                templist.add(comp);

            }
        }
        complaintList.clear();
        complaintList.addAll(templist);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

}

