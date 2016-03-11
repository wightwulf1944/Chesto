package me.shiro.chesto;

import android.net.Uri;

/**
 * Created by Shiro on 2/24/2016.
 * Restrictive wrapper for Uri.Builder
 */
public final class Danbooru {

    private final Uri.Builder builder;

    public Danbooru() {
        builder = new Uri.Builder()
                .scheme("https")
                .authority("danbooru.donmai.us");
    }

    public Posts posts() {
        builder.appendPath("posts.json");
        return new Posts();
    }

    public class Posts {

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

        public String make() {
            return builder.toString();
        }
    }
}
