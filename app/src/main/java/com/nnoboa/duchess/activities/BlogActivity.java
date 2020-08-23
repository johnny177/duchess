package com.nnoboa.duchess.activities;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.controllers.adapters.BlogAdapter;
import com.nnoboa.duchess.controllers.blog.BlogItems;
import com.nnoboa.duchess.controllers.blog.BlogLoader;

import java.util.ArrayList;
import java.util.List;

public class BlogActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<BlogItems>> {

    public static final String LOG_TAG = BlogActivity.class.getSimpleName();
    private static final String BLOG_REQUEST_URL = "https://www.googleapis.com/blogger/v3/blogs/" +
            "5733303841599055017/posts?key=AIzaSyAnVV5Yd1rUk9aQYsR7YfuQu1R6qEHZXfM";
    private static final int BLOG_LOADER_ID = 1;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    boolean isConnected;
    Context context;
    View emptyView;
    ListView listView;
    private BlogAdapter blogAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        context = BlogActivity.this;

        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        listView = findViewById(R.id.blog_list);
        emptyView = findViewById(R.id.blog_empty_view);
        swipeRefreshLayout = findViewById(R.id.refresh_view);
        blogAdapter = new BlogAdapter(context, new ArrayList<BlogItems>());
        listView.setAdapter(blogAdapter);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Uri baseUri = Uri.parse(BLOG_REQUEST_URL);
                Uri.Builder builder = baseUri.buildUpon();

                new BlogLoader(BlogActivity.this, builder.toString());
                Toast.makeText(BlogActivity.this, "Refreshing", Toast.LENGTH_SHORT).show();
                recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlogItems currentBlog = blogAdapter.getItem(position);

                Uri blogUri = Uri.parse(currentBlog.getmUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, blogUri);

                startActivity(websiteIntent);
            }
        });

        android.app.LoaderManager loaderManager = getLoaderManager();

        loaderManager.initLoader(BLOG_LOADER_ID, null, this);


    }

    @Override
    public BlogLoader onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(BLOG_REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        return new BlogLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<BlogItems>> loader, List<BlogItems> data) {
        blogAdapter.clear();
        emptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        if (data != null && !data.isEmpty()) {
            blogAdapter.addAll(data);
            swipeRefreshLayout.setRefreshing(false);
        } else if (!isConnected) {
            listView.setEmptyView(emptyView);
            swipeRefreshLayout.setRefreshing(true);
        } else if (networkInfo.isConnected()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BlogItems>> loader) {
        blogAdapter.clear();
    }


}

