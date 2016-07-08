package me.shiro.chesto;

import android.databinding.ObservableArrayList;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.shiro.chesto.danbooruRetrofit.DanbooruApi;
import me.shiro.chesto.danbooruRetrofit.Post;
import me.shiro.chesto.events.Event;

/**
 * Created by Shiro on 2/26/2016.
 * List class that handles fetching and merging  lists
 */
public final class PostList extends ObservableArrayList<Post>
        implements SwipeRefreshLayout.OnRefreshListener, GreedoLayoutSizeCalculator.SizeCalculatorDelegate {

    private static final String TAG = PostList.class.getSimpleName();
    private static final EventBus eventBus = EventBus.getDefault();
    private static PostList instance;

    private int currentPage = 0;
    private String tags;

    public static PostList getInstance() {
        if (instance == null) {
            instance = new PostList();
        }
        return instance;
    }

    private PostList() {
        // Disable creation
    }

    public void newSearch(final String tagSearch) {
        eventBus.post(new Event.PostsLoading());
        currentPage = 0;
        tags = tagSearch;
        clear();
        eventBus.post(new Event.PostListRefreshed());
        requestMorePosts();
    }

    @Override
    public void onRefresh() {
        newSearch(tags);
    }

    public void requestMorePosts() {
        DanbooruApi.getPosts(tags, ++currentPage);
    }

    public void onReceiveMorePosts(List<Post> newPostList) {
        if (!newPostList.isEmpty()) {
            newPostList.removeAll(this);
            final int positionStart = size();
            final int itemCount = newPostList.size();
            addAll(newPostList);
            Event.PostListUpdated event = new Event.PostListUpdated(positionStart, itemCount);
            eventBus.post(event);
        }

        eventBus.post(new Event.PostsLoadingFinished());
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
