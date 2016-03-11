package me.shiro.chesto;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private static final int REQUEST_POST_COUNT = 50;
    private static PostList instance;
    private static OkHttpClient client;
    private static final android.os.Handler handler = new Handler(Looper.getMainLooper());

    private PostAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentPage = 0;
    private String tags;

    public static PostList getInstance() {
        if (instance == null) {
            instance = new PostList();
        }
        return instance;
    }

    private PostList() {
        super(REQUEST_POST_COUNT);
        client = new OkHttpClient();
    }

    public void registerPostAdapter(PostAdapter adapter) {
        this.adapter = adapter;
    }

    public void registerSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
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
                        .limit(REQUEST_POST_COUNT)
                        .tags(tags)
                        .make();

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new RequestMorePosts());
    }

    private class RequestMorePosts implements Callback {

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

            if (isEmpty()) {
                if (!newPostList.isEmpty()) {
                    filterInvalid(newPostList.iterator());
                    addAll(newPostList);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            } else if (!newPostList.isEmpty()) {
                // Remove duplicates in new postList
                final int duplicateIndex = newPostList.indexOf(get(size() - 1));
                if (duplicateIndex >= 0) {
                    newPostList.subList(0, duplicateIndex + 1).clear();
                }

                if (!newPostList.isEmpty()) {
                    filterInvalid(newPostList.iterator());
                    final int previousSize = size();
                    addAll(newPostList);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemRangeInserted(previousSize, size());
                        }
                    });
                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    public void refresh() {
        String apiUrl =
                new Danbooru()
                        .posts()
                        .page(1)
                        .limit(REQUEST_POST_COUNT)
                        .tags(tags)
                        .make();

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Refresh());
    }

    private class Refresh implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "Error trying to refresh posts", e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            final Reader reader = response.body().charStream();
            final JsonReader jsonReader = new JsonReader(reader);
            final List<Post> newPostList = PostParser.parsePage(jsonReader);

            filterInvalid(newPostList.iterator());

            if (!isEmpty() && !newPostList.isEmpty()) {
                final int duplicateIndex = indexOf(newPostList.get(newPostList.size() - 1));
                if (duplicateIndex >= 0) {
                    subList(0, duplicateIndex + 1).clear();
                }
            }

            if (!newPostList.isEmpty()) {
                addAll(0, newPostList);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemRangeInserted(0, newPostList.size());
                    }
                });
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    // Removes posts with no preview url
    private void filterInvalid(Iterator<Post> i) {
        while (i.hasNext()) {
            if (i.next().getPreviewFileUrl() == null) {
                i.remove();
            }
        }
    }
}
