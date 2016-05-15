package me.shiro.chesto;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.shiro.chesto.danbooruRetrofit.Danbooru;
import me.shiro.chesto.danbooruRetrofit.Post;
import me.shiro.chesto.postActivity.PostPagerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shiro on 2/26/2016.
 * List class that handles fetching and merging  lists
 * TODO: implement bus pattern for changes
 * TODO: changes will be observed by swiperefresh, postadapter, and postpageradapter
 */
public final class PostList extends ArrayList<Post>
        implements SwipeRefreshLayout.OnRefreshListener, GreedoLayoutSizeCalculator.SizeCalculatorDelegate {

    private static final String TAG = PostList.class.getSimpleName();
    public static final int REQUEST_POST_COUNT = 100;
    private static final Handler handler = ChestoApplication.getMainThreadHandler();
    private static PostList instance;

    private PostAdapter mPostAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PostPagerAdapter mPostPagerAdapter;
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

    public void registerPostAdapter(PostAdapter postAdapter) {
        mPostAdapter = postAdapter;
    }

    public void registerSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    public void registerPostPagerAdapter(PostPagerAdapter postPagerAdapter) {
        mPostPagerAdapter = postPagerAdapter;
    }

    public void searchTags(final String tagSearch) {
        clear();
        currentPage = 0;
        tags = tagSearch;
        requestMorePosts();
    }

    @Override
    public void onRefresh() {
        searchTags(tags);
    }

    // Removes posts with no preview url
    private static void filterInvalid(Iterator<Post> i) {
        while (i.hasNext()) {
            if (i.next().getPreviewFileUrl() == null) {
                i.remove();
            }
        }
    }

    public void requestMorePosts() {
        Danbooru.api
                .getPosts(tags, ++currentPage, REQUEST_POST_COUNT)
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
                                    mPostAdapter.notifyDataSetChanged();
                                    if (mPostPagerAdapter != null) {
                                        mPostPagerAdapter.notifyDataSetChanged();
                                    }
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
                                        mPostAdapter.notifyItemRangeInserted(previousSize, size());
                                        if (mPostPagerAdapter != null) {
                                            mPostPagerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
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
    public double aspectRatioForIndex(int i) {
        if (i >= size()) {
            return 1.0;
        } else {
            Post post = get(i);
            return (double) post.getImageWidth() / post.getImageHeight();
        }
    }
}
