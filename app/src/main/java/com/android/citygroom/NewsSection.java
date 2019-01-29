package com.android.citygroom;


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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsSection extends Fragment implements ValueEventListener{
    RecyclerView recyclerView;
    NewsAdapter adapter;
    DatabaseReference newsref;
    ArrayList<NewsArticle> newsArticleList = new ArrayList<>(), templist = new ArrayList<>();
    NewsArticle news;

    public NewsSection() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_news_section, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new NewsAdapter(getActivity(), newsArticleList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        newsref = FirebaseDatabase.getInstance().getReference("NEWS");

        newsref.addValueEventListener(this);




        return view;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
    {
        templist.clear();

        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            news = ds.getValue(NewsArticle.class);
            templist.add(news);
        }

        newsArticleList.clear();
        newsArticleList.addAll(templist);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }



}
