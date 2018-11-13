package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>>{

    public static final String LOG_TAG = MainActivity.class.getName();

    /** URL for the news data from the Guardian data set */
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?";

    /** Adapter for the list of news articles*/
    private NewsAdapter mAdapter;

    /**
     * Constant value for the news loader ID.  Choose any integer.  This only comes into play if
     * you're missing multiple loaders
     */
    private static final int NEWS_LOADER_ID = 1;

    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        //Create a new adapter that takes a list of news articles as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        //Set the adapter on the {@link ListView} so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected article.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find the current article that was clicked on
                News currentArticle = mAdapter.getItem(position);

                //Convert the String URL into an URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentArticle.getUrl());

                //Create a new intent to view the news URI;
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader.  Pass in the int ID constant defined above and pass in null
            // for the bundle.  Pass in this activity for the LoaderCallbacks parameter (which is
            // valid because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error.  First, hide loading indicator so error message will be
            // visible.
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            //Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences.  The second parameter is the
        // default value for this preference.
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        //getString retrieves a String value from the preferences.  The second parameter is the
        // default value for this preference.
        String sections = sharedPreferences.getString(
                getString(R.string.settings_sections_key),
                getString(R.string.settings_sections_default));

        //parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it.
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("q", "alaska");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("sectionId", sections);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "b83df4b5-4b2d-4cd3-8023-c837f69bd643");

        //Create a new loader for the given URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> articles) {

        //Clear the adapter of previous news data
        mAdapter.clear();

        //get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //Hide loading indicator
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        //Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);

        // If there is a valid list of (@link News} articles, then add them to the adapter's data
        // set.  This will trigger the ListView to update.

        if(networkInfo == null) {
            mEmptyStateTextView.setText(R.string.no_internet);
        } else if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        } else {
            mEmptyStateTextView.setText(R.string.no_news);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        //Clears out out existing data.
        mAdapter.clear();
    }

    @Override
    //This method initializes the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the Options menu we specified in the XML.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
