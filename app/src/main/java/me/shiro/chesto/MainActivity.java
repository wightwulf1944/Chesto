package me.shiro.chesto;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

public final class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final PostList postList = PostList.getInstance();
    private SwipeRefreshLayout swipeView;
    private MenuItem searchViewItem;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
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

        final int spacing = Utils.dpToPx(4, this);
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

        postList.searchTags("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        final String query = intent.getStringExtra(SearchManager.QUERY);
        postList.searchTags(query);
        actionBar.setSubtitle(query);
        searchViewItem.collapseActionView();
        swipeView.scrollTo(0, 0);
        swipeView.setRefreshing(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        searchViewItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}