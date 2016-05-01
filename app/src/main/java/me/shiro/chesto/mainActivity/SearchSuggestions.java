package me.shiro.chesto.mainActivity;

import android.content.Context;
import android.database.MatrixCursor;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.CursorAdapter;

import java.util.List;

import me.shiro.chesto.ChestoApplication;
import me.shiro.chesto.R;
import me.shiro.chesto.danbooruRetrofit.Danbooru;
import me.shiro.chesto.danbooruRetrofit.Tag;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shiro on 3/14/2016.
 * Handles search suggestions
 */
final class SearchSuggestions implements
        SearchView.OnSuggestionListener, SearchView.OnQueryTextListener, Callback<List<Tag>> {

    private static final String TAG = SearchSuggestions.class.getName();
    private static final Handler handler = ChestoApplication.getMainThreadHandler();
    private static final String[] COLUMNS = new String[]{
            "_ID",
            "SUGGEST_COLUMN_TEXT_1"
    };
    private static final int[] VIEWS = new int[]{
            0,
            R.id.tagSuggestion
    };
    private final SearchView searchView;
    private final SimpleCursorAdapter adapter;
    private List<Tag> suggestedTags;
    private String query;
    private Call<List<Tag>> suggestionCall;

    SearchSuggestions(final SearchView searchView, final Context context) {
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
        searchView.setOnSuggestionListener(this);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        final Tag selectedSuggestion = suggestedTags.get(position);
        final String newQuery = searchView.getQuery()
                .toString()
                .replace(query, selectedSuggestion.getName());
        searchView.setQuery(newQuery, false);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (suggestionCall != null) {
            suggestionCall.cancel();
        }
        adapter.changeCursor(null);

        final int spaceIndex = newText.lastIndexOf(" ");
        if (spaceIndex >= 0) {
            query = newText.substring(spaceIndex + 1);
        } else {
            query = newText;
        }

        if (query.length() > 1) {
            suggestionCall = Danbooru.api
                    .searchTags(query + "*");
            suggestionCall.enqueue(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
        final Call<List<Tag>> thisCall = call;

        suggestedTags = response.body();

        handler.post(new Runnable() {
            @Override
            public void run() {
                final MatrixCursor cursor = new MatrixCursor(COLUMNS);
                for (Tag tag : suggestedTags) {
                    cursor.newRow().add(tag.getId()).add(tag.getName());
                }
                if (!thisCall.isCanceled()) {
                    adapter.changeCursor(cursor);
                }
            }
        });
    }

    @Override
    public void onFailure(Call<List<Tag>> call, Throwable t) {
        Log.w(TAG, "Did not fetch tag suggestions", t);
    }
}
