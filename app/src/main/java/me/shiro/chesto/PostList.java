package me.shiro.chesto;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Shiro on 2/26/2016.
 * List class that handles fetching and merging Post lists
 */
public final class PostList extends ArrayList<Post> {

    private static final String TAG = PostList.class.getSimpleName();
    private static PostList instance;
    private static OkHttpClient client;

    private PostAdapter adapter;
    private int currentPage = 0;
    private String tags;

    public static PostList getInstance() {
        if (instance == null) {
            instance = new PostList();
        }
        return instance;
    }

    private PostList() {
        super(Const.REQUEST_POST_COUNT);
        client = new OkHttpClient();
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

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new PostRequestCallback());
    }

    private class PostRequestCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "Error fetching more posts", e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            Reader reader = response.body().charStream();
            JsonReader jsonReader = new JsonReader(reader);
            List<Post> newPostList = PostParser.parsePage(jsonReader);

            if (newPostList != null && !newPostList.isEmpty()) {
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
        }
    }
}
