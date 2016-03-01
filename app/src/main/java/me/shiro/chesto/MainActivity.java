package me.shiro.chesto;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

public final class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final PostList postList = PostList.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
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


        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int maxRowHeight = metrics.heightPixels / 3;
        layoutManager.setMaxRowHeight(maxRowHeight);

        final int spacing = Utils.dpToPx(4, this);
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                final boolean isAtTop = recyclerView.getChildAt(0).getTop() == spacing
                        && layoutManager.findFirstVisibleItemPosition() == 0;
                swipeView.setEnabled(isAtTop);
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        switch (intent.getAction()) {

            case Intent.ACTION_MAIN:
                postList.searchTags("");
                break;

            case Intent.ACTION_SEARCH:
                postList.searchTags(intent.getStringExtra(SearchManager.QUERY));
                break;

            default:
                Log.w(TAG, "Unhandled intent: " + intent.getAction());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}