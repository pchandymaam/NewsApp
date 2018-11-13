package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper method related to requesting and receiving Alaskan news articles from
 * The Guardian API
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the Guardian dataset and return a list of {@link News} objects
     */
    public static List<News> fetchNewsData(String requestUrl) {

        Log.i(LOG_TAG, "TEST: fetchNewsData() called...");
        //Create URL object
        URL url = createURL(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        //Extract relevant fields from the JSON response and create a list of {@link News) articles
        List<News> articles = extractArticleFromJson(jsonResponse);

        //Return the list of {@link news} articles
        return articles;
    }

    /**
     * Create a private constructor.  This class in only meant to hold static variables and
     * methods, which can be accessed directly from the class name QueryUtils (and an object
     * instance of Query Utils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link News} objects that has been build up from parsing a given JSON
     * response.
     *
     * @param newsJSON
     * @return
     */

    public static List<News> extractArticleFromJson(String newsJSON) {
        //If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        //Create an empty ArrayList that we can start adding news articles to
        List<News> news = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE.  If there is a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown, the exception will be
        // caught, so the app doesn't crash, and and error message will be printed to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONObject associated with the key called "response, which represents
            // a list of news articles.
            JSONObject newsArticles = baseJsonResponse.getJSONObject("response");

            // For a given news article, extract the JSONObject associated with the key called
            // "results", which represents a list of all results for a given article
            JSONArray newsResult =  newsArticles.getJSONArray("results");

            // For each result in the newsArray, create an {@link News} object
            for (int i = 0; i < newsResult.length(); i++) {
                String author = "No Author";

                // Get a single news article at position i within the list of articles.
                JSONObject currentNewsArticle = newsResult.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String section = currentNewsArticle.getString("sectionName");

                // Extract the value for the key called "webTitle"
                String title = currentNewsArticle.getString("webTitle");

                // Extract the value for the key called "webPublicationDate"
                String date = currentNewsArticle.getString("webPublicationDate");

                // Extract the value for the key called "webURL"
                String webURL = currentNewsArticle.getString("webUrl");

                // For a given news article, extract the JSONObject associated with the key called
                // "tags", which represents a list of all tags for a given article.
                JSONArray authorResults = currentNewsArticle.getJSONArray("tags");

                // If there is no author, then display "No Author"
                if (authorResults == null) {
                    author = "No Author";

                    // Else, extract the author's name.
                } else {
                    for (int j = 0; j< authorResults.length(); j++) {
                        // create a new object to display the authors name
                        JSONObject currentTag = authorResults.getJSONObject(j);

                        // Extract the value for the key called "author"
                        author = currentTag.getString("webTitle");
                        Log.v("Author Results", "name: " + author + ", article: " + title);
                    }
                }

                News article = new News(section, title, date, author, webURL);

                news.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // the exception will be caught and the error message will be printed to the log.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of news articles
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the request is successful (response code 200), then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why the
                // makeHttpRequest (URL url) method signature specifies an IOException could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the server
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}