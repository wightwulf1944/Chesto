package me.shiro.chesto;

import android.net.Uri;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Shiro on 2/24/2016.
 * Restrictive wrapper for Uri.Builder
 */
public final class Danbooru {

    private final Uri.Builder builder;
    private static final OkHttpClient client = new OkHttpClient();

    public Danbooru() {
        builder = new Uri.Builder()
                .scheme("https")
                .authority("danbooru.donmai.us");
    }

    private class Common {
        public void into(Callback callback) {
            Request request = new Request.Builder()
                    .url(builder.toString())
                    .build();

            client.newCall(request).enqueue(callback);
        }
    }

    public Posts posts() {
        builder.appendPath("posts.json");
        return new Posts();
    }

    public final class Posts extends Common {
        public Danbooru.Posts page(final Integer page) {
            builder.appendQueryParameter("page", page.toString());
            return this;
        }

        public Danbooru.Posts limit(final Integer limit) {
            builder.appendQueryParameter("limit", limit.toString());
            return this;
        }

        public Danbooru.Posts tags(String tags) {
            builder.appendQueryParameter("tags", tags.replace(" ", "+"));
            return this;
        }
    }

    public Tags tags() {
        builder.path("tags.json");
        return new Tags();
    }

    public final class Tags extends Common {
        public Danbooru.Tags nameMatches(final String tagName) {
            builder.appendQueryParameter("search[name_matches]", tagName + '*');
            return this;
        }

        public Danbooru.Tags order(final String order) {
            builder.appendQueryParameter("search[order]", order);
            return this;
        }

        public Danbooru.Tags limit(final Integer limit) {
            builder.appendQueryParameter("limit", limit.toString());
            return this;
        }
    }
}
