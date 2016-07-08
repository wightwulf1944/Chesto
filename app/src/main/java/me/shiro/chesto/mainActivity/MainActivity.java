package me.shiro.chesto.mainActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;
import com.github.nitrico.lastadapter.LastAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import me.shiro.chesto.BR;
import me.shiro.chesto.PostList;
import me.shiro.chesto.R;
import me.shiro.chesto.Utils;
import me.shiro.chesto.danbooruRetrofit.Post;
import me.shiro.chesto.events.Event;
import me.shiro.chesto.postActivity.PostActivity;

public final class MainActivity extends AppCompatActivity implements LastAdapter.OnBindListener, LastAdapter.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final EventBus eventBus = EventBus.getDefault();
    private final PostList postList = PostList.getInstance();
    private AppBarLayout appBar;
    private MenuItem searchViewItem;
    private Toolbar actionBar;
    private SwipeRefreshLayout swipeView;
    private RecyclerView recyclerView;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus.register(this);
        setContentView(R.layout.activity_main);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        actionBar = (Toolbar) findViewById(R.id.actionBar);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        setSupportActionBar(actionBar);

        swipeView.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        swipeView.setOnRefreshListener(postList);

        final GreedoLayoutManager layoutManager = new GreedoLayoutManager(postList);
        final int maxRowHeight = getResources().getDisplayMetrics().heightPixels / 3;
        layoutManager.setMaxRowHeight(maxRowHeight);

        final LastAdapter adapter = LastAdapter.with(postList, BR.item)
                .map(Post.class, R.layout.item_recyclerview_post)
                .onBindListener(this)
                .onClickListener(this)
                .build();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final int spacing = Utils.dpToPx(4);
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                View topChild = recyclerView.getChildAt(0);
                if (topChild != null) {
                    swipeView.setEnabled(
                            topChild.getTop() == spacing
                                    && layoutManager.findFirstVisibleItemPosition() == 0
                    );
                }
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        appBar.setExpanded(true);
        recyclerView.scrollToPosition(0);
        swipeView.scrollTo(0, 0);
        searchViewItem.collapseActionView();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH:
                searchQuery = intent.getStringExtra(SearchManager.QUERY);
                actionBar.setSubtitle(searchQuery);
                postList.newSearch(searchQuery);
                break;
            default:
                postList.newSearch("");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        searchViewItem = menu.findItem(R.id.search);

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchViewItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!searchViewItem.isActionViewExpanded()) {
                    searchViewItem.expandActionView();
                    if (searchQuery != null) {
                        searchView.setQuery(searchQuery, false);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        new SearchSuggestions(searchView, this);
        return true;
    }

    @Subscribe
    public void onSwipeRefreshEvent(Event.PostsLoading event) {
        swipeView.setRefreshing(true);
    }

    @Subscribe
    public void onSwipeNotRefreshingEvent(Event.PostsLoadingFinished event) {
        swipeView.setRefreshing(false);
    }

    @Override
    public void onBind(@NotNull Object o, @NotNull View view, int i) {
        if (i == postList.size() - 5) {
            postList.requestMorePosts();
        }

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view;
        simpleDraweeView.setImageURI(postList.get(i).getPreviewFileUrl());
    }

    @Override
    public void onClick(@NotNull Object o, @NotNull View view, int i) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra(PostActivity.POST_INDEX, i);
        startActivity(intent);
    }
}