package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;


public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Constructs the new {@link NewsAdapter}
     *
     * @param context  of the app
     * @param articles is the list of articles, which is the data source of the adapter
     */

    public NewsAdapter(Context context, ArrayList<News> articles) {
        super(context, 0, articles);
    }

    /**
     * Returns a list of item view that displays information about the news article at the given
     * position in the list of articles
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate the new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        }

        //Find the article at the given position in the list of articles
        News currentArticle = getItem(position);

        //Find the TextView with the view ID section
        TextView sectionView = listItemView.findViewById(R.id.section);
        // get the value for the section and save it as a string
        String section = currentArticle.getSection();
        // Display the section name of the current article in the TextView
        sectionView.setText(section);

        //Find the TextView the the view ID title
        TextView titleView = listItemView.findViewById(R.id.title);
        // Get the value for the title and save it as a string
        String title = currentArticle.getTitle();
        //Display the title of the current article in the TextView
        titleView.setText(title);

        //Find the TextView the the view ID date
        TextView dateView = listItemView.findViewById(R.id.date);
        // Get the value for the date and save it as a string
        String date = currentArticle.getDate();
        //Display the date of the current article in the TextView
        dateView.setText(date);

        //Find the TextView the the view ID author
        TextView authorView = listItemView.findViewById(R.id.author);
        // Get the authors first name
        String author = currentArticle.getAuthor();
        // Display the authors full name for the current article in the TextView
        authorView.setText(author);

        //Return the list item view
        return listItemView;
    }
}

