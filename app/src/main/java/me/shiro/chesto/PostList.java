package me.shiro.chesto;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Shiro on 2/26/2016.
 * List class that handles fetching and merging Post lists
 */
public final class PostList extends ArrayList<Post> {

    private static final String TAG = PostList.class.getSimpleName();

    private PostAdapter adapter;
    private int currentPage = 0;
    private String tags;

    public PostList() {
        super(Const.REQUEST_POST_COUNT);
    }

    public void registerPostAdapter(PostAdapter adapter) {
        this.adapter = adapter;
    }

    public void searchTags(final String tagSearch) {
        clear();
        currentPage = 0;
        tags = tagSearch;
        requestMorePosts();
    }

    public void requestMorePosts() {
        String apiUrl =
                new Danbooru()
                        .posts()
                        .page(++currentPage)
                        .limit(Const.REQUEST_POST_COUNT)
                        .tags(tags)
                        .make();
        new GetPostsTask().execute(apiUrl);
    }

    public void onMorePostsRecieved(List<Post> newPostList) {
        // Remove duplicates in new postList
        if (!isEmpty()) {
            final int duplicateIndex = newPostList.indexOf(get(size() - 1));

            if (duplicateIndex >= 0) {
                newPostList.subList(0, duplicateIndex + 1).clear();
            }
        }

        // Remove posts with no preview url
        for (Iterator<Post> i = newPostList.iterator(); i.hasNext(); ) {
            if (i.next().getPreviewFileUrl() == null) {
                i.remove();
            }
        }

        int previousSize = size();
        addAll(newPostList);
        adapter.notifyItemRangeInserted(previousSize, newPostList.size());
    }

    private final class GetPostsTask extends AsyncTask<String, Void, List<Post>> {

        @Override
        protected List<Post> doInBackground(String... params) {
            final String apiUrl = params[0];
            JsonReader jsonReader = null;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                final int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Error in http response: "
                            + responseCode + " - "
                            + urlConnection.getResponseMessage());
                }

                final InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    throw new NullPointerException();
                }

                jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(inputStream)));
            } catch (IOException e) {
                Log.e(TAG, "Error getting json", e);
            }

            return PostParser.parsePage(jsonReader);
        }

        @Override
        protected void onPostExecute(List<Post> newPostList) {
            if (newPostList != null && !newPostList.isEmpty()) {
                onMorePostsRecieved(newPostList);
            }
        }
    }
}
