package com.nnoboa.duchess.controllers.blog;

public class BlogItems {

    /**
     * The author of the blog
     */
    private String mAuthor;

    /**
     * The title of the blog
     */
    private String mTitle;

    /**
     * the url of the blog
     */
    private String mUrl;

    /**
     * time of publication
     */
    private String mDatePublished;


    /**
     * @param auth  is the name of the author of the blog
     * @param title is the title of the blog
     * @param url   is the url of the blog post
     * @param time  is the date and time the blog was published
     */

    public BlogItems(String auth, String title, String url, String time) {
        mAuthor = auth;
        mTitle = title;
        mDatePublished = time;
        mUrl = url;
    }

    public BlogItems() {

    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmDatePublished() {
        return mDatePublished;
    }

    public String getmTitle() {
        return mTitle;
    }
}
