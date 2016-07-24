package me.shiro.chesto.activitySearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.shiro.chesto.R;
import me.shiro.chesto.retrofitDanbooru.Danbooru;
import me.shiro.chesto.retrofitDanbooru.Tag;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by UGZ on 7/19/2016.
 */
public class SuggestionsAdapter
        extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>
        implements RealmChangeListener<Realm>, Callback<List<Tag>> {

    private static final Realm realm = Realm.getDefaultInstance();
    private static final Danbooru api = new Retrofit.Builder()
            .baseUrl("http://danbooru.donmai.us/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Danbooru.class);
    private final LayoutInflater inflater;
    private RealmResults<Tag> suggestionsList;

    SuggestionsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        suggestionsList = realm.where(Tag.class)
                .findAllSorted("postCount", Sort.DESCENDING);
        realm.addChangeListener(this);
    }

    void setQuery(String s) {
        //  get the last word
        final int wordIndex = s.lastIndexOf(" ");
        if (wordIndex >= 0) {
            s = s.substring(wordIndex + 1);
        }
        suggestionsList = realm.where(Tag.class)
                .beginsWith("name", s, Case.INSENSITIVE)
                .findAllSorted("postCount", Sort.DESCENDING);
        notifyDataSetChanged();
        api.searchTags(s + "*").enqueue(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tag tag = suggestionsList.get(position);
        holder.postCount.setText(tag.getPostCountStr());
        holder.name.setText(tag.getName());
    }

    @Override
    public int getItemCount() {
        return suggestionsList.size();
    }

    @Override
    public void onChange(Realm element) {
        notifyDataSetChanged();
    }

    @Override
    public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(response.body());
        realm.commitTransaction();
        // above lines triggers onChange()
    }

    @Override
    public void onFailure(Call<List<Tag>> call, Throwable t) {
        //TODO:
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView postCount;
        private final TextView name;

        private ViewHolder(View v) {
            super(v);
            postCount = (TextView) v.findViewById(R.id.postCount);
            name = (TextView) v.findViewById(R.id.name);
        }
    }
}