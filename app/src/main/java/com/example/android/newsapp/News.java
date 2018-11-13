package com.example.android.newsapp;

/**
 * an {@link News} object contains information related to a single news article.
 */

public class News {

    /**
     * Name of the section
     */
    private String mSection;

    /**
     * Title of the article
     */
    private String mTitle;

    /**
     * Date of the article.  I still need to convert this from a string to datetime
     */
    private String mDate;

    /**
     * The name of the author
     */
    private String mAuthor;

    /**
     * URL for the article
     */
    private String mWebUrl;

    /**
     * Constructs a new {@link News} object.
     *  @param section   is the name of the section
     * @param title     is the title for article
     * @param date      is the date of the article
     * @param author is the full name of the author
     * @param webUrl
     */
    public News(String section, String title, String date, String author, String webUrl) {
        mSection = section;
        mTitle = title;
        mDate = date;
        mAuthor = author;
        mWebUrl = webUrl;
    }

    /**
     * Returns the name of the section for the article.
     */
    public String getSection() {
        return mSection;
    }

    /**
     * Returns the title for the article.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the date of the article
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the first name of the author
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the URL for the article
     */
    public String getUrl() {
        return mWebUrl;
    }

}
