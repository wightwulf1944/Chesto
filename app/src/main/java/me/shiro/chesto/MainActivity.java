package me.shiro.chesto;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

public final class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar progressBar;
    PostList postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        postList = new PostList();
        postList.registerLoadObserver(new LoadObserver());

        final PostAdapter postAdapter = new PostAdapter(postList);

        final GreedoLayoutManager layoutManager = new GreedoLayoutManager(postAdapter);
        layoutManager.setMaxRowHeight(400);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        int spacing = Utils.dpToPx(3, this);
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(spacing));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);

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

    public class LoadObserver {

        public void onPostsLoadStarted() {
            progressBar.setVisibility(View.VISIBLE);
        }

        public void onPostsLoadFinish() {
            progressBar.setVisibility(View.GONE);
        }
    }
}