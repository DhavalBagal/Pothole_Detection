package com.android.citygroom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder>{
    @NonNull

    private Context context;
    private List<NewsArticle> newslist;
    NewsArticle news;

    public NewsAdapter(@NonNull Context context, List<NewsArticle> newslist) {
        this.context = context;
        this.newslist = newslist;
    }

    @Override
    public NewsAdapter.NewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.news_card, viewGroup, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsHolder newsHolder, int i)
    {
        news = newslist.get(i);
        newsHolder.headline.setText(news.headline);
        newsHolder.timestamp.setText(news.timestamp);
    }

    @Override
    public int getItemCount() {

            return newslist.size();

    }

    class NewsHolder extends RecyclerView.ViewHolder {

        TextView headline, timestamp;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);

            headline = itemView.findViewById(R.id.news_headline);
            timestamp = itemView.findViewById(R.id.news_ts);
        }
    }
}

