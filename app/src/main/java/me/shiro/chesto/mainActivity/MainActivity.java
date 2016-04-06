package me.shiro.chesto.mainActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

import me.shiro.chesto.PostAdapter;
import me.shiro.chesto.PostList;
import me.shiro.chesto.R;
import me.shiro.chesto.Utils;

public final class MainActivity extends AppCompatActivity {

    private final PostList postList = PostList.getInstance();
    private SwipeRefreshLayout swipeView;
    private RecyclerView recyclerView;
    private MenuItem searchViewItem;
    private Toolbar actionBar;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        Utils.appContext = getApplicationContext();
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final PostAdapter postAdapter = new PostAdapter(postList);
        final GreedoLayoutManager layoutManager = new GreedoLayoutManager(postAdapter);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        postList.registerSwipeRefreshLayout(swipeView);

        swipeView.setColorSchemeColors(R.color.colorPrimaryDark);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postList.refresh();
            }
        });

        final int maxRowHeight = getResources().getDisplayMetrics().heightPixels / 3;
        layoutManager.setMaxRowHeight(maxRowHeight);

        final int spacing = Utils.dpToPx(4);
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);
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
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        swipeView.setRefreshing(true);
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH:
                recyclerView.scrollToPosition(0);
                swipeView.scrollTo(0, 0);
                searchQuery = intent.getStringExtra(SearchManager.QUERY);
                actionBar.setSubtitle(searchQuery);
                postList.searchTags(searchQuery);
                if (searchViewItem != null) {
                    searchViewItem.collapseActionView();
                }
                break;
            default:
                postList.searchTags("");
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
}