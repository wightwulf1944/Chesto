package me.shiro.chesto;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.shiro.chesto.danbooruRetrofit.Danbooru;
import me.shiro.chesto.danbooruRetrofit.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shiro on 2/26/2016.
 * List class that handles fetching and merging  lists
 */
public final class PostList extends ArrayList<Post> implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = PostList.class.getSimpleName();
    private static final int REQUEST_POST_COUNT = 50;
    private static final android.os.Handler handler = ChestoApplication.getMainThreadHandler();
    private static PostList instance;

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
        Danbooru.api
                .getPosts(tags, ++currentPage)
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        final List<Post> newPostList = response.body();

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

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        Log.e(TAG, "Error fetching more posts", t);
                    }
                });
    }

    @Override
    public void onRefresh() {
        Danbooru.api
                .getPosts(tags, 1)
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        final List<Post> newPostList = response.body();

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

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        Log.e(TAG, "Error trying to refresh posts", t);
                    }
                });
    }

    // Removes posts with no preview url
    private static void filterInvalid(Iterator<Post> i) {
        while (i.hasNext()) {
            if (i.next().getPreviewFileUrl() == null) {
                i.remove();
            }
        }
    }
}
