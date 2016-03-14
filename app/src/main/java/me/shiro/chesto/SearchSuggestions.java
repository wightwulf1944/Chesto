package me.shiro.chesto;

import android.content.Context;
import android.database.MatrixCursor;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.JsonReader;
import android.util.Log;
import android.widget.CursorAdapter;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Shiro on 3/14/2016.
 * Handles search suggestions
 */
public final class SearchSuggestions implements
        SearchView.OnSuggestionListener, SearchView.OnQueryTextListener, Callback {

    private static final String[] COLUMNS = new String[]{
            "_ID",
            "SUGGEST_COLUMN_TEXT_1"
    };
    private static final int[] VIEWS = new int[]{
            0,
            R.id.tagSuggestion
    };
    private static final android.os.Handler handler = new Handler(Looper.getMainLooper());
    private final SearchView searchView;
    private final SimpleCursorAdapter adapter;
    private List<Tag> suggestedTags;

    public SearchSuggestions(final SearchView searchView, final Context context) {
        this.searchView = searchView;
        this.adapter = new SimpleCursorAdapter(
                context,
                R.layout.item_searchview_suggestions,
                null,
                COLUMNS,
                VIEWS,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        searchView.setSuggestionsAdapter(adapter);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        final Tag selectedSuggestion = suggestedTags.get(position);
        searchView.setQuery(selectedSuggestion.tagName, false);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        new Danbooru()
                .tags()
                .nameMatches(newText)
                .order("count")
                .limit(10)
                .into(this);

        return true;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e("TEST", "Error fetching tag suggestions", e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Reader reader = response.body().charStream();
        JsonReader jsonReader = new JsonReader(reader);
        suggestedTags = TagParser.parsePage(jsonReader);
        handler.post(new Runnable() {
            @Override
            public void run() {
                final MatrixCursor cursor = new MatrixCursor(COLUMNS);
                for (Tag tag : suggestedTags) {
                    cursor.newRow().add(tag.id).add(tag.tagName);
                }
                adapter.changeCursor(cursor);
            }
        });
    }
}
